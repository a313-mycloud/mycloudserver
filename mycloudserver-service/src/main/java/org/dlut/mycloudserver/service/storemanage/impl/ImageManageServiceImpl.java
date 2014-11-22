/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.impl;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;
import org.dlut.mycloudserver.service.storemanage.ImageManage;
import org.dlut.mycloudserver.service.storemanage.convent.ImageConvent;
import org.springframework.stereotype.Service;

/**
 * 类ImageManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午3:51:56
 */
@Service("imageManageService")
public class ImageManageServiceImpl implements IImageManageService {

    @Resource
    private ImageManage imageManage;

    @Override
    public MyCloudResult<ImageDTO> getImageByUuid(String imageUuid) {
        ImageDO imageDO = imageManage.getImageByUuid(imageUuid);
        ImageDTO imageDTO = ImageConvent.conventToImageDTO(imageDO);
        if (imageDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_NOT_EXIST);
        }
        return MyCloudResult.successResult(imageDTO);
    }

    @Override
    public MyCloudResult<Boolean> createImage(ImageDTO imageDTO) {
        ImageDO imageDO = ImageConvent.conventToImageDO(imageDTO);
        if (imageDO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        if (!imageManage.createImage(imageDO)) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_CREATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> updateImage(ImageDTO imageDTO) {
        ImageDO imageDO = ImageConvent.conventToImageDO(imageDTO);
        if (imageDO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        if (!imageManage.updateImage(imageDO)) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_UPDATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

}
