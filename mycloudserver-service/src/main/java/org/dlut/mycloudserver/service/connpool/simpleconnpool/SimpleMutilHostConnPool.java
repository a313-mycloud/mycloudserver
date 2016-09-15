/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool.simpleconnpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.dal.dataobject.HostDO;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.connpool.ISingleHostConnPool;
import org.mycloudserver.common.util.LibvirtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类SimpleMutilHostConnPool.java的实现描述：TODO 类实现描述
 *
 * @author luojie 2014年11月26日 下午3:39:13
 */
public class SimpleMutilHostConnPool implements IMutilHostConnPool {

    private static Logger log = LoggerFactory.getLogger(SimpleMutilHostConnPool.class);

    /**
     * 本地连接池
     */
    private ISingleHostConnPool localHostConnPool;

    private static Map<Integer, ISingleHostConnPool> remoteMutilHostConnPoolMap;

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;

    /**
     * 每个单节点连接池初始的连接数
     */
    private int initConnNum;

    /**
     * 每个单节点最大的连接数
     */
    private int maxConnNum;

    /**
     * 本地物理机连接url
     */
    private static final String LOCAL_HOST_URL = "qemu:///system";

    public SimpleMutilHostConnPool(int initConnNum, int maxConnNum) {
        this.initConnNum = initConnNum;
        this.maxConnNum = maxConnNum;
        localHostConnPool = new SimpleSingleHostConnPool(LOCAL_HOST_URL, this.initConnNum, this.maxConnNum);
        remoteMutilHostConnPoolMap = new ConcurrentHashMap<Integer, ISingleHostConnPool>();
    }

    @Override
    public Connection getLocalConn() {
        return localHostConnPool.getConn();
    }

    @Override
    public Connection getConnByHostId(int hostId) {
        ISingleHostConnPool singleHostConnPool = remoteMutilHostConnPoolMap.get(hostId);
        if (singleHostConnPool == null) {
            return null;
        }
        return singleHostConnPool.getConn();
    }

    /**
     * 定期更新连接池
     */
    public void updateRemoteMutilHostConnPoolMap() {
        Map<Integer,HostDTO>  hostList =getHostFromDB();
        HashMap<Integer, Integer> lossCounts = new HashMap<Integer, Integer>();
        for (int i = 0; i < 3; i++) {
            for (Map.Entry<Integer,HostDTO> entry : hostList.entrySet()) {
                if (!lossCounts.containsKey(entry.getKey()))
                    lossCounts.put(entry.getKey(), 0);
                if (!LibvirtUtil.canConnect(entry.getValue().getHostIp(), 1, TimeUnit.SECONDS))
                    lossCounts.put(entry.getKey(), lossCounts.get(entry.getKey()) + 1);
            }
        }

        for (Map.Entry<Integer, Integer> lossCount : lossCounts.entrySet()) {
            if (lossCount.getValue() > 2 && remoteMutilHostConnPoolMap.containsKey(lossCount.getKey())) {
                remoteMutilHostConnPoolMap.remove(lossCount.getKey());
            } else if (lossCount.getValue()<=2 && !remoteMutilHostConnPoolMap.containsKey(lossCount.getKey())) {
                String hostConnUrl = "qemu+tcp://" + hostList.get(lossCount.getKey()).getHostIp() + "/system";
                ISingleHostConnPool singleHostConnPool = new SimpleSingleHostConnPool(hostConnUrl, initConnNum, maxConnNum);
                remoteMutilHostConnPoolMap.put(lossCount.getKey(), singleHostConnPool);
            }
        }
    }


    private Map<Integer,HostDTO> getHostFromDB(){
        Map<Integer,HostDTO> allHost=new HashMap<Integer,HostDTO>();
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setLimit(1000);
        queryHostCondition.setOffset(0);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取物理机列表失败，原因：" + result.getMsgInfo());
            return allHost;
        }
        for(HostDTO hostDTO:result.getModel().getList()){
            allHost.put(hostDTO.getHostId(),hostDTO);
        }
        return allHost;
    }

    public static Map<Integer, ISingleHostConnPool> getRemoteMutilHostConnPoolMap() {
        return remoteMutilHostConnPoolMap;
    }
}
