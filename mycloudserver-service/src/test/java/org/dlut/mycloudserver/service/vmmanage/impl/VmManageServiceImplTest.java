/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage.impl;

import static org.junit.Assert.fail;

import java.util.UUID;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;

/**
 * 类VmManageServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月1日 下午4:06:57
 */
public class VmManageServiceImplTest extends BaseTestCase {

    @Resource(name = "vmManageService")
    private IVmManageService vmManageService;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.vmmanage.impl.VmManageServiceImpl#getVmByUuid(java.lang.String)}
     * .
     */
    @Test
    public void testGetVmByUuid() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.vmmanage.impl.VmManageServiceImpl#createVm(org.dlut.mycloudserver.client.common.vmmanage.VmDTO)}
     * .
     */
    @Test
    public void testCreateVm() {
        VmDTO vmDTO = new VmDTO();
        String vmUuid = UUID.randomUUID().toString();
        printObject(vmUuid);
        vmDTO.setVmUuid(vmUuid);
        vmDTO.setVmVcpu(2);
        vmDTO.setVmMemory((long) (512 * 1024 * 1024));
        vmDTO.setImageUuid("0ca6084d-0d55-4bc8-ba21-0b56951e913a");
        vmDTO.setUserAccount("admin");
        vmDTO.setClassId(1);

        MyCloudResult<Boolean> result = vmManageService.createVm(vmDTO);
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.vmmanage.impl.VmManageServiceImpl#startVm(java.lang.String)}
     * .
     */
    @Test
    public void testStartVm() {
        String vmUuid = "04efb738-953f-4c74-b747-cf30ead3321f";
        MyCloudResult<Boolean> result = vmManageService.startVm(vmUuid);
        printObject(result);
    }

}
