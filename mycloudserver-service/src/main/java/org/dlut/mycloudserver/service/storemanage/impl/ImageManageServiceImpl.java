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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryImageCondition;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.storemanage.ImageManage;
import org.dlut.mycloudserver.service.storemanage.convent.ImageConvent;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;
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

    private static Logger      log = LoggerFactory.getLogger(ImageManageServiceImpl.class);

    @Resource
    private ImageManage        imageManage;

    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool mutilHostConnPool;

    /**
     * 根据镜像的uuid获取镜像信息
     */
    @Override
    public MyCloudResult<ImageDTO> getImageByUuid(String imageUuid, boolean isIncludDeletedImage) {
        ImageDO imageDO = imageManage.getImageByUuid(imageUuid, isIncludDeletedImage);
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
        MyCloudResult<ImageDTO> result = this.getImageByUuid(srcImageUuid, false);
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
        Connection connect = null;
        try {
            // 克隆镜像
            connect = mutilHostConnPool.getLocalConn();
            if (connect == null) {
                return MyCloudResult.failedResult(ErrorEnum.GET_LOCAL_CONN);
            }
            StoragePool pool = connect.getStoragePoolByName(StoreConstants.IMAGE_POOL_NAME);
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("name", newImageUuid);
            context.put("uuid", newImageUuid);
            context.put("newImagePath", newImagePath);
            context.put("srcImagePath", srcImageDTO.getImagePath());
            context.put("imageSize", srcImageDTO.getImageTotalSize());
            String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.VOLUME_TEMPLATE_PATH, context);
            pool.storageVolCreateXML(xmlDesc, 0);
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult("-1", e.getMessage());
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (LibvirtException e) {
                    log.error("error message", e);
                }
            }
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
            log.warn("在数据库中创建镜像失败，原因：" + createResult.getMsgInfo());
            return MyCloudResult.failedResult(createResult.getMsgCode(), createResult.getMsgInfo());
        }
        // 更新父镜像的依赖数量
        srcImageDTO.setReferenceCount(srcImageDTO.getReferenceCount() + 1);
        MyCloudResult<Boolean> updateResult = this.updateImage(srcImageDTO);
        if (!updateResult.isSuccess()) {
            log.warn("在数据库中更新镜像失败，原因：" + updateResult.getMsgInfo());
            return MyCloudResult.failedResult(updateResult.getMsgCode(), updateResult.getMsgInfo());
        }
        return MyCloudResult.successResult(newImageDTO);
    }

    @Override
    public MyCloudResult<Boolean> deleteImageByUuid(String imageUuid) {
        MyCloudResult<ImageDTO> result = this.getImageByUuid(imageUuid, false);
        if (!result.isSuccess()) {
            return MyCloudResult.failedResult(result.getMsgCode(), result.getMsgInfo());
        }
        ImageDTO needImageDTO = result.getModel();
        // 如果引用为0，并且不是模板，允许物理删除
        if (needImageDTO.getReferenceCount() == 0 && !needImageDTO.getIsTemplate()) {
            if (!physicalDeleteImage(needImageDTO)) {
                log.warn("物理删除镜像 " + needImageDTO + "失败");
                return MyCloudResult.failedResult(ErrorEnum.IMAGE_PHYSICAL_DELETE_FAIL);
            }
        } else {
            // 将状态is_delete变为true
            needImageDTO.setIsDelete(Boolean.TRUE);
            MyCloudResult<Boolean> updateResult = this.updateImage(needImageDTO);
            if (!updateResult.isSuccess()) {
                return MyCloudResult.failedResult(updateResult.getMsgCode(), updateResult.getMsgInfo());
            }
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    //    /**
    //     * 判断镜像是否和虚拟机绑定
    //     * 
    //     * @param imageUuid
    //     * @return
    //     */
    //    private boolean isBindVm(String imageUuid) {
    //        QueryVmCondition queryVmCondition = new QueryVmCondition();
    //        queryVmCondition.setImageUuid(imageUuid);
    //        MyCloudResult<Integer> result = vmManageService.countQuery(queryVmCondition);
    //        if (!result.isSuccess()) {
    //            log.error("查询虚拟机数量失败");
    //            return false;
    //        }
    //        if (result.getModel() == 0) {
    //            return false;
    //        }
    //        return true;
    //    }

    /**
     * 物理删除镜像，包括删除文件以及数据库中的记录
     * 
     * @param imageDTO
     * @return
     */
    private boolean physicalDeleteImage(ImageDTO imageDTO) {
        if (imageDTO == null) {
            return false;
        }
        // 删除镜像文件
        if (!deleteVolByLibvirt(imageDTO.getImageUuid())) {
            log.warn("删除镜像文件 " + imageDTO.getImagePath() + "失败");
            return false;
        }
        // 删除数据库记录
        if (!imageManage.deleteImageByUuid(imageDTO.getImageUuid())) {
            log.warn("删除镜像" + imageDTO.getImageUuid() + " 数据库记录失败");
            return false;
        }
        // 如果待删除的镜像有父镜像，则父镜像的引用值减一
        if (!StringUtils.isBlank(imageDTO.getParentImageUuid())) {
            if (!decreaseImageReferenceCount(imageDTO.getParentImageUuid())) {
                log.warn("对父镜像 " + imageDTO.getParentImageUuid() + " 的引用值减一失败");
                return false;
            }
        }
        return true;
    }

    /**
     * 减少镜像的引用值，如果镜像的is_delete为true，并且引用值为0，则可以物理删除
     * 
     * @param imageUuid
     * @return
     */
    private boolean decreaseImageReferenceCount(String imageUuid) {
        MyCloudResult<ImageDTO> result = this.getImageByUuid(imageUuid, true);
        if (!result.isSuccess()) {
            log.warn("");
            return false;
        }
        ImageDTO imageDTO = result.getModel();
        imageDTO.setReferenceCount(imageDTO.getReferenceCount() - 1);
        // 如果被标记为已删除，并且引用值为0，且不是模板，则可以删除
        if (imageDTO.getIsDelete() && imageDTO.getReferenceCount() == 0 && !imageDTO.getIsTemplate()) {
            // 物理删除
            if (!physicalDeleteImage(imageDTO)) {
                return false;
            }
        } else {
            MyCloudResult<Boolean> updateResult = this.updateImage(imageDTO);
            if (!updateResult.isSuccess()) {
                log.warn("更新镜像 " + imageDTO + " 失败，原因：" + updateResult.getMsgInfo());
                return false;
            }
        }
        return true;

    }

    /**
     * 使用libvirt库删除卷
     * 
     * @param imageUuid
     * @return
     */
    private boolean deleteVolByLibvirt(String imageUuid) {
        Connection conn = null;
        try {
            conn = mutilHostConnPool.getLocalConn();
            if (conn == null) {
                return false;
            }
            StoragePool pool = conn.getStoragePoolByName(StoreConstants.IMAGE_POOL_NAME);
            pool.refresh(0);
            StorageVol vol = pool.storageVolLookupByName(imageUuid);
            if (vol == null) {
                log.warn("删除镜像 " + imageUuid + "失败，原因：镜像不存在");
                return false;
            }
            vol.delete(0);
            return true;
        } catch (LibvirtException e) {
            log.error("error message", e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (LibvirtException e) {
                    log.error("error message", e);
                }
            }
        }
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryImageCondition queryImageCondition) {
        if (queryImageCondition == null) {
            MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = imageManage.countQuery(queryImageCondition);
        return MyCloudResult.successResult(totalCount);
    }

    @Override
    public MyCloudResult<Pagination<ImageDTO>> query(QueryImageCondition queryImageCondition) {
        if (queryImageCondition == null) {
            MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = imageManage.countQuery(queryImageCondition);
        List<ImageDO> imageDOList = imageManage.query(queryImageCondition);
        List<ImageDTO> imageDTOList = ImageConvent.conventToImageDTOList(imageDOList);
        Pagination<ImageDTO> pagination = new Pagination<ImageDTO>(queryImageCondition.getOffset(),
                queryImageCondition.getLimit(), totalCount, imageDTOList);
        return MyCloudResult.successResult(pagination);
    }
}
