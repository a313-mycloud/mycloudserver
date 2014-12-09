/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

/**
 * 类ShowTypeEnum.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月8日 下午1:55:34
 */
public enum ShowTypeEnum {

    SPICE(1, "spice"),
    VNC(2, "vnc");

    private int    value;
    private String desc;

    public static ShowTypeEnum getShowTypeByValue(int value) {
        for (ShowTypeEnum showTypeEnum : ShowTypeEnum.values()) {
            if (showTypeEnum.getValue() == value) {
                return showTypeEnum;
            }
        }
        return null;
    }

    private ShowTypeEnum(int value, String desc) {
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
