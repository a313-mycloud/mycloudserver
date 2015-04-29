/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.performancemonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorDTO;
import org.dlut.mycloudserver.client.common.performancemonitor.PerformanceMonitorStatusEnum;
import org.dlut.mycloudserver.client.common.performancemonitor.QueryPerformanceMonitorCondition;
import org.dlut.mycloudserver.client.service.performancemonitor.IPerformanceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 类PerformanceListener.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午8:12:26
 */
@Service
public class PerformanceListener implements Runnable {

    private static Logger                       log           = LoggerFactory.getLogger(PerformanceListener.class);

    private Map<Integer, PerformanceMonitorDTO> monitorDTOMap;

    /**
     * 连接超时的时间，单位ms
     */
    private static final int                    CONN_TIME_OUT = 1000;

    /**
     * 读取数据超时的时间，单位ms
     */
    private static final int                    READ_TIME_OUT = 1000;

    @Resource(name = "performanceMonitorService")
    private IPerformanceMonitorService          performanceMonitorService;

    public PerformanceListener() {
        monitorDTOMap = new HashMap<Integer, PerformanceMonitorDTO>();
    }

    public void execute() {
        // 开启一个后台线程用来监控主机的性能
        new Thread(this).start();
        log.info("后台性能监控线程开启成功");
    }

    @Override
    public void run() {
        while (true) {
            try {
                monitor();
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 对所有注册了的主机进行一次性能监控
     */
    private void monitor() {
        // 刷新monitorConnMap
        refreshMonitorConnMap();

        // 采集每个主机的性能数据
        for (Integer id : monitorDTOMap.keySet()) {
            monitorForOne(monitorDTOMap.get(id));
        }
    }

    private void monitorForOne(PerformanceMonitorDTO performanceMonitorDTO) {
        String url = "http://" + performanceMonitorDTO.getIp() + ":8001";
        InputStream is = null;
        BufferedReader br = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONN_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String res = br.readLine();
            br.close();
            is.close();
            JSONObject jsonRes = JSON.parseObject(res);
            int cores = jsonRes.getIntValue("cores");
            int totalMem = jsonRes.getIntValue("totalMem");
            int usedMem = jsonRes.getIntValue("usedMem");
            double loadAverage = jsonRes.getDoubleValue("loadAverage");
            double sendRate = jsonRes.getDoubleValue("sendRate");
            double receiveRate = jsonRes.getDoubleValue("receiveRate");
            performanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum.RUNNING);
            performanceMonitorDTO.setCores(cores);
            performanceMonitorDTO.setTotalMem(totalMem);
            performanceMonitorDTO.setUsedMem(usedMem);
            performanceMonitorDTO.setLoadAverage(loadAverage);
            performanceMonitorDTO.setSendRate(sendRate);
            performanceMonitorDTO.setReceiveRate(receiveRate);
        } catch (IOException e) {
            log.error("error message", e);
            performanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum.CLOSED);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("error message", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("error message", e);
                }
            }
        }

        // 将最新数据写入数据库
        MyCloudResult<Boolean> res = performanceMonitorService.updatePerformanceMonitor(performanceMonitorDTO);
        if (!res.isSuccess()) {
            log.error("更新性能监控数据失败，原因：" + res.getMsgInfo());
        }
    }

    /**
     * 从数据库中获取注册的性能监控主机列表
     * 
     * @return
     */
    private Map<Integer, PerformanceMonitorDTO> getPerformanceMonitorMapFromDB() {
        Map<Integer, PerformanceMonitorDTO> performanceMonitorMap = new HashMap<Integer, PerformanceMonitorDTO>();
        QueryPerformanceMonitorCondition queryPerformanceMonitorCondition = new QueryPerformanceMonitorCondition();
        queryPerformanceMonitorCondition.setOffset(0);
        queryPerformanceMonitorCondition.setLimit(1000);
        MyCloudResult<Pagination<PerformanceMonitorDTO>> res = performanceMonitorService
                .query(queryPerformanceMonitorCondition);
        if (!res.isSuccess()) {
            log.error("在数据库中查询注册的性能监控主机列表失败，原因：" + res.getMsgInfo());
            return performanceMonitorMap;
        }
        for (PerformanceMonitorDTO performanceMonitorDTO : res.getModel().getList()) {
            performanceMonitorMap.put(performanceMonitorDTO.getId(), performanceMonitorDTO);
        }
        return performanceMonitorMap;
    }

    /**
     * 刷新monitorConnMap
     */
    private void refreshMonitorConnMap() {
        Map<Integer, PerformanceMonitorDTO> performanceMonitorMapDB = getPerformanceMonitorMapFromDB();

        Iterator<Map.Entry<Integer, PerformanceMonitorDTO>> it = monitorDTOMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, PerformanceMonitorDTO> entry = it.next();
            Integer id = entry.getKey();
            if (!performanceMonitorMapDB.containsKey(id)) {
                it.remove();
            } else {
                performanceMonitorMapDB.remove(id);
            }
        }

        // 新增加的需要监听的主机
        for (Integer id : performanceMonitorMapDB.keySet()) {
            monitorDTOMap.put(id, performanceMonitorMapDB.get(id));
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        String url = "http://127.0.0.1:8001";
        URLConnection conn;
        try {
            conn = new URL(url).openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String res = br.readLine();
            br.close();
            is.close();
            JSONObject jsonRes = JSON.parseObject(res);
            double loadAverage = jsonRes.getDoubleValue("loadAverage");
            System.out.println(loadAverage);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();

        System.out.println((t2 - t1) + "ms");
    }
}
