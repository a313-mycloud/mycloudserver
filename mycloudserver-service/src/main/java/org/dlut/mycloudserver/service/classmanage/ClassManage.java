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

import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.dal.dataobject.ClassDO;
import org.dlut.mycloudserver.dal.mapper.ClassManageMapper;
import org.springframework.stereotype.Service;

/**
 * 类ClassManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午4:39:56
 */
@Service
public class ClassManage {

    @Resource
    private ClassManageMapper classManageMapper;

    public ClassDO getClassById(int classId) {
        if (classId <= 0) {
            return null;
        }
        return classManageMapper.getClassById(classId);
    }

    public int createClass(ClassDO classDO) {
        if (classDO == null) {
            return 0;
        }
        int res = classManageMapper.createClass(classDO);
        if (res == 1) {
            return classDO.getClassId();
        }
        return 0;
    }

    public boolean updateClass(ClassDO classDO) {
        if (classDO == null) {
            return false;
        }
        return classManageMapper.updateClass(classDO) == 1 ? true : false;
    }

    public boolean deleteClass(int classId) {
        if (classId <= 0) {
            return false;
        }
        return classManageMapper.deleteClassById(classId) == 1 ? true : false;
    }

    public int countQuery(QueryClassCondition queryClassCondition) {
        if (queryClassCondition == null) {
            return 0;
        }
        return classManageMapper.countQuery(queryClassCondition);
    }

    public List<ClassDO> query(QueryClassCondition queryClassCondition) {
        if (queryClassCondition == null) {
            return new ArrayList<ClassDO>();
        }
        return classManageMapper.query(queryClassCondition);
    }
}
