/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage.impl;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.constants.VmConstants;
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
    public MyCloudResult<Boolean> createVm(VmDTO vmDTO) {
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
        return MyCloudResult.successResult(Boolean.TRUE);
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
        String xmlDesc = TemplateUtil.renderTemplate(VmConstants.VOLUME_TEMPLATE_PATH, context);
        System.out.println(xmlDesc);

        Connection conn = mutilHostConnPool.getLocalConn();
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
            Integer vncPort = displayVnc(domainXmlDesc);
            System.out.println("vnc 端口号为：" + vncPort);
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
     * 获取虚拟机vnc端口号
     * 
     * @param domainXmlDesc
     * @return
     */
    private Integer displayVnc(String domainXmlDesc) {
        if (StringUtils.isBlank(domainXmlDesc)) {
            return null;
        }

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new StringReader(domainXmlDesc));
            Element root = document.getRootElement();
            String strPort = root.element("devices").element("graphics").attributeValue("port");
            int vncPort = Integer.parseInt(strPort);
            return vncPort;
        } catch (DocumentException e) {
            log.error("解析" + domainXmlDesc + "失败", e);
            return null;
        } catch (NumberFormatException e) {
            log.error("vnc端口号不是数字");
            return null;
        }
    }

    @Override
    public MyCloudResult<Boolean> forceShutDownVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }

        return null;
    }
}
