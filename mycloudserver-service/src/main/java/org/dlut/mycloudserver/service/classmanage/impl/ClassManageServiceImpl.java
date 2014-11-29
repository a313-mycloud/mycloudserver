/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage.impl;

import java.util.List;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.classmanage.QueryClassCondition;
import org.dlut.mycloudserver.client.common.usermanage.RoleEnum;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.dlut.mycloudserver.dal.dataobject.ClassDO;
import org.dlut.mycloudserver.service.classmanage.ClassManage;
import org.dlut.mycloudserver.service.classmanage.convent.ClassConvent;
import org.springframework.stereotype.Service;

/**
 * 类ClassManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午4:47:18
 */
@Service("classManageService")
public class ClassManageServiceImpl implements IClassManageService {

    @Resource
    private ClassManage        classManage;

    @Resource(name = "userManageService")
    private IUserManageService userManageService;

    @Override
    public MyCloudResult<ClassDTO> getClassById(int classId) {
        ClassDO classDO = classManage.getClassById(classId);
        ClassDTO classDTO = ClassConvent.conventToClassDTO(classDO);
        if (classDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }

        return MyCloudResult.successResult(classDTO);
    }

    @Override
    public MyCloudResult<Integer> createClass(ClassDTO classDTO) {
        ClassDO classDO = ClassConvent.conventToClassDO(classDTO);
        if (classDO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!isTeacher(classDTO.getTeacherAccount())) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_TEACHER);
        }
        int classId = classManage.createClass(classDO);
        if (classId == 0) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_CREATE_FAIL);
        }
        return MyCloudResult.successResult(classId);
    }

    @Override
    public MyCloudResult<Boolean> updateClass(ClassDTO classDTO) {
        if (classDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (classDTO.getTeacherAccount() != null) {
            if (!isTeacher(classDTO.getTeacherAccount())) {
                return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_TEACHER);
            }
        }
        ClassDO classDO = ClassConvent.conventToClassDO(classDTO);
        if (!classManage.updateClass(classDO)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_UPDATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 判断账号是否是教师
     * 
     * @param account
     * @return
     */
    private boolean isTeacher(String account) {
        MyCloudResult<UserDTO> result = userManageService.getUserByAccount(account);
        if (result.isSuccess() && result.getModel().getRole() == RoleEnum.TEACHER) {
            return true;
        }
        return false;
    }

    @Override
    public MyCloudResult<Boolean> deleteClass(int classId) {
        // TODO 需要先判断该课程下是否还有学生
        if (!classManage.deleteClass(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryClassCondition queryClassCondition) {
        if (queryClassCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int count = classManage.countQuery(queryClassCondition);
        return MyCloudResult.successResult(count);
    }

    @Override
    public MyCloudResult<Pagination<ClassDTO>> query(QueryClassCondition queryClassCondition) {
        if (queryClassCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = classManage.countQuery(queryClassCondition);
        List<ClassDO> classDOList = classManage.query(queryClassCondition);
        List<ClassDTO> classDTOList = ClassConvent.conventToClassDTOList(classDOList);
        Pagination<ClassDTO> pagination = new Pagination<ClassDTO>(queryClassCondition.getOffset(),
                queryClassCondition.getLimit(), totalCount, classDTOList);
        return MyCloudResult.successResult(pagination);
    }
}
