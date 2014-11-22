/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.convent;

import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;

/**
 * 类ImageConvent.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午4:18:54
 */
public class ImageConvent {

    public static ImageDTO conventToImageDTO(ImageDO imageDO) {
        if (imageDO == null) {
            return null;
        }
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setDesc(imageDO.getDesc());
        imageDTO.setImageFormat(StoreFormat.getStoreFormatByValue(imageDO.getImageFormat()));
        imageDTO.setImageName(imageDO.getImageName());
        imageDTO.setImagePath(imageDO.getImagePath());
        imageDTO.setImageSize(imageDO.getImageSize());
        imageDTO.setImageUuid(imageDO.getImageUuid());
        imageDTO.setIsDelete(imageDO.getIsDelete());
        imageDTO.setIsTemplagte(imageDO.getIsTemplate());
        imageDTO.setParentImageUuid(imageDO.getParentImageUuid());
        imageDTO.setReferenceCount(imageDO.getReferenceCount());

        return imageDTO;
    }

    public static ImageDO conventToImageDO(ImageDTO imageDTO) {
        if (imageDTO == null) {
            return null;
        }

        ImageDO imageDO = new ImageDO();
        imageDO.setDesc(imageDTO.getDesc());
        if (imageDTO.getImageFormat() != null) {
            imageDO.setImageFormat(imageDTO.getImageFormat().getValue());
        }
        imageDO.setImageName(imageDTO.getImageName());
        imageDO.setImagePath(imageDTO.getImagePath());
        imageDO.setImageSize(imageDTO.getImageSize());
        imageDO.setImageUuid(imageDTO.getImageUuid());
        imageDO.setIsDelete(imageDTO.getIsDelete());
        imageDO.setIsTemplate(imageDTO.getIsTemplagte());
        imageDO.setParentImageUuid(imageDTO.getParentImageUuid());
        imageDO.setReferenceCount(imageDTO.getReferenceCount());

        return imageDO;
    }
}
