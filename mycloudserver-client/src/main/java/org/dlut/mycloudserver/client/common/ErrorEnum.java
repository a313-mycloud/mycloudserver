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

    USER_NOT_EXIST("0900", "用户不存在"),

    CLASS_NOT_EXIST("1000", "课程不存在"),
    CLASS_CREATE_FAIL("1001", "创建课程失败"),
    CLASS_NOT_TEACHER("1002", "课程对应的用户不是教师"),
    CLASS_UPDATE_FAIL("1003", "更新课程失败"),
    CLASS_DELETE_FAIL("1004", "删除课程失败"),

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
