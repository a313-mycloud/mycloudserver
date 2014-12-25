/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.classmanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDO;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassDeleteCondition;
import org.dlut.mycloudserver.dal.dataobject.TemplateVmClassQueryCondition;
import org.dlut.mycloudserver.dal.mapper.TemplateVmClassMapper;
import org.springframework.stereotype.Service;

/**
 * 类TemplateVmClassManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月25日 下午2:00:24
 */
@Service
public class TemplateVmClassManage {

    @Resource
    private TemplateVmClassMapper templateVmClassMapper;

    /**
     * 将一个模板镜像和课程绑定
     * 
     * @param templateVmUuid
     * @param classId
     * @return
     */
    public boolean addTemplateVmToClass(String templateVmUuid, int classId) {
        if (StringUtils.isBlank(templateVmUuid) && classId <= 0) {
            return false;
        }
        return templateVmClassMapper.addTemplateVmToClass(templateVmUuid, classId) == 1;
    }

    /**
     * 根据条件删除
     * 
     * @param templateVmClassDeleteCondition
     * @return
     */
    public boolean delete(TemplateVmClassDeleteCondition templateVmClassDeleteCondition) {
        if (templateVmClassDeleteCondition == null) {
            return false;
        }
        templateVmClassMapper.delete(templateVmClassDeleteCondition);
        return true;
    }

    /**
     * 统计数量
     * 
     * @param templateVmClassQueryCondition
     * @return
     */
    public int countQuery(TemplateVmClassQueryCondition templateVmClassQueryCondition) {
        if (templateVmClassQueryCondition == null) {
            return 0;
        }
        return templateVmClassMapper.countQuery(templateVmClassQueryCondition);
    }

    /**
     * 查询
     * 
     * @param templateVmClassQueryCondition
     * @return
     */
    public List<TemplateVmClassDO> query(TemplateVmClassQueryCondition templateVmClassQueryCondition) {
        if (templateVmClassQueryCondition == null) {
            return new ArrayList<TemplateVmClassDO>();
        }
        return templateVmClassMapper.query(templateVmClassQueryCondition);
    }
}
