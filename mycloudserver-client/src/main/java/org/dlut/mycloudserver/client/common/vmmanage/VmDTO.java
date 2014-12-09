/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.vmmanage;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * 类VmDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月1日 下午1:02:42
 */
public class VmDTO extends BaseDTO {

    private static final long serialVersionUID = 1190643482692634742L;

    /**
     * 虚拟机uuid
     */
    private String            vmUuid;

    /**
     * 虚拟机cpu数量
     */
    private Integer           vmVcpu;

    /**
     * 虚拟机内存
     */
    private Long              vmMemory;

    /**
     * 虚拟机对应的镜像uuid
     */
    private String            imageUuid;

    /**
     * 虚拟机的运行状态
     */
    private VmStatusEnum      vmStatus;

    /**
     * 虚拟机运行所在的物理机id
     */
    private Integer           hostId;

    /**
     * 用户账号
     */
    private String            userAccount;

    /**
     * 课程id
     */
    private Integer           classId;

    /**
     * 显示的类型
     */
    private ShowTypeEnum      showType;

    /**
     * 连接显示的端口号
     */
    private Integer           showPort;

    /**
     * 连接显示的密码
     */
    private String            showPassword;

    /**
     * 描述
     */
    private String            desc;

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

}
