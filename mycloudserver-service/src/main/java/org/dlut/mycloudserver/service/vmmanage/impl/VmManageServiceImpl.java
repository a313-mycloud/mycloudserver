/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.dal.dataobject.VmDO;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.schedule.IScheduler;
import org.dlut.mycloudserver.service.vmmanage.VmManage;
import org.dlut.mycloudserver.service.vmmanage.convent.VmConvent;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.constants.VmConstants;
import org.mycloudserver.common.util.CommonUtil;
import org.mycloudserver.common.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类VmManageServiceImpl.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月1日 下午1:01:27
 */
@Service("vmManageService")
public class VmManageServiceImpl implements IVmManageService {

    private static Logger       log = LoggerFactory.getLogger(VmManageServiceImpl.class);

    @Resource(name = "vmManage")
    private VmManage            vmManage;

    @Resource(name = "userManageService")
    private IUserManageService  userManageService;

    @Resource(name = "imageManageService")
    private IImageManageService imageManageService;

    @Resource(name = "classManageService")
    private IClassManageService classManageService;

    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool  mutilHostConnPool;

    @Resource(name = "scheduler")
    private IScheduler          scheduler;

    @Override
    public MyCloudResult<VmDTO> getVmByUuid(String vmUuid) {
        VmDO vmDO = vmManage.getVmByUuid(vmUuid);
        VmDTO vmDTO = VmConvent.conventToVmDTO(vmDO);
        if (vmDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }

        return MyCloudResult.successResult(vmDTO);
    }

