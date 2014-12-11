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
     * 创建新的虚拟机，必须设置vmVcpu、vmMemory、imageUuid、userAccount、showType、
     * showPassword，可选：classId、desc
     * 
     * @param vmDTO
     * @return 新创建的vm的uuid
     */
    public MyCloudResult<String> createVm(VmDTO vmDTO);

    /**
     * 克隆虚拟机，必须设置vmVcpu、vmMemory、userAccount、showType、showPassword，
     * 可选：classId、desc
     * 
     * @param destVmDTO
     * @param srcVmUuid
     * @return
     */
    public MyCloudResult<String> cloneVm(VmDTO destVmDTO, String srcVmUuid);

    /**
     * 开启虚拟机
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
     * 删除虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteVm(String vmUuid);
}