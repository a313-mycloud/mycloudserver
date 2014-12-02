/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

/**
 * 类VmStatusEnum.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月28日 下午10:36:59
 */
public enum VmStatusEnum {

    CLOSED(0, "关闭中"),
    RUNNING(1, "运行中");

    private int    status;
    private String desc;

    public static VmStatusEnum getVmStatusByStatus(int status) {
        for (VmStatusEnum vmStatusEnum : VmStatusEnum.values()) {
            if (vmStatusEnum.getStatus() == status) {
                return vmStatusEnum;
            }
        }
        return null;
    }

    private VmStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

}