    @Override
    public MyCloudResult<String> createVm(VmDTO vmDTO) {
        String vmUuid = CommonUtil.createUuid();
        vmDTO.setVmUuid(vmUuid);
        VmDO vmDO = VmConvent.conventToVmDO(vmDTO);
        if (vmDO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<ImageDTO> imageResult = imageManageService.getImageByUuid(vmDTO.getImageUuid(), false);
        if (!imageResult.isSuccess()) {
            log.warn("虚拟机镜像 " + vmDTO.getImageUuid() + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_NOT_EXIST);
        }
        MyCloudResult<UserDTO> userResult = userManageService.getUserByAccount(vmDTO.getUserAccount());
        if (!userResult.isSuccess()) {
            log.warn("用户 " + vmDTO.getUserAccount() + " 不存在");
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_EXIST);
        }
        if (vmDTO.getClassId() != null) {
            MyCloudResult<ClassDTO> classResult = classManageService.getClassById(vmDTO.getClassId());
            if (!classResult.isSuccess()) {
                log.warn("课程 " + vmDTO.getClassId() + "不存在");
                return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
            }
        }
        vmDO.setVmStatus(VmStatusEnum.CLOSED.getStatus());
        if (!vmManage.createVm(vmDO)) {
            log.error("创建虚拟机 " + vmDO + "失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_CREATE_FAIL);
        }
        return MyCloudResult.successResult(vmUuid);
    }

    @Override
    public MyCloudResult<Boolean> startVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> vmResult = getVmByUuid(vmUuid);
        if (!vmResult.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = vmResult.getModel();
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        MyCloudResult<ImageDTO> imageResult = imageManageService.getImageByUuid(vmDTO.getImageUuid(), false);
        if (!imageResult.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_NOT_EXIST);
        }
        ImageDTO imageDTO = imageResult.getModel();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("vmUuid", vmDTO.getVmUuid());
        // 单位为KB
        context.put("vmMemory", vmDTO.getVmMemory() / 1024);
        context.put("vmVcpu", vmDTO.getVmVcpu());
        context.put("imagePath", imageDTO.getImagePath());
        context.put("showType", vmDTO.getShowType());
        context.put("showPassword", vmDTO.getShowPassword());
        String xmlDesc = TemplateUtil.renderTemplate(VmConstants.VOLUME_TEMPLATE_PATH, context);
        System.out.println(xmlDesc);

        Integer bestHostId = scheduler.getBestHostId(vmDTO);
        if (bestHostId == null) {
            log.error("启动虚拟机" + vmDTO + "时，获取最佳物理机id失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_GET_BEST_HOST_FIAL);
        }
        Connection conn = mutilHostConnPool.getConnByHostId(bestHostId);
        if (conn == null) {
            log.error("获取连接失败");
            return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
        }
        try {
            Domain domain = conn.startVm(xmlDesc);
            if (domain == null) {
                log.error("创建虚拟机" + vmUuid + "失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_START_FAIL);
            }
            String domainXmlDesc = domain.getXMLDesc(0);
            System.out.println(domainXmlDesc);
            Integer showPort = CommonUtil.getShowPortFromVmDescXml(domainXmlDesc);
            System.out.println("vnc 端口号为：" + showPort);

            // 在数据库中更新虚拟机
            vmDTO.setVmStatus(VmStatusEnum.RUNNING);
            vmDTO.setHostId(bestHostId);
            vmDTO.setShowPort(showPort);
            if (!updateVmIn(vmDTO)) {
                log.error("在数据库中更新vm失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
            }
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult(ErrorEnum.VM_START_FAIL);
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 强制关闭虚拟机
     */
    @Override
    public MyCloudResult<Boolean> forceShutDownVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();
        if (vmDTO.getVmStatus() == VmStatusEnum.CLOSED) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        Connection conn = mutilHostConnPool.getConnByHostId(vmDTO.getHostId());
        if (conn == null) {
            log.error("获取连接失败");
            return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
        }
        try {
            if (!conn.destroyVm(vmUuid)) {
                return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
            }
            // 在数据库中更新虚拟机状态
            vmDTO.setVmStatus(VmStatusEnum.CLOSED);
            if (!updateVmIn(vmDTO)) {
                log.error("在数据库中更新vm失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
            }
            return MyCloudResult.successResult(Boolean.TRUE);
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
    }

    /**
     * 更新虚拟机
     * 
     * @param vmDTO
     * @return
     */
    private boolean updateVmIn(VmDTO vmDTO) {
        VmDO vmDO = VmConvent.conventToVmDO(vmDTO);
        if (vmDO == null) {
            return false;
        }

        return vmManage.updateVm(vmDO);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int count = vmManage.countQuery(queryVmCondition);
        return MyCloudResult.successResult(count);
    }

    @Override
    public MyCloudResult<Pagination<VmDTO>> query(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = vmManage.countQuery(queryVmCondition);
        List<VmDO> vmDOList = vmManage.query(queryVmCondition);
        List<VmDTO> vmDTOList = VmConvent.coventToVmDTOList(vmDOList);
        Pagination<VmDTO> pagination = new Pagination<VmDTO>(queryVmCondition.getOffset(), queryVmCondition.getLimit(),
                totalCount, vmDTOList);
        return MyCloudResult.successResult(pagination);
    }

    @Override
    public MyCloudResult<String> cloneVm(VmDTO destVmDTO, String srcVmUuid) {
        if (destVmDTO == null || StringUtils.isBlank(srcVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(srcVmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + srcVmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();
        MyCloudResult<ImageDTO> imageResult = imageManageService.getImageByUuid(vmDTO.getImageUuid(), false);
        if (!imageResult.isSuccess()) {
            log.error("虚拟机镜像" + vmDTO.getImageUuid() + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.IMAGE_NOT_EXIST);
        }
        ImageDTO srcImageDTO = imageResult.getModel();
        // 克隆镜像
        // 克隆的镜像沿用父镜像的名称
        MyCloudResult<ImageDTO> cloneImageResult = imageManageService.cloneImage(srcImageDTO.getImageUuid(),
                srcImageDTO.getImageName(), false);
        if (!cloneImageResult.isSuccess()) {
            log.error("虚拟机镜像克隆失败，原因：" + cloneImageResult.getMsgInfo());
            return MyCloudResult.failedResult(cloneImageResult.getMsgCode(), cloneImageResult.getMsgInfo());
        }
        destVmDTO.setImageUuid(cloneImageResult.getModel().getImageUuid());
        destVmDTO.setVmName(vmDTO.getVmName());
        MyCloudResult<String> createResult = createVm(destVmDTO);
        if (!createResult.isSuccess()) {
            log.error("创建虚拟机" + destVmDTO + "失败，原因：" + createResult.getMsgInfo());
            return MyCloudResult.failedResult(createResult.getMsgCode(), createResult.getMsgInfo());
        }
        return MyCloudResult.successResult(createResult.getModel());
    }

    @Override
    public MyCloudResult<Boolean> deleteVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + vmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();
        MyCloudResult<Boolean> deleteImageResult = imageManageService.deleteImageByUuid(vmDTO.getImageUuid());
        if (!deleteImageResult.isSuccess()) {
            log.error("删除虚拟机镜像" + vmDTO.getImageUuid() + "失败，原因：" + deleteImageResult.getMsgInfo());
            return MyCloudResult.failedResult(deleteImageResult.getMsgCode(), deleteImageResult.getMsgInfo());
        }
        if (!vmManage.deleteVmByUuid(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> updateVm(VmDTO vmDTO) {
        if (vmDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!updateVmIn(vmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }
}
