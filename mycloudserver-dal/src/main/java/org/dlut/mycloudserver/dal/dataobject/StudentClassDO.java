/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类StudentClassDO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月15日 下午8:45:39
 */
public class StudentClassDO extends BaseDO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 学生账号
     */
    private String  studentAccount;

    /**
     * 课程id
     */
    private Integer classId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

}
