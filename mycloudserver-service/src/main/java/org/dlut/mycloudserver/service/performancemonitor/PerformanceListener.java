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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 类PerformanceListener.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月25日 下午8:12:26
 */
@Service
public class PerformanceListener implements Runnable {

    private static Logger                       log     = LoggerFactory.getLogger(PerformanceListener.class);

    /**
     * 存储已建立ssh连接的conn
     */
    private Map<Integer, Connection>            monitorConnMap;

    private Map<Integer, PerformanceMonitorDTO> monitorDTOMap;

    private static final int                    TIMEOUT = 5000;

    @Resource(name = "performanceMonitorService")
    private IPerformanceMonitorService          performanceMonitorService;

    public PerformanceListener() {
        monitorConnMap = new HashMap<Integer, Connection>();
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
                Thread.sleep(1000);
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
        ExecutorService executorService = Executors.newFixedThreadPool(monitorConnMap.size());
        for (Integer id : monitorConnMap.keySet()) {
            executorService.execute(new MonitorForOneTask(id, monitorConnMap.get(id), monitorDTOMap.get(id)));
        }
        executorService.shutdown();

        try {
            boolean end = true;
            do {
                end = executorService.awaitTermination(10, TimeUnit.SECONDS);
            } while (!end);
        } catch (InterruptedException e) {
            log.error("error message", e);
        }
    }

    private class MonitorForOneTask implements Runnable {

        private int                   id;

        private Connection            conn;

        private PerformanceMonitorDTO monitorDTO;

        public MonitorForOneTask(int id, Connection conn, PerformanceMonitorDTO monitorDTO) {
            this.id = id;
            this.conn = conn;
            this.monitorDTO = monitorDTO;
        }

        @Override
        public void run() {
            monitorForOne(id, conn, monitorDTO);
        }

    }

    /**
     * 对一台主机进行一次性能监控，并将结果写入数据库
     * 
     * @param id
     * @param conn
     */
    private void monitorForOne(int id, Connection conn, PerformanceMonitorDTO monitorDTO) {
        PerformanceMonitorDTO performanceMonitorDTO = new PerformanceMonitorDTO();
        performanceMonitorDTO.setId(id);
        performanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum.RUNNING);

        // 获取总的核心数
        try {
            int totalCores = getCpuCores(conn);
            performanceMonitorDTO.setCores(totalCores);
        } catch (IOException e) {
            log.warn(e.getMessage());
            performanceMonitorDTO.setCores(-1);
        }

        // 获取总的内存大小
        try {
            int totalMem = getTotalMem(conn);
            performanceMonitorDTO.setTotalMem(totalMem);
        } catch (IOException e) {
            log.warn("error message", e);
            performanceMonitorDTO.setTotalMem(-1);
        }

        // 获取已使用的内存数量
        try {
            int usedMem = getUsedMem(conn);
            performanceMonitorDTO.setUsedMem(usedMem);
        } catch (IOException e) {
            log.warn("error message", e);
            performanceMonitorDTO.setUsedMem(-1);
        }

        // 获取平均负载值
        try {
            double loadAverage = getLoadAverage(conn);
            performanceMonitorDTO.setLoadAverage(loadAverage);
        } catch (IOException e) {
            log.error("error message", e);
            performanceMonitorDTO.setLoadAverage(-1.0);
        }

        // 获取网络的发送速率和接收速率
        try {
            double[] rates = getSendAndReceiveRate(conn, monitorDTO.getInterfaceName());
            performanceMonitorDTO.setReceiveRate(rates[0]);
            performanceMonitorDTO.setSendRate(rates[1]);
        } catch (IOException e) {
            log.warn("error message", e);
            performanceMonitorDTO.setReceiveRate(-1.0);
            performanceMonitorDTO.setSendRate(-1.0);
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

        Iterator<Map.Entry<Integer, Connection>> it = monitorConnMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Connection> entry = it.next();
            Integer id = entry.getKey();
            if (!performanceMonitorMapDB.containsKey(id)) {
                Connection conn = entry.getValue();
                it.remove();
                monitorDTOMap.remove(id);
                conn.close();
            } else {
                performanceMonitorMapDB.remove(id);
            }
        }

