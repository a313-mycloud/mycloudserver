/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.vmmanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;

/**
 * 类IVMManageService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月27日 下午10:34:59
 */
public interface IVmManageService {

    /**
     * 根据uuid获取虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<VmDTO> getVmByUuid(String vmUuid);

    /**
     * 创建新的虚拟机，必须设置vmName, vmVcpu、vmMemory、imageUuid、userAccount、showType、
     * showPassword ，classId(0表示没有课程),parentVmUuid(如果没有，则设为“”),isTemplateVm,
     * isPublicTemplate, masterDiskBusType, interfaceType 可选：desc
     */
    public MyCloudResult<String> createVm(VmDTO vmDTO);

    /**
     * 克隆虚拟机，必须设置vmName,
     * vmVcpu、vmMemory、userAccount、showType、showPassword，classId,
     * isTemplateVM,isPublicTemplate,vmNetworkType 可选：desc
     * 
     * @param destVmDTO
     * @param srcVmUuid
     * @return
     */
    public MyCloudResult<String> cloneVm(VmDTO destVmDTO, String srcVmUuid);

    /**
     * 开启虚拟机，模板镜像不能启动虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> startVm(String vmUuid);

    /**
     * 强制关闭虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> forceShutDownVm(String vmUuid);

    /**
     * 根据条件统计虚拟机个数
     * 
     * @param queryVmCondition
     * @return
     */
    public MyCloudResult<Integer> countQuery(QueryVmCondition queryVmCondition);

    /**
     * 根据条件查询虚拟机列表
     * 
     * @param queryVmCondition
     * @return
     */
    public MyCloudResult<Pagination<VmDTO>> query(QueryVmCondition queryVmCondition);

    /**
     * 删除虚拟机，此接口会将虚拟机与硬盘的关系解除，如果此虚拟机为模板虚拟机，此接口还会将模板虚拟机与课程的关系解除，
     * 同时将所有从此模板虚拟机克隆出来的虚拟机全部删除
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteVm(String vmUuid);

    /**
     * 此接口功能有问题，不能使用
     * 
     * @param classId
     * @return
     */
    public MyCloudResult<Boolean> deleteVmByClassId(int classId);

    /**
     * 此接口功能有问题，不能使用
     * 
     * @param userAccount
     * @return
     */
    public MyCloudResult<Boolean> deleteVmByUserAccount(String userAccount);

    /**
     * 更新虚拟机
     * 
     * @param vmDTO
     * @return
     */
    public MyCloudResult<Boolean> updateVm(VmDTO vmDTO);

    /**
     * 在数据库中将硬盘绑定到虚拟机，如果此时虚拟机正在运行，则会将硬盘挂载到虚拟机上
     * 
     * @param vmUuid
     * @param diskUuid
     * @return
     */
    public MyCloudResult<Boolean> attachDisk(String vmUuid, String diskUuid);

    /**
     * 在数据库中将硬盘和虚拟机解绑定，如果此时虚拟机正在运行，则会将硬盘从虚拟机中卸载
     * 
     * @param diskUuid
     * @return
     */
    public MyCloudResult<Boolean> detachDisk(String diskUuid);

    /**
     * 将vmUuid下的所有硬盘卸载
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> detachAllDiskFromVm(String vmUuid);

    /**
     * 将虚拟机转化为模板虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> changeToTemplateVm(String vmUuid);

    /**
     * 将虚拟机转化为公有模板虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> changeToPublicTemplateVm(String vmUuid);

    /**
     * 将模板虚拟机变为非模板虚拟机，此接口会将所有从该模板虚拟机克隆的虚拟机全部删除
     * 
     * @param templateVmUuid
     * @return
     */
    public MyCloudResult<Boolean> changeToNonTempalteVm(String templateVmUuid);

    public MyCloudResult<StoreFormat> getImageFormat(String filePath);
}
