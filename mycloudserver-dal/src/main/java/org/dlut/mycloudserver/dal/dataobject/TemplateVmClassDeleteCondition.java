/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类TemplateVmClassDeleteCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月25日 下午1:37:34
 */
public class TemplateVmClassDeleteCondition {

    private String  templateVmUuid;

    private Integer classId;

    public String getTemplateVmUuid() {
        return templateVmUuid;
    }

    public void setTemplateVmUuid(String templateVmUuid) {
        this.templateVmUuid = templateVmUuid;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

}
