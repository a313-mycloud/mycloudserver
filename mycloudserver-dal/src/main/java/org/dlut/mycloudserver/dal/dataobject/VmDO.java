/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类VmDO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月30日 下午6:59:24
 */
public class VmDO extends BaseDO {

    /**
     * 虚拟机名称
     */
    private String  vmName;

    /**
     * 虚拟机的uuid
     */
    private String  vmUuid;

    /**
     * 虚拟机的cpu数量
     */
    private Integer vmVcpu;

    /**
     * 虚拟机的内存
     */
    private Long    vmMemory;

    /**
     * 虚拟机镜像的uuid
     */
    private String  imageUuid;

    /**
     * 镜像的总大小
     */
    private Long    imageTotalSize;

    /**
     * 虚拟机的镜像格式
     */
    private String  imageFormat;

    /**
     * 虚拟机mac地址
     */
    private String  vmMacAddress;

    /**
     * 虚拟机网络类型，nat或者桥接
     */
    private int     vmNetworkType;

    /**
     * 虚拟机的运行状态
     */
    private Integer vmStatus;

    /**
     * 虚拟机运行所在的物理机的id
     */
    private Integer hostId;

    /**
     * 用户账号
     */
    private String  userAccount;

    /**
     * 虚拟机所属的课程，如果为0，表示空课程
     */
    private Integer classId;

    /**
     * 显示类型，有vnc和spice两种类型
     */
    private Integer showType;

    /**
     * 显示的端口号
     */
//    private Integer showPort;
    private  String showPort;

    /**
     * 连接显示的密码
     */
    private String  showPassword;

    /**
     * 表示从哪个虚拟机克隆过来的
     */
    private String  parentVmUuid;

    /**
     * 主硬盘总线类型
     */
    private Integer masterDiskBusType;

    /**
     * 网卡类型
     */
    private Integer interfaceType;

    /**
     * 是否为模板虚拟机
     */
    private Boolean isTemplateVm;

    /**
     * 如果虚拟机是模板虚拟机，则标示是否是共有的
     */
    private Boolean isPublicTemplate;
    /*
     * 记录当前文件系统中存储的镜像版本，新创建的版本号为0，每次更新加1
     */
    private Integer imageVersion;
    /*
     * 记录上次该像像运行的主机号，初次创建为-1
     */
    private Integer  lastHostId;
    /*
     * 标记当前镜像是否可读，默认为1
     */
    private Integer isCanRead;
    /*
     * 标记系统类型
     */
    private Integer systemType;
    

    /**
     * 描述
     */
    private String  desc;

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getVmUuid() {
        return vmUuid;
    }

    public void setVmUuid(String vmUuid) {
        this.vmUuid = vmUuid;
    }

    public Integer getVmVcpu() {
        return vmVcpu;
    }

    public void setVmVcpu(Integer vmVcpu) {
        this.vmVcpu = vmVcpu;
    }

    public Long getVmMemory() {
        return vmMemory;
    }

    public void setVmMemory(Long vmMemory) {
        this.vmMemory = vmMemory;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public Integer getVmStatus() {
        return vmStatus;
    }

    public void setVmStatus(Integer vmStatus) {
        this.vmStatus = vmStatus;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getShowType() {
        return showType;
    }

    public void setShowType(Integer showType) {
        this.showType = showType;
    }

    public String  getShowPort() {
        return showPort;
    }

    public void setShowPort(String  showPort) {
        this.showPort = showPort;
    }

    public String getShowPassword() {
        return showPassword;
    }

    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }

    public Long getImageTotalSize() {
        return imageTotalSize;
    }

    public void setImageTotalSize(Long imageTotalSize) {
        this.imageTotalSize = imageTotalSize;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getParentVmUuid() {
        return parentVmUuid;
    }

    public void setParentVmUuid(String parentVmUuid) {
        this.parentVmUuid = parentVmUuid;
    }

    public Boolean getIsTemplateVm() {
        return isTemplateVm;
    }

    public void setIsTemplateVm(Boolean isTemplateVm) {
        this.isTemplateVm = isTemplateVm;
    }

    public Boolean getIsPublicTemplate() {
        return isPublicTemplate;
    }

    public void setIsPublicTemplate(Boolean isPublicTemplate) {
        this.isPublicTemplate = isPublicTemplate;
    }

    public String getVmMacAddress() {
        return vmMacAddress;
    }

    public void setVmMacAddress(String vmMacAddress) {
        this.vmMacAddress = vmMacAddress;
    }

    public int getVmNetworkType() {
        return vmNetworkType;
    }

    public void setVmNetworkType(int vmNetworkType) {
        this.vmNetworkType = vmNetworkType;
    }

    public Integer getMasterDiskBusType() {
        return masterDiskBusType;
    }

    public void setMasterDiskBusType(Integer masterDiskBusType) {
        this.masterDiskBusType = masterDiskBusType;
    }

    public Integer getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Integer interfaceType) {
        this.interfaceType = interfaceType;
    }

		public Integer getLastHostId() {
		return lastHostId;
	}

	public void setLastHostId(Integer lastHostId) {
		this.lastHostId = lastHostId;
	}

	public Integer getIsCanRead() {
		return isCanRead;
	}

	public void setIsCanRead(Integer isCanRead) {
		this.isCanRead = isCanRead;
	}

	public Integer getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(Integer imageVersion) {
		this.imageVersion = imageVersion;
	}

	
	public Integer getSystemType() {
		return systemType;
	}

	public void setSystemType(Integer systemType) {
		this.systemType = systemType;
	}

	@Override
	public String toString() {
		return "VmDO [vmName=" + vmName + ", vmUuid=" + vmUuid + ", vmVcpu="
				+ vmVcpu + ", vmMemory=" + vmMemory + ", imageUuid="
				+ imageUuid + ", imageTotalSize=" + imageTotalSize
				+ ", imageFormat=" + imageFormat + ", vmMacAddress="
				+ vmMacAddress + ", vmNetworkType=" + vmNetworkType
				+ ", vmStatus=" + vmStatus + ", hostId=" + hostId
				+ ", userAccount=" + userAccount + ", classId=" + classId
				+ ", showType=" + showType + ", showPort=" + showPort
				+ ", showPassword=" + showPassword + ", parentVmUuid="
				+ parentVmUuid + ", masterDiskBusType=" + masterDiskBusType
				+ ", interfaceType=" + interfaceType + ", isTemplateVm="
				+ isTemplateVm + ", isPublicTemplate=" + isPublicTemplate
				+ ", imageVersion=" + imageVersion + ", lastHostId="
				+ lastHostId + ", isCanRead=" + isCanRead + ", desc=" + desc
				+ "]";
	}
	
    

}
