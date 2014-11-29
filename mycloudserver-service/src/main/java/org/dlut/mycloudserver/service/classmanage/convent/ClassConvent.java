/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage.convent;

import java.util.ArrayList;
import java.util.List;

import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.dal.dataobject.ClassDO;

/**
 * 类ClassConvent.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月28日 下午4:52:03
 */
public class ClassConvent {

    public static ClassDTO conventToClassDTO(ClassDO classDO) {
        if (classDO == null) {
            return null;
        }

        ClassDTO classDTO = new ClassDTO();
        classDTO.setClassId(classDO.getClassId());
        classDTO.setClassName(classDO.getClassName());
        classDTO.setTeacherAccount(classDO.getTeacherAccount());

        return classDTO;
    }

    public static ClassDO conventToClassDO(ClassDTO classDTO) {
        if (classDTO == null) {
            return null;
        }

        ClassDO classDO = new ClassDO();
        classDO.setClassId(classDTO.getClassId());
        classDO.setClassName(classDTO.getClassName());
        classDO.setTeacherAccount(classDTO.getTeacherAccount());

        return classDO;
    }

    public static List<ClassDTO> conventToClassDTOList(List<ClassDO> classDOList) {
        List<ClassDTO> classDTOList = new ArrayList<ClassDTO>();
        if (classDOList == null) {
            return classDTOList;
        }

        for (ClassDO classDO : classDOList) {
            classDTOList.add(conventToClassDTO(classDO));
        }

        return classDTOList;
    }
}
