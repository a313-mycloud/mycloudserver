/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.storemanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;

/**
 * 类IImageManageService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月27日 下午10:33:54
 */
public interface IImageManageService {

    /**
     * 根据镜像的uuid获取镜像信息
     * 
     * @param imageUuid
     * @return
     */
    public MyCloudResult<ImageDTO> getImageByUuid(String imageUuid, boolean isIncludDeletedImage);

    /**
     * 创建一个新的镜像，只需要设置imageName、imageUuid、imagePath以及isTemplate这几个属性即可
     * 
     * @param imageDTO
     * @return
     */
    public MyCloudResult<Boolean> createImage(ImageDTO imageDTO);

    /**
     * 更新一个镜像
     * 
     * @param imageDTO
     * @return
     */
    public MyCloudResult<Boolean> updateImage(ImageDTO imageDTO);

    /**
     * 快速克隆一个虚拟机镜像
     * 
     * @param srcImageUuid
     * @param destImageName
     * @param isTemplate 是否作为模板
     * @return 克隆后的虚拟机镜像信息
     */
    public MyCloudResult<ImageDTO> cloneImage(String srcImageUuid, String destImageName, boolean isTemplate);

    /**
     * 根据uuid删除镜像
     * 
     * @param imageUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteImageByUuid(String imageUuid);
}
