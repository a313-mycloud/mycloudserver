/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.classmanage;

import java.io.Serializable;

/**
 * 类QueryClassCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午4:00:27
 */
public class QueryClassCondition implements Serializable {

    private static final long serialVersionUID = 3326025327445888428L;

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

    private int               offset           = 0;

    private int               limit            = 10;

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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
