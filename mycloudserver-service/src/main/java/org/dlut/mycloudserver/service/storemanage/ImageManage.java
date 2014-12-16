/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.dlut.mycloudserver.client.common.storemanage.QueryImageCondition;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;
import org.dlut.mycloudserver.dal.mapper.ImageManageMapper;
import org.springframework.stereotype.Service;

/**
 * 类ImageManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午2:55:55
 */
@Service
public class ImageManage {

    @Resource
    private ImageManageMapper imageManageMapper;

    public ImageDO getImageByUuid(String imageUuid, boolean isIncludDeletedImage) {
        if (StringUtils.isBlank(imageUuid)) {
            return null;
        }
        return imageManageMapper.getImageByUuid(imageUuid, isIncludDeletedImage);
    }

    public boolean createImage(ImageDO imageDO) {
        if (imageDO == null) {
            return false;
        }
        return imageManageMapper.createImage(imageDO) == 1 ? true : false;
    }

    public boolean updateImage(ImageDO imageDO) {
        if (imageDO == null) {
            return false;
        }
        return imageManageMapper.updateImage(imageDO) == 1 ? true : false;
    }

    public boolean deleteImageByUuid(String imageUuid) {
        if (StringUtils.isBlank(imageUuid)) {
            return false;
        }
        return imageManageMapper.deleteImageByUuid(imageUuid) == 1 ? true : false;
    }

    /**
     * 统计数量
     * 
     * @param queryImageCondition
     * @return
     */
    public int countQuery(QueryImageCondition queryImageCondition) {
        if (queryImageCondition == null) {
            return 0;
        }
        return imageManageMapper.countQuery(queryImageCondition);
    }

    /**
     * 分页获取列表
     * 
     * @param queryImageCondition
     * @return
     */
    public List<ImageDO> query(QueryImageCondition queryImageCondition) {
        if (queryImageCondition == null) {
            return new ArrayList<ImageDO>();
        }
        return imageManageMapper.query(queryImageCondition);
    }
}
