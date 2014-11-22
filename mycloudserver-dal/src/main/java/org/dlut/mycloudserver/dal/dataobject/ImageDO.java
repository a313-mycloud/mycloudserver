/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.dataobject;

/**
 * 类ImageDO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午2:56:36
 */
public class ImageDO extends BaseDO {

    /**
     * 虚拟机镜像的uuid
     */
    private String  imageUuid;

    /**
     * 虚拟机镜像名称
     */
    private String  imageName;

    /**
     * 虚拟机镜像大小，单位为字节
     */
    private Long    imageSize;

    /**
     * 虚拟机镜像路径
     */
    private String  imagePath;

    /**
     * 虚拟机镜像格式
     */
    private Integer imageFormat;

    /**
     * 虚拟机镜像所依赖的父镜像uuid
     */
    private String  parentImageUuid;

    /**
     * 被其他镜像依赖的次数
     */
    private Integer referenceCount;

    /**
     * 是否已近删除
     */
    private Boolean isDelete;

    /**
     * 是否可以作为模板提供给其他人克隆
     */
    private Boolean isTemplate;

    /**
     * 描述信息
     */
    private String  desc;

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

    public Integer getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(Integer imageFormat) {
        this.imageFormat = imageFormat;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

}
