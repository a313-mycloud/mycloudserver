/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.libvirt.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类LibvirtUtil.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月30日 下午6:00:13
 */
public class LibvirtUtil {

    private static Logger log = LoggerFactory.getLogger(LibvirtUtil.class);

    public static Connect createLibvirtConn(final String url, int timeOut, TimeUnit timeUnit) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Connect> future = executorService.submit(new Callable<Connect>() {

            @Override
            public Connect call() throws Exception {
                Connect conn = new Connect(url);
                if (conn.isConnected()) {
                    log.info(url + " 创建连接成功");
                    return conn;
                }
                log.info(url + " 创建连接失败");
                return null;
            }

        });
        try {
            Connect conn = future.get(timeOut, timeUnit);
            return conn;
        } catch (Exception e) {
            return null;
        } finally {
            executorService.shutdown();
        }
    }
}