        // 新增加的需要监听的主机
        for (Integer id : performanceMonitorMapDB.keySet()) {
            PerformanceMonitorDTO performanceMonitorDTO = performanceMonitorMapDB.get(id);
            try {
                Connection conn = createConn(performanceMonitorDTO.getIp(), performanceMonitorDTO.getSshUserName(),
                        performanceMonitorDTO.getSshPassword(), TIMEOUT);
                monitorConnMap.put(id, conn);
                monitorDTOMap.put(id, performanceMonitorDTO);
            } catch (IOException e) {
                // 连接失败,更新数据库
                log.warn("error message", e);
                PerformanceMonitorDTO updatePerformanceMonitorDTO = new PerformanceMonitorDTO();
                updatePerformanceMonitorDTO.setId(id);
                updatePerformanceMonitorDTO.setPerformanceMonitorStatus(PerformanceMonitorStatusEnum.CLOSED);
                MyCloudResult<Boolean> res = performanceMonitorService
                        .updatePerformanceMonitor(updatePerformanceMonitorDTO);
                if (!res.isSuccess()) {
                    log.error("更新PerformanceMonitorDTO失败，原因：" + res.getMsgInfo());
                }
            }
        }
    }

    /**
     * 创建一个新的ssh连接
     * 
     * @param ip
     * @param userName
     * @param password
     * @param timeOut
     * @return
     * @throws IOException
     */
    private Connection createConn(String ip, String userName, String password, int timeOut) throws IOException {
        Connection conn = new Connection(ip);
        conn.connect(null, timeOut, timeOut);
        boolean isAuthenticated = conn.authenticateWithPassword(userName, password);
        if (!isAuthenticated) {
            String errMesg = "ip:" + ip + "   username:" + userName + "    password:" + password + "     验证失败";
            log.warn(errMesg);
            throw new IOException(errMesg);
        }
        return conn;
    }

    //    private double getSystemUsage(Connection conn) throws IOException {
    //        String command = "top -n 1 |grep Cpu | cut -d \",\" -f 2 | cut -d \" \" -f 3";
    //        String res = execCommand(conn, command);
    //        double systemUsage = Double.valueOf(res);
    //        return systemUsage;
    //    }

    /**
     * @param conn
     * @return 第0个元素为接收速率，第1个元素为发送速率
     * @throws IOException
     */
    private double[] getSendAndReceiveRate(Connection conn, String interfaceName) throws IOException {
        String command = "nicstat -i " + interfaceName + " 1 2 | tail -1 | awk '{print $3\" \"$4}'";
        String res = execCommand(conn, command);
        String[] strRates = res.split(" ");
        if (strRates.length != 2) {
            throw new IOException("读取网络速度失败，原因：" + res);
        }
        try {
            double[] rates = new double[2];
            rates[0] = Double.valueOf(strRates[0]);
            rates[1] = Double.valueOf(strRates[1]);
            return rates;
        } catch (NumberFormatException e) {
            throw new IOException("读取网络速度失败");
        }
    }

    /**
     * 获取cpu核心数
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    private int getCpuCores(Connection conn) throws IOException {
        String command = "grep 'processor' /proc/cpuinfo | sort -u | wc -l";
        String res = execCommand(conn, command);
        try {
            int totalCores = Integer.valueOf(res);
            return totalCores;
        } catch (NumberFormatException e) {
            throw new IOException("获取cpu核心数失败");
        }
    }

    /**
     * 获取总的内存大小，单位为MB
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    private int getTotalMem(Connection conn) throws IOException {
        String command = "free -m | tail -3 | head -1 | awk '{print $2}'";
        String res = execCommand(conn, command);
        try {
            int totalMem = Integer.valueOf(res);
            return totalMem;
        } catch (NumberFormatException e) {
            throw new IOException("获取总内存大小失败");
        }
    }

    /**
     * 获取已使用内存的大小，单位为MB
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    private int getUsedMem(Connection conn) throws IOException {
        String command = "free -m | tail -2 | head -1 | awk '{print $3}'";
        String res = execCommand(conn, command);
        try {
            int usedMem = Integer.valueOf(res);
            return usedMem;
        } catch (NumberFormatException e) {
            throw new IOException("获取已使用内存大小失败");
        }
    }

    /**
     * 获取系统平均负载值
     * 
     * @param conn
     * @return
     * @throws IOException
     */
    private double getLoadAverage(Connection conn) throws IOException {
        String command = "cat /proc/loadavg | awk '{print $2}'";
        String res = execCommand(conn, command);
        try {
            double loadAverage = Double.valueOf(res);
            return loadAverage;
        } catch (NumberFormatException e) {
            throw new IOException("获取平均负载值失败");
        }
    }

    /**
     * 获取用户空间的cpu使用率和内核空间的cpu使用率
     * 
     * @param conn
     * @return 第0个元素为用户空间的cpu使用率，第1个元素为内核空间的cpu使用率
     * @throws IOException
     */
    //    private double[] getUserAndSysUsage(Connection conn) throws IOException {
    //        String command = "top -n 1 | grep Cpu | cut -d ':' -f 2 | awk -F ',' '{print $1$2}' | awk '{print $1\" \"$3}'";
    //        String res = execCommand(conn, command);
    //        System.out.println(res);
    //        String[] strUsages = res.split(" ");
    //        if (strUsages.length != 2) {
    //            throw new IOException("获取cpu使用率失败");
    //        }
    //        try {
    //            double[] usages = new double[2];
    //            usages[0] = Double.valueOf(strUsages[0]);
    //            usages[1] = Double.valueOf(strUsages[1]);
    //            return usages;
    //        } catch (NumberFormatException e) {
    //            throw new IOException("获取cpu使用率失败");
    //        }
    //    }

    private String execCommand(Connection conn, String command) throws IOException {
        Session session = conn.openSession();
        session.requestDumbPTY();
        session.execCommand(command);
        //        session.waitForCondition(ChannelCondition.EXIT_SIGNAL, 0);
        InputStream stdout = new StreamGobbler(session.getStdout());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
        String line = br.readLine();
        br.close();
        stdout.close();
        session.close();
        return line;
    }
}
