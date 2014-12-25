/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类TemplateVmClassDO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月25日 下午1:26:13
 */
public class TemplateVmClassDO extends BaseDO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 模板镜像的uuid
     */
    private String  templateVmUuid;

    /**
     * 课程的id
     */
    private Integer classId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
