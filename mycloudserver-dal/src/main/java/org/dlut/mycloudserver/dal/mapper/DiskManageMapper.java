/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.dal.dataobject.DiskDO;

/**
 * 类DiskManageMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午4:11:45
 */
public interface DiskManageMapper {

    /**
     * 根据uuid获取硬盘
     * 
     * @param diskUuid
     * @return
     */
    DiskDO getDiskByUuid(@Param("diskUuid") String diskUuid);

    /**
     * 创建一个新的硬盘
     * 
     * @param diskDO
     * @return
     */
    int createDisk(DiskDO diskDO);

    /**
     * 更新镜像
     * 
     * @param diskDO
     * @return
     */
    int updateDisk(DiskDO diskDO);

    /**
     * 删除硬盘
     * 
     * @param diskUuid
     * @return
     */
    int deleteDiskByUuid(@Param("diskUuid") String diskUuid);

    /**
     * 统计数量
     * 
     * @param queryDiskCondition
     * @return
     */
    int countQuery(QueryDiskCondition queryDiskCondition);

    /**
     * 分页查询硬盘列表
     * 
     * @param queryDiskCondition
     * @return
     */
    List<DiskDO> query(QueryDiskCondition queryDiskCondition);
}
