/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

/**
 * 类NetworkTypeEnum.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月29日 下午3:27:42
 */
public enum NetworkTypeEnum {
 
    NAT(1, "nat"),
    BRIDGE(2, "桥接"),
    WEB(3,"web");

    private int    value;
    private String desc;

    public static NetworkTypeEnum getNetworkTypeByValue(int value) {
        for (NetworkTypeEnum networkTypeEnum : NetworkTypeEnum.values()) {
            if (networkTypeEnum.getValue() == value) {
                return networkTypeEnum;
            }
        }
        return null;
    }

    private NetworkTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
