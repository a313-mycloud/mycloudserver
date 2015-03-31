/*
 * Copyright 2015 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

/**
 * 主硬盘总线枚举类
 * 
 * @author luojie 2015年3月31日 下午7:38:01
 */
public enum MasterDiskBusTypeEnum {

    VIRTIO(1, "virtio"),
    SCSI(2, "scsi"),
    IED(3, "ide");

    private int    value;
    private String desc;

    public static MasterDiskBusTypeEnum getMasterDiskBusTypeByValue(int value) {
        for (MasterDiskBusTypeEnum masterDiskBusTypeEnum : MasterDiskBusTypeEnum.values()) {
            if (value == masterDiskBusTypeEnum.getValue()) {
                return masterDiskBusTypeEnum;
            }
        }

        return null;
    }

    private MasterDiskBusTypeEnum(int value, String desc) {
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
