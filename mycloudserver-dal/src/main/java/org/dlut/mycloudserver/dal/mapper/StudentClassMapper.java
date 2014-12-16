/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDO;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.StudentClassQueryCondition;

/**
 * 类StudentClassMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月15日 下午8:19:22
 */
public interface StudentClassMapper {

    /**
     * 为学生添加一个课程
     * 
     * @param studentAccount
     * @param classId
     * @return
     */
    int addClassForStudent(@Param("studentAccount") String studentAccount, @Param("classId") int classId);

    /**
     * 根据条件删除
     * 
     * @param studentClassDeleteCondition
     * @return
     */
    int delete(StudentClassDeleteCondition studentClassDeleteCondition);

    /**
     * 根据条件统计数量
     * 
     * @param studentClassQueryCondition
     * @return
     */
    int countQuery(StudentClassQueryCondition studentClassQueryCondition);

    /**
     * 根据添加查询结果
     * 
     * @param studentClassQueryCondition
     * @return
     */
    List<StudentClassDO> query(StudentClassQueryCondition studentClassQueryCondition);

}
