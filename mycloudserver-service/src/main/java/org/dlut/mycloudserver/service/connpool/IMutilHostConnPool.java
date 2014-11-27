/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool;

/**
 * 多节点连接池接口
 * 
 * @author luojie 2014年11月26日 下午2:51:03
 */
public interface IMutilHostConnPool {

    /**
     * 获取本地libvirt连接
     * 
     * @return
     */
    public Connection getLocalConn();

    /**
     * 根据物理机id获取该物理机的libvirt连接
     * 
     * @param hostId
     * @return
     */
    public Connection getConnByHostId(int hostId);
}
