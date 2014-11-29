/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.classmanage;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * 类ClassDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午3:18:10
 */
public class ClassDTO extends BaseDTO {

    private static final long serialVersionUID = -6745759667815140147L;

    /**
     * 课程id
     */
    private Integer           classId;

    /**
     * 课程名称
     */
    private String            className;

    /**
     * 教师账号
     */
    private String            teacherAccount;

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherAccount() {
        return teacherAccount;
    }

    public void setTeacherAccount(String teacherAccount) {
        this.teacherAccount = teacherAccount;
    }

}
