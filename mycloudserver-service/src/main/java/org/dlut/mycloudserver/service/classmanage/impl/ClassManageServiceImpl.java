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
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.dal.dataobject.ClassDO;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDO;
import org.dlut.mycloudserver.dal.dataobject.StudentClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.StudentClassQueryCondition;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDO;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassQueryCondition;
import org.dlut.mycloudserver.service.classmanage.ClassManage;
import org.dlut.mycloudserver.service.classmanage.StudentClassManage;
import org.dlut.mycloudserver.service.classmanage.TemplateVmClassManage;
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
    private ClassManage           classManage;

    @Resource
    private StudentClassManage    studentClassManage;

    @Resource(name = "userManageService")
    private IUserManageService    userManageService;

    @Resource
    private TemplateVmClassManage templateVmClassManage;

    @Resource(name = "vmManageService")
    private IVmManageService      vmManageService;

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

    private List<ClassDTO> getClassListByTemplateClassDOList(List<TemplateVmClassDO> templateVmClassDOList) {
        List<ClassDTO> classList = new ArrayList<ClassDTO>();
        for (TemplateVmClassDO templateVmClassDO : templateVmClassDOList) {
            MyCloudResult<ClassDTO> result = getClassById(templateVmClassDO.getClassId());
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

    /**
     * 判断是否是模板虚拟机
     * 
     * @param templateVmUuid
     * @return
     */
    private boolean isTemplateVmExist(String templateVmUuid) {
        if (StringUtils.isBlank(templateVmUuid)) {
            return false;
        }

        MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(templateVmUuid);
        if (!result.isSuccess()) {
            return false;
        }

        if (!result.getModel().getIsTemplateVm()) {
            return false;
        }

        return true;
    }

    @Override
    public MyCloudResult<Boolean> addTemplateVmToClass(String templateVmUuid, int classId) {
        if (!isTemplateVmExist(templateVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_TEMPLATE);
        }
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }

        if (!templateVmClassManage.addTemplateVmToClass(templateVmUuid, classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_ADD_TEMPLATE_VM_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteOneTemplateVmInOneClass(String templateVmUuid, int classId) {
        if (!isTemplateVmExist(templateVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_TEMPLATE);
        }
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }

        TemplateVmClassDeleteCondition templateVmClassDeleteCondition = new TemplateVmClassDeleteCondition();
        templateVmClassDeleteCondition.setClassId(classId);
        templateVmClassDeleteCondition.setTemplateVmUuid(templateVmUuid);
        if (!templateVmClassManage.delete(templateVmClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_TEMPLATE_VM_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteAllTemplateVmInOneClass(int classId) {
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        TemplateVmClassDeleteCondition templateVmClassDeleteCondition = new TemplateVmClassDeleteCondition();
        templateVmClassDeleteCondition.setClassId(classId);
        if (!templateVmClassManage.delete(templateVmClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_TEMPLATE_VM_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteAllClassWithTemplateVm(String templateVmUuid) {
        if (!isTemplateVmExist(templateVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_TEMPLATE);
        }

        TemplateVmClassDeleteCondition templateVmClassDeleteCondition = new TemplateVmClassDeleteCondition();
        templateVmClassDeleteCondition.setTemplateVmUuid(templateVmUuid);
        if (!templateVmClassManage.delete(templateVmClassDeleteCondition)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_DELETE_TEMPLATE_VM_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    private List<VmDTO> getTempalteVmListByTemplateVmClassDOList(List<TemplateVmClassDO> templateVmClassDOList) {
        List<VmDTO> vmDTOList = new ArrayList<VmDTO>();
        if (templateVmClassDOList == null) {
            return vmDTOList;
        }
        for (TemplateVmClassDO templateVmClassDO : templateVmClassDOList) {
            MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(templateVmClassDO.getTemplateVmUuid());
            vmDTOList.add(result.getModel());
        }

        return vmDTOList;
    }

    @Override
    public MyCloudResult<Pagination<VmDTO>> getTemplateVmsInOneClass(int classId, int offset, int limit) {
        if (!isClassExist(classId)) {
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        if (offset < 0 || limit <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        TemplateVmClassQueryCondition templateVmClassQueryCondition = new TemplateVmClassQueryCondition();
        templateVmClassQueryCondition.setClassId(classId);
        templateVmClassQueryCondition.setOffset(offset);
        templateVmClassQueryCondition.setLimit(limit);
        int totalCount = templateVmClassManage.countQuery(templateVmClassQueryCondition);
        List<TemplateVmClassDO> templateVmClassDOList = templateVmClassManage.query(templateVmClassQueryCondition);
        List<VmDTO> vmDTOList = getTempalteVmListByTemplateVmClassDOList(templateVmClassDOList);
        Pagination<VmDTO> pagination = new Pagination<VmDTO>(offset, limit, totalCount, vmDTOList);
        return MyCloudResult.successResult(pagination);
    }

    @Override
    public MyCloudResult<Pagination<ClassDTO>> getClassesWithTemplateVm(String templateVmUuid, int offset, int limit) {
        if (!isTemplateVmExist(templateVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_TEMPLATE);
        }
        if (offset < 0 || limit <= 0) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        TemplateVmClassQueryCondition templateVmClassQueryCondition = new TemplateVmClassQueryCondition();
        templateVmClassQueryCondition.setTemplateVmUuid(templateVmUuid);
        templateVmClassQueryCondition.setOffset(offset);
        templateVmClassQueryCondition.setLimit(limit);
        int totalCount = templateVmClassManage.countQuery(templateVmClassQueryCondition);
        List<TemplateVmClassDO> templateVmClassDOList = templateVmClassManage.query(templateVmClassQueryCondition);
        List<ClassDTO> classDTOList = getClassListByTemplateClassDOList(templateVmClassDOList);
        Pagination<ClassDTO> pagination = new Pagination<ClassDTO>(offset, limit, totalCount, classDTOList);
        return MyCloudResult.successResult(pagination);
    }

    //    @Override
    //    public MyCloudResult<Boolean> isBind(int classId, String templateVmUuid) {
    //        if (!isTemplateVmExist(templateVmUuid)) {
    //            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_TEMPLATE);
    //        }
    //        if (!isClassExist(classId)) {
    //            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
    //        }
    //        TemplateVmClassQueryCondition templateVmClassQueryCondition = new TemplateVmClassQueryCondition();
    //        templateVmClassQueryCondition.setClassId(classId);
    //        templateVmClassQueryCondition.setTemplateVmUuid(templateVmUuid);
    //        if (templateVmClassManage.countQuery(templateVmClassQueryCondition) <= 0) {
    //            return MyCloudResult.successResult(false);
    //        }
    //        return MyCloudResult.successResult(true);
    //    }

}
