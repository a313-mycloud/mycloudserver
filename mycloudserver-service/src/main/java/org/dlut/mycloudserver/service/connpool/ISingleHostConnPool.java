/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool;

/**
 * 单物理机libvirt连接池
 * 
 * @author luojie 2014年11月26日 下午3:32:50
 */
public interface ISingleHostConnPool {

    public Connection getConn();
}
