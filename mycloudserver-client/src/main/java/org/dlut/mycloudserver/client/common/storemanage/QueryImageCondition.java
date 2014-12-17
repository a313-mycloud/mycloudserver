/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.storemanage;

import java.io.Serializable;

/**
 * 类QueryImageCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午5:52:34
 */
public class QueryImageCondition implements Serializable {

    private static final long serialVersionUID = 6373399484088311327L;

    /**
     * 镜像uuid
     */
    private String            imageUuid;

    /**
     * 镜像格式
     */
    private StoreFormat       imageFormat;

    /**
     * 父镜像uuid
     */
    private String            parentImageUuid;

    /**
     * 被子镜像引用的次数
     */
    private Integer           referenceCount;

    /**
     * 是否为模板
     */
    private Boolean           isTemplate;

    /**
     * 是否已经删除
     */
    private Boolean           isDelete;

    private int               offset           = 0;

    private int               limit            = 10;

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public StoreFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(StoreFormat imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getParentImageUuid() {
        return parentImageUuid;
    }

    public void setParentImageUuid(String parentImageUuid) {
        this.parentImageUuid = parentImageUuid;
    }

    public Integer getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
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
