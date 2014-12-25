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
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDO;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassQueryCondition;

/**
 * 类TemplateVmClassMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月25日 下午1:28:42
 */
public interface TemplateVmClassMapper {

    /**
     * 将一个模板镜像和课程绑定
     * 
     * @param templateVmUuid
     * @param classId
     * @return
     */
    int addTemplateVmToClass(@Param("templateVmUuid") String templateVmUuid, @Param("classId") int classId);

    /**
     * 根据条件删除
     * 
     * @param templateVmClassDeleteCondition
     * @return
     */
    int delete(TemplateVmClassDeleteCondition templateVmClassDeleteCondition);

    /**
     * 统计数量
     * 
     * @param templateVmClassQueryCondition
     * @return
     */
    int countQuery(TemplateVmClassQueryCondition templateVmClassQueryCondition);

    /**
     * 查询
     * 
     * @param templateVmClassQueryCondition
     * @return
     */
    List<TemplateVmClassDO> query(TemplateVmClassQueryCondition templateVmClassQueryCondition);
}
