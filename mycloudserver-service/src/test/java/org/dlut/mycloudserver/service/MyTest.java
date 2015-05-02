/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;

/**
 * 类MyTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2015年4月29日 下午10:19:44
 */
public class MyTest {

    private class TestConnectionTaks implements Callable<Connect> {

        private String url;

        public TestConnectionTaks(String url) {
            this.url = url;
        }

        @Override
        public Connect call() throws Exception {
            Connect conn = new Connect(url);
            //            for (int i = 0; i < 10; i++) {
            //                System.out.println(i);
            //                try {
            //                    Thread.sleep(500);
            //                } catch (InterruptedException e) {
            //                    e.printStackTrace();
            //                }
            //            }
            return conn;
        }

    }

    private class TestConnectionThread implements Runnable {

        private String url;

        public TestConnectionThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                Connect conn = new Connect(url);
                if (conn.isConnected()) {
                    System.out.println(url + "连接成功");
                } else {
                    System.out.println(url + "连接失败");
                }
            } catch (LibvirtException e) {
                e.printStackTrace();
            }

        }

    }

    @Test
    public void test() {
        String url = "qemu+tcp://192.168.0.20/system";

        long t1 = System.currentTimeMillis();

        Connect conn = null;
        try {
            conn = new Connect(url);
            System.out.println(conn.isConnected());
        } catch (LibvirtException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (LibvirtException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        long t2 = System.currentTimeMillis();
        System.out.println((t2 - t1) + "ms");

        //        ExecutorService executorService = Executors.newCachedThreadPool();
        //        while (true) {
        //            Future<Connect> f = executorService.submit(new TestConnectionTaks(url));
        //            try {
        //                Connect conn = f.get(1, TimeUnit.SECONDS);
        //                if (conn.isConnected()) {
        //                    System.out.println("连接成功");
        //                } else {
        //                    System.out.println("连接失败");
        //                }
        //                conn.close();
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //                System.out.println("连接失败");
        //            }
        //
        //            try {
        //                Thread.sleep(1000);
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }

        //        Connect[] conns = new Connect[100];
        //        try {
        //            for (int i = 0; i < conns.length; i++) {
        //                conns[i] = new Connect(url);
        //                if (conns[i].isConnected()) {
        //                    System.out.println("第" + (i + 1) + "个连接建立成功");
        //                } else {
        //                    System.out.println("第" + (i + 1) + "个连接建立失败");
        //                }
        //                try {
        //                    Thread.sleep(1000);
        //                    conns[i].close();
        //                } catch (InterruptedException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        } catch (LibvirtException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        try {
        //            Thread.sleep(1000);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
    }
}
