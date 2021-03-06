/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.storemanage;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * 类ImageDTO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午3:53:44
 */
public class ImageDTO extends BaseDTO {

    private static final long serialVersionUID = 6185861981820735034L;

    /**
     * 虚拟机镜像uuid
     */
    private String            imageUuid;

    /**
     * 虚拟机镜像名称
     */
    private String            imageName;

    /**
     * 虚拟机镜像的总大小，单位字节
     */
    private Long              imageTotalSize;

    /**
     * 虚拟机镜像已使用的大小，单位字节
     */
    private Long              imageUsedSize;

    /**
     * 虚拟机镜像路径
     */
    private String            imagePath;

    /**
     * 虚拟机镜像格式
     */
    private StoreFormat       imageFormat;

    /**
     * 父镜像uuid
     */
    private String            parentImageUuid;

    /**
     * 被其他镜像依赖的次数
     */
    private Integer           referenceCount;

    /**
     * 是否已近删除
     */
    private Boolean           isDelete;

    /**
     * 是否是模板
     */
    private Boolean           isTemplate;

    /**
     * 描述信息
     */
    private String            desc;

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
        if (referenceCount >= 0) {
            this.referenceCount = referenceCount;
        } else {
            this.referenceCount = 0;
        }
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getImageTotalSize() {
        return imageTotalSize;
    }

    public void setImageTotalSize(Long imageTotalSize) {
        this.imageTotalSize = imageTotalSize;
    }

    public Long getImageUsedSize() {
        return imageUsedSize;
    }

    public void setImageUsedSize(Long imageUsedSize) {
        this.imageUsedSize = imageUsedSize;
    }

}
