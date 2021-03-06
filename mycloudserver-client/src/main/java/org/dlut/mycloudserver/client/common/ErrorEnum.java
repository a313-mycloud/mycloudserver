/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common;

/**
 * 类ErrorEnum.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月2日 下午7:47:03
 */
public enum ErrorEnum {

    PARAM_NULL("1", "参数为空"),
    IMAGE_NOT_EXIST("2", "镜像不存在"),
    IMAGE_CREATE_FAIL("3", "镜像创建失败"),
    IMAGE_UPDATE_FAIL("4", "更新镜像失败"),
    IMAGE_FILE_NOT_EXIST("5", "镜像文件不存在"),
    IMAGE_GET_FORMAT_SIZE_FAIL("6", "获取镜像格式和大小失败"),
    IMAGE_FORMAT_UNKNOW("7", "未知的镜像格式"),
    IMAGE_CLONE_FORMAT_INVAILD("8", "不能克隆qcow2格式以外的镜像"),
    IMAGE_PHYSICAL_DELETE_FAIL("9", "物理删除镜像失败"),
    GET_LOCAL_CONN("10", "获取本地libvirt连接失败"),
    GET_CONN_FAIL("11", "获取连接失败"),
    IMAGE_HAS_BIND_VM("12", "镜像已近和其他虚拟机绑定"),
    UPLOAD_IMAGE_FORMAT_INVALID("13", "上传镜像格式违法"),
    UPLOAD_IMAGE_NOT_EXIST("14", "上传镜像不存在"),

    USER_NOT_EXIST("0900", "用户不存在"),
    USER_NOT_STUDENT("0901", "用户不是学生"),

    CLASS_NOT_EXIST("1000", "课程不存在"),
    CLASS_CREATE_FAIL("1001", "创建课程失败"),
    CLASS_NOT_TEACHER("1002", "课程对应的用户不是教师"),
    CLASS_UPDATE_FAIL("1003", "更新课程失败"),
    CLASS_DELETE_FAIL("1004", "删除课程失败"),
    CLASS_ADD_STUDENT_FAIL("1005", "在课程中添加学生失败"),
    CLASS_DELETE_STUDENT_FAIL("1006", "在课程中删除学生失败"),
    CLASS_ADD_TEMPLATE_VM_FAIL("1007", "课程添加模板虚拟机失败"),
    CLASS_DELETE_TEMPLATE_VM_FAIL("1008", "在课程中删除模板虚拟机失败"),

    VM_NOT_EXIST("2000", "虚拟机不存在"),
    VM_CREATE_FAIL("2001", "创建虚拟机失败"),
    VM_START_FAIL("2002", "启动虚拟机失败"),
    VM_GET_BEST_HOST_FIAL("2003", "获取最佳物理机失败"),
    VM_UPDATE_FIAL("2004", "在数据库中更新虚拟机失败"),
    VM_DESTROY_FAIL("2005", "虚拟机强制关机失败"),
    VM_DELETE_FAIL("2006", "删除虚拟机失败"),
    VM_TEMPLATE_CAN_NOT_START("2007", "模板虚拟机不能启动"),
    VM_ONLY_CLONE_FROM_TEMPLATE("2008", "只能从模板虚拟机中克隆虚拟机"),
    VM_RUNNING_CAN_NOT_CHANGE_TO_TEMPLATE("2009", "运行中的虚拟机不能转化为模板虚拟机"),
    VM_NOT_TEMPLATE("2010", "不是模板虚拟机"),
    VM_GET_DEV_NAME_FIAL("2011", "未找到合适的设备名称来挂载硬盘"),
    VM_TRANSFER_FIAL("2012", "虚拟机传输失败"),
    VM_DHCP_FAIL("2013", "虚拟机从DHCP获取IP地址失败"),
    VM_ADDRESSMAPPING_FAIL("2014", "虚拟机地址映射失败"),
    VM_SHOWPORT_ILLEGAL("2015", "虚拟机地址不合法"),

    HOST_NOT_EXIST("3000", "物理机不存在"),
    HOST_DELETE_FAIL("3001", "删除物理机失败"),

    DISK_NOT_EXIST("4000", "硬盘不存在"),
    DISK_PHYSICAL_CREATE_FAIL("4001", "物理创建硬盘失败"),
    DISK_DB_CREATE_FAIL("4002", "在数据库中创建硬盘失败"),
    DISK_UPDATE_FAIL("4003", "更新硬盘失败"),
    DISK_DB_DELETE_FAIL("4004", "数据库删除硬盘失败"),
    DISK_PHYSICAL_DELETE_FAIL("4005", "物理删除硬盘失败"),
    DISK_HAS_ATTACH_VM("4006", "硬盘已挂载到虚拟机上"),
    DISK_ATTACH_FAIL("4007", "硬盘挂载失败"),
    DISK_DETACH_FAIL("4008", "硬盘卸载失败"),

    PERFORMANCE_NOT_EXIST("5000", "性能监控的主机没有注册"),
    PERFORMANCE_CREATE_FAIL("5001", "性能监控创建失败"),
    PERFORMANCE_UPDATE_FAIL("5002", "更新性能监控失败"),
    PERFORMANCE_DELETE_FAIL("5003", "删除性能监控主机失败"),

    PARAM_IS_INVAILD("9999", "参数非法");

    private String errCode;
    private String errDesc;

    private ErrorEnum(String errCode, String errDesc) {
        this.errCode = errCode;
        this.errDesc = errDesc;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrDesc() {
        return this.errDesc;
    }
}
