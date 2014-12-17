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

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.dal.dataobject.DiskDO;
import org.dlut.mycloudserver.dal.mapper.DiskManageMapper;
import org.springframework.stereotype.Service;

/**
 * 类DiskManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午8:34:35
 */
@Service
public class DiskManage {

    @Resource
    private DiskManageMapper diskManageMapper;

    /**
     * 根据uuid获取硬盘
     * 
     * @param diskUuid
     * @return
     */
    public DiskDO getDiskByUuid(String diskUuid) {
        if (StringUtils.isBlank(diskUuid)) {
            return null;
        }
        return diskManageMapper.getDiskByUuid(diskUuid);
    }

    /**
     * 创建一个新的硬盘
     * 
     * @param diskDO
     * @return
     */
    public boolean createDisk(DiskDO diskDO) {
        if (diskDO == null) {
            return false;
        }
        return diskManageMapper.createDisk(diskDO) == 1 ? true : false;
    }

    /**
     * 更新镜像
     * 
     * @param diskDO
     * @return
     */
    public boolean updateDisk(DiskDO diskDO) {
        if (diskDO == null) {
            return false;
        }
        return diskManageMapper.updateDisk(diskDO) == 1 ? true : false;
    }

    /**
     * 删除硬盘
     * 
     * @param diskUuid
     * @return
     */
    public boolean deleteDiskByUuid(String diskUuid) {
        if (StringUtils.isBlank(diskUuid)) {
            return false;
        }
        return diskManageMapper.deleteDiskByUuid(diskUuid) == 1 ? true : false;
    }

    /**
     * 统计数量
     * 
     * @param queryDiskCondition
     * @return
     */
    public int countQuery(QueryDiskCondition queryDiskCondition) {
        if (queryDiskCondition == null) {
            return 0;
        }
        return diskManageMapper.countQuery(queryDiskCondition);
    }

    /**
     * 分页查询硬盘列表
     * 
     * @param queryDiskCondition
     * @return
     */
    public List<DiskDO> query(QueryDiskCondition queryDiskCondition) {
        if (queryDiskCondition == null) {
            return new ArrayList<DiskDO>();
        }
        return diskManageMapper.query(queryDiskCondition);
    }
}
