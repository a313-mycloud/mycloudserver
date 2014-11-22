/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.storemanage;

/**
 * 类StoreFormat.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午3:58:32
 */
public enum StoreFormat {

    RAW(1, "raw"),
    QCOW2(2, "qcow2");

    private int    value;
    private String desc;

    public static StoreFormat getStoreFormatByValue(int value) {
        for (StoreFormat storeFormat : StoreFormat.values()) {
            if (storeFormat.getValue() == value) {
                return storeFormat;
            }
        }

        return null;
    }

    private StoreFormat(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }
}
