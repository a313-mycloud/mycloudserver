/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类DiskDO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午3:53:25
 */
public class DiskDO extends BaseDO {

    /**
     * 硬盘的uuid
     */
    private String diskUuid;

    /**
     * 硬盘名称
     */
    private String diskName;

    /**
     * 硬盘总大小，单位为字节
     */
    private long   diskTotalSize;

    /**
     * 硬盘格式
     */
    private String diskFormat;

    /**
     * 挂载所在的虚拟机
     */
    private String attachVmUuid;

    /**
     * 所属的用户
     */
    private String userAccount;

    /**
     * 硬盘描述
     */
    private String desc;

    public String getDiskUuid() {
        return diskUuid;
    }

    public void setDiskUuid(String diskUuid) {
        this.diskUuid = diskUuid;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public long getDiskTotalSize() {
        return diskTotalSize;
    }

    public void setDiskTotalSize(long diskTotalSize) {
        this.diskTotalSize = diskTotalSize;
    }

    public String getAttachVmUuid() {
        return attachVmUuid;
    }

    public void setAttachVmUuid(String attachVmUuid) {
        this.attachVmUuid = attachVmUuid;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
