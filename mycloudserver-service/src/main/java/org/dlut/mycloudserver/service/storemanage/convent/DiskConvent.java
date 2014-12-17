/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.convent;

import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.dal.dataobject.DiskDO;
import org.mycloudserver.common.util.FileUtil;

/**
 * 类DiskConvent.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午9:10:16
 */
public class DiskConvent {

    public static DiskDTO conventToDiskDTO(DiskDO diskDO) {
        if (diskDO == null) {
            return null;
        }

        DiskDTO diskDTO = new DiskDTO();
        diskDTO.setAttachVmUuid(diskDO.getAttachVmUuid());
        diskDTO.setDesc(diskDO.getDesc());
        diskDTO.setDiskFormat(StoreFormat.getStoreFormatByValue(diskDO.getDiskFormat()));
        diskDTO.setDiskName(diskDO.getDiskName());
        diskDTO.setDiskTotalSize(diskDO.getDiskTotalSize());
        diskDTO.setDiskUsedSize(FileUtil.getFileSize(diskDO.getDiskPath()));
        diskDTO.setDiskUuid(diskDO.getDiskUuid());
        diskDTO.setUserAccount(diskDO.getUserAccount());
        diskDTO.setDiskPath(diskDO.getDiskPath());

        return diskDTO;
    }

    public static DiskDO conventToDiskDO(DiskDTO diskDTO) {
        if (diskDTO == null) {
            return null;
        }

        DiskDO diskDO = new DiskDO();
        diskDO.setAttachVmUuid(diskDTO.getAttachVmUuid());
        diskDO.setDesc(diskDTO.getDesc());
        if (diskDTO.getDiskFormat() != null) {
            diskDO.setDiskFormat(diskDTO.getDiskFormat().getValue());
        }
        diskDO.setDiskName(diskDTO.getDiskName());
        diskDO.setDiskPath(diskDTO.getDiskPath());
        diskDO.setDiskTotalSize(diskDTO.getDiskTotalSize());
        diskDO.setDiskUuid(diskDTO.getDiskUuid());
        diskDO.setUserAccount(diskDTO.getUserAccount());

        return diskDO;
    }
}
