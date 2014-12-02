/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.vmmanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
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
     * 创建新的虚拟机，需要设置vmUuid、vmVcpu、vmMemory、imageUuid、desc、userAccount，classId可选
     * 
     * @param vmDTO
     * @return
     */
    public MyCloudResult<Boolean> createVm(VmDTO vmDTO);

    /**
     * 开启虚拟机
     * 
     * @param vmUuid
     * @return
     */
    public MyCloudResult<Boolean> startVm(String vmUuid);
}
