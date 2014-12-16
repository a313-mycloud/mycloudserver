/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类StudentClassQueryCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月15日 下午8:50:46
 */
public class StudentClassQueryCondition {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 课程id
     */
    private Integer classId;

    /**
     * 学生账号
     */
    private String  studentAccount;

    /**
     * 偏移量
     */
    private int     offset = 0;

    /**
     * 每页大小
     */
    private int     limit  = 10;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
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
