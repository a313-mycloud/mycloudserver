/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.service.storemanage.IDiskManageService;
import org.dlut.mycloudserver.dal.dataobject.DiskDO;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.storemanage.DiskManage;
import org.dlut.mycloudserver.service.storemanage.convent.DiskConvent;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;
import org.mycloudserver.common.constants.StoreConstants;
import org.mycloudserver.common.util.CommonUtil;
import org.mycloudserver.common.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类DiskManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午9:06:56
 */
@Service("diskManageService")
public class DiskManageServiceImpl implements IDiskManageService {

    private static Logger      log = LoggerFactory.getLogger(DiskManageServiceImpl.class);

    @Resource
    private DiskManage         diskManage;

    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool mutilHostConnPool;

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
        if (diskDTO == null || StringUtils.isBlank(diskDTO.getDiskName())
                || StringUtils.isBlank(diskDTO.getUserAccount()) || diskDTO.getDiskTotalSize() == null
                || diskDTO.getDiskBusType() == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }
        String diskUuid = CommonUtil.createUuid();
        // 利用libvirt创建一个卷并且格式化为ntfs
        if (!physicalCreateDisk(diskUuid, diskDTO.getDiskTotalSize())) {
            return MyCloudResult.failedResult(ErrorEnum.DISK_PHYSICAL_CREATE_FAIL);
        }
        diskDTO.setDiskUuid(diskUuid);
        diskDTO.setDiskPath(StoreConstants.DISK_POOL_PATH + diskUuid);
        // 默认所有格式都是qcow2
        diskDTO.setDiskFormat(StoreFormat.QCOW2);
        if (StringUtils.isBlank(diskDTO.getAttachVmUuid())) {
            diskDTO.setAttachVmUuid("");
        }
        DiskDO diskDO = DiskConvent.conventToDiskDO(diskDTO);
        // 在数据库中创建硬盘
        if (!diskManage.createDisk(diskDO)) {
            // 如果失败，则删除已物理创建的硬盘
            if (!physicalDeleteDisk(diskUuid)) {
                log.error("物理删除硬盘" + diskUuid + "失败");
            }
            return MyCloudResult.failedResult(ErrorEnum.DISK_DB_CREATE_FAIL);
        }
        return MyCloudResult.successResult(diskUuid);
    }

    /**
     * 利用libvirt创建一个卷，并且对卷格式化为ntfs
     * 
     * @param diskUuid
     * @param diskTotalSize
     * @return
     */
    private boolean physicalCreateDisk(String diskUuid, long diskTotalSize) {
        Connection conn = mutilHostConnPool.getLocalConn();
        if (conn == null) {
            log.error("获取libvirt本地连接失败");
            return false;
        }
        try {
            StoragePool diskPool = conn.getStoragePoolByName(StoreConstants.DISK_POOL_NAME);
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("name", diskUuid);
            context.put("uuid", diskUuid);
            context.put("diskSize", diskTotalSize);
            String diskPath = StoreConstants.DISK_POOL_PATH + diskUuid;
            context.put("newDiskPath", diskPath);
            String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.DISK_VOLUME_TEMPLATE_PATH, context);
            diskPool.storageVolCreateXML(xmlDesc, 0);
            // 对新创建的卷进行ntfs格式化
            if (!formatDiskToNtfs(diskPath)) {
                log.error("硬盘格式化失败");
                return false;
            }
        } catch (LibvirtException e) {
            log.error("创建硬盘失败", e);
            return false;
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        return true;
    }

    /**
     * 对硬盘进行ntfs格式化
     * 
     * @param diskPath
     * @return
     */
    private boolean formatDiskToNtfs(String diskPath) {
        //以sudo身份执行命令，该命令需要在后台服务器中需要被配置为可以无密码执行
        String command = "sudo virt-format -a " + diskPath + " --filesystem=ntfs";
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);//该语句用于执行linux命令
            process.waitFor();
            if (process.exitValue() == 0) {
                return true;
            }
        } catch (IOException e) {
            log.error("error message", e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            log.error("error message", e);
        }
        return false;
    }

    private boolean physicalDeleteDisk(String diskUuid) {
        Connection conn = mutilHostConnPool.getLocalConn();
        if (conn == null) {
            log.error("获取libvirt本地连接失败");
            return false;
        }
        try {
            StoragePool pool = conn.getStoragePoolByName(StoreConstants.DISK_POOL_NAME);
            pool.refresh(0);
            StorageVol vol = pool.storageVolLookupByName(diskUuid);
            if (vol == null) {
                log.warn("删除镜像 " + diskUuid + "失败，原因：镜像不存在");
                return false;
            }
            vol.delete(0);
            return true;
        } catch (LibvirtException e) {
            log.error("物理删除硬盘失败", e);
            return false;
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
    }

    @Override
    public MyCloudResult<Boolean> updateDisk(DiskDTO diskDTO) {
        if (diskDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        DiskDO diskDO = DiskConvent.conventToDiskDO(diskDTO);
        if (!diskManage.updateDisk(diskDO)) {
            return MyCloudResult.failedResult(ErrorEnum.DISK_UPDATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteDiskByUuid(String diskUuid) {
        if (StringUtils.isBlank(diskUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!diskManage.deleteDiskByUuid(diskUuid)) {
            log.error("删除硬盘" + diskUuid + "失败");
            return MyCloudResult.failedResult(ErrorEnum.DISK_DB_DELETE_FAIL);
        }
        if (!physicalDeleteDisk(diskUuid)) {
            log.error("物理删除" + diskUuid + "失败");
            return MyCloudResult.failedResult(ErrorEnum.DISK_PHYSICAL_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryDiskCondition queryDiskCondition) {
        if (queryDiskCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int count = diskManage.countQuery(queryDiskCondition);
        return MyCloudResult.successResult(count);
    }

    @Override
    public MyCloudResult<Pagination<DiskDTO>> query(QueryDiskCondition queryDiskCondition) {
        if (queryDiskCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = diskManage.countQuery(queryDiskCondition);
        List<DiskDO> diskDOList = diskManage.query(queryDiskCondition);
        List<DiskDTO> diskDTOList = DiskConvent.conventToDiskDTOList(diskDOList);
        Pagination<DiskDTO> pagination = new Pagination<DiskDTO>(queryDiskCondition.getOffset(),
                queryDiskCondition.getLimit(), totalCount, diskDTOList);
        return MyCloudResult.successResult(pagination);
    }
}
