/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.client.service.storemanage.IDiskManageService;
import org.dlut.mycloudserver.dal.dataobject.DiskDO;
import org.dlut.mycloudserver.service.storemanage.DiskManage;
import org.dlut.mycloudserver.service.storemanage.convent.DiskConvent;
import org.springframework.stereotype.Service;

/**
 * 类DiskManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午9:06:56
 */
@Service("diskManageService")
public class DiskManageServiceImpl implements IDiskManageService {

    @Resource
    private DiskManage diskManage;

    @Override
    public MyCloudResult<DiskDTO> getDiskByUuid(String diskUuid) {
        if (StringUtils.isBlank(diskUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        DiskDO diskDO = diskManage.getDiskByUuid(diskUuid);
        DiskDTO diskDTO = DiskConvent.conventToDiskDTO(diskDO);
        if (diskDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.DISK_NOT_EXIST);
        }
        return MyCloudResult.successResult(diskDTO);
    }

    @Override
    public MyCloudResult<String> createDisk(DiskDTO diskDTO) {
        return null;
    }

    @Override
    public MyCloudResult<Boolean> updateDisk(DiskDTO diskDTO) {
        return null;
    }

    @Override
    public MyCloudResult<Boolean> deleteDiskByUuid(String diskUuid) {
        return null;
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryDiskCondition queryDiskCondition) {
        return null;
    }

    @Override
    public MyCloudResult<Pagination<DiskDTO>> query(QueryDiskCondition queryDiskCondition) {
        return null;
    }

}
