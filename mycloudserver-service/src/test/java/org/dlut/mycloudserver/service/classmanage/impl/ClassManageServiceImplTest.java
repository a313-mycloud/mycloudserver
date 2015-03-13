/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage.impl;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;

/**
 * 类ClassManageServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午6:10:58
 */
public class ClassManageServiceImplTest extends BaseTestCase {

    @Resource(name = "classManageService")
    private IClassManageService classManageService;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.classmanage.impl.ClassManageServiceImpl#getClassById(int)}
     * .
     */
    @Test
    public void testGetClassById() {
        MyCloudResult<ClassDTO> result = classManageService.getClassById(1);
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.classmanage.impl.ClassManageServiceImpl#createClass(org.dlut.mycloudserver.client.common.classmanage.ClassDTO)}
     * .
     */
    @Test
    public void testCreateClass() {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setClassName("a313");
        classDTO.setTeacherAccount("teacher");
        MyCloudResult<Integer> result = classManageService.createClass(classDTO);
        printObject(result);
    }

    @Test
    public void testUpdateClass() {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setClassId(4);
        classDTO.setClassName("操作系统2");
        classDTO.setTeacherAccount("b");
        MyCloudResult<Boolean> result = classManageService.updateClass(classDTO);
        printObject(result);
    }

    @Test
    public void testDeleteClass() {
        int classId = 4;
        MyCloudResult<Boolean> result = classManageService.deleteClass(classId);
        printObject(result);
    }

    @Test
    public void testCount() {
        QueryClassCondition queryClassCondition = new QueryClassCondition();
        queryClassCondition.setTeacherAccount("a");
        MyCloudResult<Integer> result = classManageService.countQuery(queryClassCondition);
        printObject(result);
    }

    @Test
    public void testQuery() {
        QueryClassCondition queryClassCondition = new QueryClassCondition();
        queryClassCondition.setLimit(5);
        queryClassCondition.setOffset(0);
        MyCloudResult<Pagination<ClassDTO>> result = classManageService.query(queryClassCondition);
        printObject(result);
    }

    @Test
    public void testAddStudentInOneClass() {
        int classId = 3;
        String studentAccount = "1";
        MyCloudResult<Boolean> result = classManageService.addStudentInOneClass(studentAccount, classId);
        printObject(result);
    }

    @Test
    public void testGetClassesOfOneStudent() {
        String studentAccount = "student";
        MyCloudResult<Pagination<ClassDTO>> result = classManageService.getClassesOfOneStudent(studentAccount, 0, 10);
        printObject(result);
    }

    @Test
    public void testGetStudentsInOneClass() {
        int classId = 3;
        MyCloudResult<Pagination<UserDTO>> result = classManageService.getStudentsInOneClass(classId, 0, 10);
        printObject(result);
    }

    @Test
    public void testAddTemplateVmToClass() {
        int classId = 5;
        String templateVmUuid = "1d3526dc-8942-42c9-8b6d-86c2bf08e329";
        MyCloudResult<Boolean> result = classManageService.addTemplateVmToClass(templateVmUuid, classId);
        printObject(result);
    }

    @Test
    public void testDeleteAllClassWithTemplateVM() {
        String vmUuid = "a9256198-dc70-4bec-a81b-77cbe5b363c9";
        MyCloudResult<Boolean> result = classManageService.deleteAllClassWithTemplateVm(vmUuid);
        printObject(result);
    }
}
