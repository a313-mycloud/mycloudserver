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
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;

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

    /**
     * 将一个学生添加到一门课程中
     * 
     * @param studentAccount
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> addStudentInOneClass(String studentAccount, int classId);

    /**
     * 删除一门课中的一个学生
     * 
     * @param studentAccount
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> deleteStudentInOneClass(String studentAccount, int classId);

    /**
     * 删除一个学生的所有课程
     * 
     * @param studentAccount
     * @return
     */
    public MyCloudResult<Boolean> deleteStudentAllClass(String studentAccount);

    /**
     * 删除一门课下面的所有学生
     * 
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> deleteAllStudentInOneClass(int classId);

    /**
     * 分页获取一个课程下的学生列表
     * 
     * @param classId
     * @param offset
     * @param limit
     * @return
     */
    public MyCloudResult<Pagination<UserDTO>> getStudentsInOneClass(int classId, int offset, int limit);

    /**
     * 分页获取一个学生的课程列表
     * 
     * @param studentAccount
     * @param offset
     * @param limit
     * @return
     */
    public MyCloudResult<Pagination<ClassDTO>> getClassesOfOneStudent(String studentAccount, int offset, int limit);

    /**
     * 将虚拟机模板添加到课程中
     * 
     * @param templateVmUuid
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> addTemplateVmToClass(String templateVmUuid, int classId);

    /**
     * 删除某个课程下的的某个虚拟机
     * 
     * @param classId
     * @param templateVmUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteOneTemplateVmInOneClass(String templateVmUuid, int classId);

    /**
     * 删除某个课程下面的所有虚拟机
     * 
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> deleteAllTemplateVmInOneClass(int classId);

    /**
     * 删除某个虚拟机镜像模板下面的所有虚拟机
     * 
     * @param templateVmUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteAllClassWithTemplateVm(String templateVmUuid);

    /**
     * 获取某个课程下面的所有模板虚拟机
     * 
     * @param classId
     * @param offset
     * @param limit
     * @return
     */
    public MyCloudResult<Pagination<VmDTO>> getTemplateVmsInOneClass(int classId, int offset, int limit);

    /**
     * 获取某个虚拟机模板对应的所有课程
     * 
     * @param templateVmUuid
     * @param offset
     * @param limit
     * @return
     */
    public MyCloudResult<Pagination<ClassDTO>> getClassesWithTemplateVm(String templateVmUuid, int offset, int limit);

}
