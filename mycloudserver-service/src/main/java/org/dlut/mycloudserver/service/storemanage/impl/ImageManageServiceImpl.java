/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;
import org.dlut.mycloudserver.service.storemanage.ImageManage;
import org.dlut.mycloudserver.service.storemanage.convent.ImageConvent;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.mycloudserver.common.constants.StoreConstants;
import org.mycloudserver.common.util.CommonUtil;
import org.mycloudserver.common.util.FileUtil;
import org.mycloudserver.common.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类ImageManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午3:51:56
 */
@Service("imageManageService")
public class ImageManageServiceImpl implements IImageManageService {

    private static Logger log = LoggerFactory.getLogger(ImageManageServiceImpl.class);

    @Resource
    private ImageManage   imageManage;

    /**
     * 根据镜像的uuid获取镜像信息
     */
    @Override
    public MyCloudResult<ImageDTO> getImageByUuid(String imageUuid) {
        ImageDO imageDO = imageManage.getImageByUuid(imageUuid);
        ImageDTO imageDTO = ImageConvent.conventToImageDTO(imageDO);
        if (imageDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_NOT_EXIST);
        }
        return MyCloudResult.successResult(imageDTO);
    }

    /**
     * 创建一个新的镜像，只需要设置imageName、imageUuid、imagePath以及isTemplate这几个属性即可
     */
    @Override
    public MyCloudResult<Boolean> createImage(ImageDTO imageDTO) {
        if (imageDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        // 校验文件是否存在
        File imageFile = new File(imageDTO.getImagePath());
        if (!imageFile.isFile()) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_FILE_NOT_EXIST);
        }
        // 获取镜像的格式和总大小
        Object[] result = FileUtil.getStoreFormatAndSize(imageDTO.getImagePath());
        if (result == null) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_GET_FORMAT_SIZE_FAIL);
        }
        if (result[0] == null) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_FORMAT_UNKNOW);
        }
        imageDTO.setImageFormat((StoreFormat) result[0]);
        imageDTO.setImageTotalSize((Long) result[1]);

        ImageDO imageDO = ImageConvent.conventToImageDO(imageDTO);
        if (!imageManage.createImage(imageDO)) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_CREATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 更新一个镜像
     */
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

    /**
     * 快速克隆一个虚拟机镜像
     */
    @Override
    public MyCloudResult<ImageDTO> cloneImage(String srcImageUuid, String destImageName, boolean isTemplate) {
        MyCloudResult<ImageDTO> result = this.getImageByUuid(srcImageUuid);
        if (!result.isSuccess()) {
            log.warn("获取待克隆的镜像 " + srcImageUuid + " 失败");
            return MyCloudResult.failedResult(result.getMsgCode(), result.getMsgInfo());
        }
        ImageDTO srcImageDTO = result.getModel();
        if (srcImageDTO.getImageFormat() != StoreFormat.QCOW2) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_CLONE_FORMAT_INVAILD);
        }
        String newImageUuid = CommonUtil.createUuid();
        String newImagePath = StoreConstants.STOREPOOL_PATH + newImageUuid;
        try {
            // 克隆镜像
            // TODO
            Connect connect = new Connect("qemu:///system");
            StoragePool pool = connect.storagePoolLookupByName(StoreConstants.STOREPOOL_NAME);
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("name", newImageUuid);
            context.put("uuid", newImageUuid);
            context.put("newImagePath", newImagePath);
            context.put("srcImagePath", srcImageDTO.getImagePath());
            String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.VOLUME_TEMPLATE_PATH, context);
            System.out.println(xmlDesc);
            pool.storageVolCreateXML(xmlDesc, 0);

        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult("-1", e.getMessage());
        }
        // 在数据库中记录新生成的镜像
        ImageDTO newImageDTO = new ImageDTO();
        newImageDTO.setImageUuid(newImageUuid);
        newImageDTO.setImageName(destImageName);
        newImageDTO.setImagePath(newImagePath);
        newImageDTO.setIsTemplate(isTemplate);
        newImageDTO.setParentImageUuid(srcImageDTO.getImageUuid());
        MyCloudResult<Boolean> createResult = this.createImage(newImageDTO);
        if (!createResult.isSuccess()) {
            return MyCloudResult.failedResult(createResult.getMsgCode(), createResult.getMsgInfo());
        }
        // 更新父镜像的依赖数量
        srcImageDTO.setReferenceCount(srcImageDTO.getReferenceCount() + 1);
        MyCloudResult<Boolean> updateResult = this.updateImage(srcImageDTO);
        if (!updateResult.isSuccess()) {
            return MyCloudResult.failedResult(updateResult.getMsgCode(), updateResult.getMsgInfo());
        }
        return MyCloudResult.successResult(newImageDTO);
    }
}
