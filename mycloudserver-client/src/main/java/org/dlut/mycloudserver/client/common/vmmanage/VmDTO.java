/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

import org.dlut.mycloudserver.client.common.BaseDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;

/**
 * 类VmDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月1日 下午1:02:42
 */
public class VmDTO extends BaseDTO {

    private static final long     serialVersionUID = 1190643482692634742L;

    /**
     * 虚拟机名称
     */
    private String                vmName;

    /**
     * 虚拟机uuid
     */
    private String                vmUuid;

    /**
     * 虚拟机cpu数量
     */
    private Integer               vmVcpu;

    /**
     * 虚拟机内存
     */
    private Long                  vmMemory;

    /**
     * 虚拟机对应的镜像uuid
     */
    private String                imageUuid;

    /**
     * 镜像的总大小
     */
    private Long                  imageTotalSize;

    /**
     * 镜像格式
     */
    private StoreFormat           imageFormat;

    /**
     * 镜像的存储路径
     */
    private String                imagePath;

    /**
     * 虚拟机的mac地址
     */
    private String                vmMacAddress;

    /**
     * 虚拟机的网络类型
     */
    private NetworkTypeEnum       vmNetworkType;

    /**
     * 虚拟机的运行状态
     */
    private VmStatusEnum          vmStatus;

    /**
     * 虚拟机运行所在的物理机id
     */
    private Integer               hostId;

    /**
     * 用户账号
     */
    private String                userAccount;

    /**
     * 课程id
     */
    private Integer               classId;

    /**
     * 显示的类型
     */
    private ShowTypeEnum          showType;

    /**
     * 连接显示的端口号
     */
    private Integer               showPort;

    /**
     * 连接显示的密码
     */
    private String                showPassword;

    /**
     * 父虚拟机的uuid
     */
    private String                parentVmUuid;

    /**
     * 主硬盘的总线类型
     */
    private MasterDiskBusTypeEnum masterDiskBusType;

    /**
     * 网卡类型
     */
    private InterfaceTypeEnum     interfaceType;

    /**
     * 是否为模板虚拟机
     */
    private Boolean               isTemplateVm;

    /**
     * 是否是共有的模板虚拟机
     */
    private Boolean               isPublicTemplate;

    /**
     * 描述
     */
    private String                desc;
    
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
     * 用于标记系统的类型
     */
    private SystemTypeEnum     systemType;

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

    public VmStatusEnum getVmStatus() {
        return vmStatus;
    }

    public void setVmStatus(VmStatusEnum vmStatus) {
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

    public ShowTypeEnum getShowType() {
        return showType;
    }

    public void setShowType(ShowTypeEnum showType) {
        this.showType = showType;
    }

    public Integer getShowPort() {
        return showPort;
    }

    public void setShowPort(Integer showPort) {
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

    public StoreFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(StoreFormat imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public NetworkTypeEnum getVmNetworkType() {
        return vmNetworkType;
    }

    public void setVmNetworkType(NetworkTypeEnum vmNetworkType) {
        this.vmNetworkType = vmNetworkType;
    }

    public MasterDiskBusTypeEnum getMasterDiskBusType() {
        return masterDiskBusType;
    }

    public void setMasterDiskBusType(MasterDiskBusTypeEnum masterDiskBusType) {
        this.masterDiskBusType = masterDiskBusType;
    }

    public InterfaceTypeEnum getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceTypeEnum interfaceType) {
        this.interfaceType = interfaceType;
    }

	public Integer getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(Integer imageVersion) {
		this.imageVersion = imageVersion;
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

	
	public SystemTypeEnum getSystemType() {
		return systemType;
	}

	public void setSystemType(SystemTypeEnum systemType) {
		this.systemType = systemType;
	}

	@Override
	public String toString() {
		return "VmDTO [vmName=" + vmName + ", vmUuid=" + vmUuid + ", vmVcpu="
				+ vmVcpu + ", vmMemory=" + vmMemory + ", imageUuid="
				+ imageUuid + ", imageTotalSize=" + imageTotalSize
				+ ", imageFormat=" + imageFormat + ", imagePath=" + imagePath
				+ ", vmMacAddress=" + vmMacAddress + ", vmNetworkType="
				+ vmNetworkType + ", vmStatus=" + vmStatus + ", hostId="
				+ hostId + ", userAccount=" + userAccount + ", classId="
				+ classId + ", showType=" + showType + ", showPort=" + showPort
				+ ", showPassword=" + showPassword + ", parentVmUuid="
				+ parentVmUuid + ", masterDiskBusType=" + masterDiskBusType
				+ ", interfaceType=" + interfaceType + ", isTemplateVm="
				+ isTemplateVm + ", isPublicTemplate=" + isPublicTemplate
				+ ", desc=" + desc + ", imageVersion=" + imageVersion
				+ ", lastHostId=" + lastHostId + ", isCanRead=" + isCanRead
				+ "]";
	}
    
    

}
