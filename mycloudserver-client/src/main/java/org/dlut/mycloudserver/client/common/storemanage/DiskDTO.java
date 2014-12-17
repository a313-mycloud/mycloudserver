/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.storemanage;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * 类DiskDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午8:41:51
 */
public class DiskDTO extends BaseDTO {

    private static final long serialVersionUID = -7441605909076566787L;

    /**
     * 硬盘uuid
     */
    private String            diskUuid;

    /**
     * 硬盘名称
     */
    private String            diskName;

    /**
     * 硬盘的总大小
     */
    private Long              diskTotalSize;

    /**
     * 硬盘已使用的大小
     */
    private Long              diskUsedSize;

    /**
     * 硬盘路径
     */
    private String            diskPath;

    /**
     * 硬盘镜像格式
     */
    private StoreFormat       diskFormat;

    /**
     * 所挂载的虚拟机uuid
     */
    private String            attachVmUuid;

    /**
     * 所属用户的账号
     */
    private String            userAccount;

    private String            desc;

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

    public Long getDiskTotalSize() {
        return diskTotalSize;
    }

    public void setDiskTotalSize(Long diskTotalSize) {
        this.diskTotalSize = diskTotalSize;
    }

    public Long getDiskUsedSize() {
        return diskUsedSize;
    }

    public void setDiskUsedSize(Long diskUsedSize) {
        this.diskUsedSize = diskUsedSize;
    }

    public String getDiskPath() {
        return diskPath;
    }

    public void setDiskPath(String diskPath) {
        this.diskPath = diskPath;
    }

    public StoreFormat getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(StoreFormat diskFormat) {
        this.diskFormat = diskFormat;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
