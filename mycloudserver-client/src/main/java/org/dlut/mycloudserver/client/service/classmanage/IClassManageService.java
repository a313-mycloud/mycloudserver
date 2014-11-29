/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.classmanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;

/**
 * 类IClassManageService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月27日 下午10:28:12
 */
public interface IClassManageService {

    /**
     * 根据id获取课程信息
     * 
     * @param classId
     * @return
     */
    public MyCloudResult<ClassDTO> getClassById(int classId);

    /**
     * 创建一个新的课程
     * 
     * @param classDTO
     * @return
     */
    public MyCloudResult<Integer> createClass(ClassDTO classDTO);

    /**
     * 更新课程
     * 
     * @param classDTO
     * @return
     */
    public MyCloudResult<Boolean> updateClass(ClassDTO classDTO);

    /**
     * 删除课程
     * 
     * @param classDTO
     * @return
     */
    public MyCloudResult<Boolean> deleteClass(int classId);

    /**
     * 根据条件统计课程数量
     * 
     * @param queryClassCondition
     * @return
     */
    public MyCloudResult<Integer> countQuery(QueryClassCondition queryClassCondition);

    /**
     * 根据条件查询课程列表
     * 
     * @param queryClassCondition
     * @return
     */
    public MyCloudResult<Pagination<ClassDTO>> query(QueryClassCondition queryClassCondition);

}
