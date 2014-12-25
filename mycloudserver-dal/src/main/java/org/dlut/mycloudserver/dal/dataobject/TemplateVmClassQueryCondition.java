/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类TemplateVmClassQueryCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月25日 下午1:40:17
 */
public class TemplateVmClassQueryCondition {

    private Integer id;

    private String  templateVmUuid;

    private Integer classId;

    /**
     * 偏移量
     */
    private int     offset = 0;

    /**
     * 每页大小
     */
    private int     limit  = 10;

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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
