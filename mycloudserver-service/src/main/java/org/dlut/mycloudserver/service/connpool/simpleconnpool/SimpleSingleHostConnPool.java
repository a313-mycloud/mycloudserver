/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool.simpleconnpool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.ISingleHostConnPool;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.util.LibvirtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类SimpleSingleHostConnPool.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月26日 下午3:34:24
 */
public class SimpleSingleHostConnPool implements ISingleHostConnPool {

    private static Logger    log                  = LoggerFactory.getLogger(SimpleSingleHostConnPool.class);

    /**
     * 创建一个新的连接的超时时间，单位为s
     */
    private static final int CREATE_CONN_TIME_OUT = 1;

    /**
     * 连接到host的libvirt的url
     */
    private String           hostConnUrl;

    /**
     * 存放连接的list
     */
    private List<Connection> connList             = new LinkedList<Connection>();

    /**
     * 初始化时的连接数
     */
    private int              initConnNum;

    /**
     * 最大的连接数
     */
    private int              maxConnNum;

    /**
     * 已创建的连接数
     */
    private int              hasCreatedConnNum;

    public SimpleSingleHostConnPool(String hostConnUrl, int initConnNum, int maxConnNum) {
        this.hostConnUrl = hostConnUrl;
        this.initConnNum = initConnNum;
        this.maxConnNum = maxConnNum;
        for (int i = 0; i < this.initConnNum; i++) {
            Connection conn = this.createNewConn();
            if (conn != null) {
                connList.add(conn);
            } else {
                break;
            }
        }
        hasCreatedConnNum = connList.size();
        log.info(hostConnUrl + "的连接池初始化成功，初始化的连接数为：" + hasCreatedConnNum);
    }

    @Override
    public synchronized Connection getConn() {
        if (!connList.isEmpty()) {
            // 如果池中存在连接，则直接返回
            return connList.remove(0);
        }
        // 如果池中不存在连接，则判断已创建的连接数是否超过设置的最大连接数，如果未超过，则创建一个新的连接，否则，返回空
        if (hasCreatedConnNum < maxConnNum) {
            Connection conn = this.createNewConn();
            if (conn != null) {
                hasCreatedConnNum++;
                return conn;
            }
        }
        return null;
    }

    /**
     * 将连接放回池中，如果池中的连接数小于最大连接数，则放入池中，否则直接关闭连接
     * 
     * @param conn
     */
    public synchronized void putConn(Connection conn) {
        if (conn instanceof SimpleConnction) {
            SimpleConnction simpleConnction = (SimpleConnction) conn;
            try {
                if (simpleConnction.getLibvirtConn().isConnected()) {
                    connList.add(conn);
                    return;
                }
                // 如果未连接，则hasCreatedConnNum减一
                hasCreatedConnNum--;
            } catch (LibvirtException e) {
                log.error("error message", e);
                hasCreatedConnNum--;
            }
        }
    }

    /**
     * 创建一个新的连接
     * 
     * @return
     */
    private Connection createNewConn() {
        Connect conn = LibvirtUtil.createLibvirtConn(hostConnUrl, CREATE_CONN_TIME_OUT, TimeUnit.SECONDS);
        if (conn != null) {
            return new SimpleConnction(conn, this);
        } else {
            log.error(hostConnUrl + " 创建新的连接失败");
            return null;
        }
    }

}
