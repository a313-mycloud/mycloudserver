/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudserver.dal.dataobject.ImageDO;

/**
 * 类ImageManageMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午3:11:41
 */
public interface ImageManageMapper {

    /**
     * 根据uuid获取对应的虚拟机镜像
     * 
     * @param imageUuid
     * @return
     */
    ImageDO getImageByUuid(@Param("imageUuid") String imageUuid);

    /**
     * 创建一个新的虚拟机镜像
     * 
     * @param imageDO
     * @return
     */
    int createImage(ImageDO imageDO);

    /**
     * 更新虚拟机镜像
     * 
     * @param imageDO
     * @return
     */
    int updateImage(ImageDO imageDO);

    /**
     * 删除指定的虚拟机镜像
     * 
     * @param imageUuid
     * @return
     */
    int deleteImageByUuid(@Param("imageUuid") String imageUuid);

}
