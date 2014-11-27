/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool.simpleconnpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.connpool.ISingleHostConnPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类SimpleMutilHostConnPool.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月26日 下午3:39:13
 */
@Service("mutilHostConnPool")
public class SimpleMutilHostConnPool implements IMutilHostConnPool {

    private static Logger                     log            = LoggerFactory.getLogger(SimpleMutilHostConnPool.class);

    /**
     * 本地连接池
     */
    private ISingleHostConnPool               localHostConnPool;

    private Map<Integer, ISingleHostConnPool> remoteMutilHostConnPoolMap;

    @Resource(name = "hostManageService")
    private IHostManageService                hostManageService;

    /**
     * 每个单节点连接池初始的连接数
     */
    private int                               initConnNum;

    /**
     * 每个单节点最大的连接数
     */
    private int                               maxConnNum;

    /**
     * 本地物理机连接url
     */
    private static final String               LOCAL_HOST_URL = "qemu:///system";

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
        return null;
    }

    /**
     * 定期更新连接池
     */
    public void updateRemoteMutilHostConnPoolMap() {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setLimit(100);
        queryHostCondition.setOffset(0);
        queryHostCondition.setHostStatusEnum(HostStatusEnum.RUNNING);
        // 从数据库中获取运行中的物理机列表
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取物理机列表失败，原因：" + result.getMsgInfo());
            return;
        }
        Map<Integer, HostDTO> runningHostMap = new HashMap<Integer, HostDTO>();
        for (HostDTO hostDTO : result.getModel().getList()) {
            runningHostMap.put(hostDTO.getHostId(), hostDTO);
        }

        // 将连接池中那些对应的物理机已关闭的连接池从remoteMutilHostConnPoolMap中删除
        for (Integer hostId : remoteMutilHostConnPoolMap.keySet()) {
            if (!runningHostMap.containsKey(hostId)) {
                remoteMutilHostConnPoolMap.remove(hostId);
            } else {
                runningHostMap.remove(hostId);
            }
        }

        // 对新增的物理机创建连接池
        for (Integer hostId : runningHostMap.keySet()) {
            String hostConnUrl = "qemu+tcp://" + runningHostMap.get(hostId).getHostIp() + "/system";
            ISingleHostConnPool singleHostConnPool = new SimpleSingleHostConnPool(hostConnUrl, initConnNum, maxConnNum);
            remoteMutilHostConnPoolMap.put(hostId, singleHostConnPool);
        }
    }
}
