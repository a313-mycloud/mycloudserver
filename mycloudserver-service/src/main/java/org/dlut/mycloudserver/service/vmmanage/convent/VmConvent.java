/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage.convent;

import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.dal.dataobject.VmDO;

/**
 * 类VmConvent.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月1日 下午1:14:55
 */
public class VmConvent {

    public static VmDTO conventToVmDTO(VmDO vmDO) {
        if (vmDO == null) {
            return null;
        }

        VmDTO vmDTO = new VmDTO();
        vmDTO.setDesc(vmDO.getDesc());
        vmDTO.setHostId(vmDO.getHostId());
        vmDTO.setImageUuid(vmDO.getImageUuid());
        vmDTO.setVmMemory(vmDO.getVmMemory());
        vmDTO.setVmStatus(VmStatusEnum.getVmStatusByStatus(vmDO.getVmStatus()));
        vmDTO.setVmUuid(vmDO.getVmUuid());
        vmDTO.setVmVcpu(vmDO.getVmVcpu());
        vmDTO.setUserAccount(vmDO.getUserAccount());
        vmDTO.setClassId(vmDO.getClassId());
        vmDTO.setVncPort(vmDO.getVncPort());

        return vmDTO;
    }

    public static VmDO conventToVmDO(VmDTO vmDTO) {
        if (vmDTO == null) {
            return null;
        }

        VmDO vmDO = new VmDO();
        vmDO.setClassId(vmDTO.getClassId());
        vmDO.setDesc(vmDTO.getDesc());
        vmDO.setHostId(vmDTO.getHostId());
        vmDO.setImageUuid(vmDTO.getImageUuid());
        vmDO.setUserAccount(vmDTO.getUserAccount());
        vmDO.setVmMemory(vmDTO.getVmMemory());
        if (vmDTO.getVmStatus() != null) {
            vmDO.setVmStatus(vmDTO.getVmStatus().getStatus());
        }
        vmDO.setVmUuid(vmDTO.getVmUuid());
        vmDO.setVmVcpu(vmDTO.getVmVcpu());
        vmDO.setVncPort(vmDTO.getVncPort());

        return vmDO;
    }
}
