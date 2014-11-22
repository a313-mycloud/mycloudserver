/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.imagemanage.impl;

import java.util.UUID;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;

/**
 * 类ImageManageServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午4:30:27
 */
public class ImageManageServiceImplTest extends BaseTestCase {

    @Resource(name = "imageManageService")
    private IImageManageService imageManageService;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.ImageManageServiceImpl#getImageByUuid(java.lang.String)}
     * .
     */
    @Test
    public void testGetImageByUuid() {
        MyCloudResult<ImageDTO> result = imageManageService.getImageByUuid("288f4ee6-42f6-4d8a-8e47-46bce4431ab1");
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.ImageManageServiceImpl#createImage(org.dlut.mycloudserver.client.common.storemanage.ImageDTO)}
     * .
     */
    @Test
    public void testCreateImage() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageFormat(StoreFormat.QCOW2);
        imageDTO.setImageName("test1");
        imageDTO.setImagePath("/home/luojie/xxxx");
        imageDTO.setImageSize(1258686L);
        imageDTO.setImageUuid(UUID.randomUUID().toString());
        imageDTO.setIsTemplagte(true);
        MyCloudResult<Boolean> result = imageManageService.createImage(imageDTO);
        printObject(result);
    }
}
