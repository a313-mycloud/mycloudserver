/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import org.dlut.mycloudserver.dal.dataobject.StudentClassDO;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.StudentClassQueryCondition;
import org.dlut.mycloudserver.service.classmanage.ClassManage;
import org.dlut.mycloudserver.service.classmanage.StudentClassManage;
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

    @Resource
    private StudentClassManage studentClassManage;

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

    /**
     * 判断账号是否是学生
     * 
     * @param studentAccount
     * @return
     */
    private boolean isStudent(String studentAccount) {
        MyCloudResult<UserDTO> result = userManageService.getUserByAccount(studentAccount);
        if (!result.isSuccess()) {
            return false;
        }
        if (result.getModel().getRole() != RoleEnum.STUDENT) {
            return false;
        }

        return true;
    }

    /**
     * 判断课程是否存在
     * 
     * @param classId
     * @return
     */
    private boolean isClassExist(int classId) {
        ClassDO classDO = classManage.getClassById(classId);
        return classDO != null;
    }

    @Override
    public MyCloudResult<Boolean> addStudentInOneClass(String studentAccount, int classId) {
        if (StringUtils.isBlank(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!isStudent(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_STUDENT);
        }
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        if (!studentClassManage.addClassForStudent(studentAccount, classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_ADD_STUDENT_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteStudentInOneClass(String studentAccount, int classId) {
        if (StringUtils.isBlank(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!isStudent(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_STUDENT);
        }
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        StudentClassDeleteCondition studentClassDeleteCondition = new StudentClassDeleteCondition();
        studentClassDeleteCondition.setClassId(classId);
        studentClassDeleteCondition.setStudentAccount(studentAccount);
        if (!studentClassManage.delete(studentClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_STUDENT_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteStudentAllClass(String studentAccount) {
        if (StringUtils.isBlank(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!isStudent(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_STUDENT);
        }
        StudentClassDeleteCondition studentClassDeleteCondition = new StudentClassDeleteCondition();
        studentClassDeleteCondition.setStudentAccount(studentAccount);
        if (!studentClassManage.delete(studentClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteAllStudentInOneClass(int classId) {
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        StudentClassDeleteCondition studentClassDeleteCondition = new StudentClassDeleteCondition();
        studentClassDeleteCondition.setClassId(classId);
        if (!studentClassManage.delete(studentClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_STUDENT_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    private List<UserDTO> getUserListByStudentClassDOList(List<StudentClassDO> studentClassDOList) {
        List<UserDTO> userList = new ArrayList<UserDTO>();
        for (StudentClassDO studentClassDO : studentClassDOList) {
            MyCloudResult<UserDTO> result = userManageService.getUserByAccount(studentClassDO.getStudentAccount());
            if (result.isSuccess()) {
                userList.add(result.getModel());
            }
        }
        return userList;
    }

    private List<ClassDTO> getClassListByStudentClassDOList(List<StudentClassDO> studentClassDOList) {
        List<ClassDTO> classList = new ArrayList<ClassDTO>();
        for (StudentClassDO studentClassDO : studentClassDOList) {
            MyCloudResult<ClassDTO> result = getClassById(studentClassDO.getClassId());
            if (result.isSuccess()) {
                classList.add(result.getModel());
            }
        }
        return classList;
    }

    @Override
    public MyCloudResult<Pagination<UserDTO>> getStudentsInOneClass(int classId, int offset, int limit) {
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        if (offset < 0 || limit <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        StudentClassQueryCondition studentClassQueryCondition = new StudentClassQueryCondition();
        studentClassQueryCondition.setClassId(classId);
        studentClassQueryCondition.setOffset(offset);
        studentClassQueryCondition.setLimit(limit);
        int totalCount = studentClassManage.countQuery(studentClassQueryCondition);
        List<StudentClassDO> studentClassDOList = studentClassManage.query(studentClassQueryCondition);
        List<UserDTO> studentList = getUserListByStudentClassDOList(studentClassDOList);
        Pagination<UserDTO> pagination = new Pagination<UserDTO>(offset, limit, totalCount, studentList);
        return MyCloudResult.successResult(pagination);
    }

    @Override
    public MyCloudResult<Pagination<ClassDTO>> getClassesOfOneStudent(String studentAccount, int offset, int limit) {
        if (StringUtils.isBlank(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!isStudent(studentAccount)) {
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_STUDENT);
        }
        if (offset < 0 || limit <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        StudentClassQueryCondition studentClassQueryCondition = new StudentClassQueryCondition();
        studentClassQueryCondition.setStudentAccount(studentAccount);
        studentClassQueryCondition.setOffset(offset);
        studentClassQueryCondition.setLimit(limit);
        int totalCount = studentClassManage.countQuery(studentClassQueryCondition);
        List<StudentClassDO> studentClassDOList = studentClassManage.query(studentClassQueryCondition);
        List<ClassDTO> classList = getClassListByStudentClassDOList(studentClassDOList);
        Pagination<ClassDTO> pagination = new Pagination<ClassDTO>(offset, limit, totalCount, classList);
        return MyCloudResult.successResult(pagination);
    }
}
