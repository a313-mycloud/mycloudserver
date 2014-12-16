/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDO;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.StudentClassQueryCondition;
import org.dlut.mycloudserver.dal.mapper.StudentClassMapper;
import org.springframework.stereotype.Service;

/**
 * 类StudentClassManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月15日 下午10:54:36
 */
@Service
public class StudentClassManage {

    @Resource
    private StudentClassMapper studentClassMapper;

    /**
     * 为学生添加一个课程
     * 
     * @param studentAccount
     * @param classId
     * @return
     */
    public boolean addClassForStudent(String studentAccount, int classId) {
        if (StringUtils.isBlank(studentAccount)) {
            return false;
        }
        return studentClassMapper.addClassForStudent(studentAccount, classId) == 1 ? true : false;
    }

    /**
     * 根据条件删除
     * 
     * @param studentClassDeleteCondition
     * @return
     */
    public boolean delete(StudentClassDeleteCondition studentClassDeleteCondition) {
        if (studentClassDeleteCondition == null) {
            return false;
        }
        studentClassMapper.delete(studentClassDeleteCondition);
        return true;
    }

    /**
     * 根据条件统计数量
     * 
     * @param studentClassQueryCondition
     * @return
     */
    public int countQuery(StudentClassQueryCondition studentClassQueryCondition) {
        if (studentClassQueryCondition == null) {
            return 0;
        }
        return studentClassMapper.countQuery(studentClassQueryCondition);
    }

    /**
     * 根据添加查询结果
     * 
     * @param studentClassQueryCondition
     * @return
     */
    public List<StudentClassDO> query(StudentClassQueryCondition studentClassQueryCondition) {
        if (studentClassQueryCondition == null) {
            return new ArrayList<StudentClassDO>();
        }
        return studentClassMapper.query(studentClassQueryCondition);
    }

}
