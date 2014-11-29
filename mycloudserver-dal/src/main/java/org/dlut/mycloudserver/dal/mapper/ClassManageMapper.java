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
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.dal.dataobject.ClassDO;

/**
 * 类ClassManageMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午3:38:10
 */
public interface ClassManageMapper {

    /**
     * 根据课程id获取课程信息
     * 
     * @param classId
     * @return
     */
    ClassDO getClassById(@Param("classId") int classId);

    /**
     * 创建新的课程
     * 
     * @param classDO
     * @return
     */
    int createClass(ClassDO classDO);

    /**
     * 更新课程
     * 
     * @param classDO
     * @return
     */
    int updateClass(ClassDO classDO);

    /**
     * 删除课程
     * 
     * @param classId
     * @return
     */
    int deleteClassById(@Param("classId") int classId);

    /**
     * 根据条件统计课程数量
     * 
     * @param queryClassCondition
     * @return
     */
    int countQuery(QueryClassCondition queryClassCondition);

    /**
     * 查询相应条件下的课程列表
     * 
     * @param queryClassCondition
     * @return
     */
    List<ClassDO> query(QueryClassCondition queryClassCondition);
}
