/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage.impl;

import static org.junit.Assert.fail;

import java.io.File;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.ShowTypeEnum;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.mycloudserver.common.util.CommonUtil;

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
        vmDTO.setVmName("xp");
        vmDTO.setImageUuid("2d688e07-d654-4bb7-a43b-a95bd222fe5d");
        vmDTO.setVmVcpu(2);
        vmDTO.setVmMemory(2147483648L);
        vmDTO.setUserAccount("admin");
        vmDTO.setShowType(ShowTypeEnum.SPICE);
        vmDTO.setShowPassword("10041104");
        vmDTO.setClassId(0);
        MyCloudResult<String> result = vmManageService.createVm(vmDTO);
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.vmmanage.impl.VmManageServiceImpl#startVm(java.lang.String)}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testStartVm() throws InterruptedException {
        Thread.sleep(10000);
        String vmUuid = "efaf8734-6788-475c-b574-aa41feaca87e";
        MyCloudResult<Boolean> result = vmManageService.startVm(vmUuid);
        printObject(result);
    }

    @Test
    public void testForceCloseVm() throws InterruptedException {
        Thread.sleep(10000);
        String vmUuid = "efaf8734-6788-475c-b574-aa41feaca87e";
        MyCloudResult<Boolean> result = vmManageService.forceShutDownVm(vmUuid);
        printObject(result);
    }

    @Test
    public void testDeleteVm() {
        String vmUuid = "e11e80cf-47e2-4ac5-aff3-78bc87e2ded5";
        MyCloudResult<Boolean> result = vmManageService.deleteVm(vmUuid);
        printObject(result);
    }

    @Test
    public void testDom4j() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File("src/test/resources/test.xml"));
        Element root = document.getRootElement();
        Element device = root.element("devices");
        Element graphics = device.element("graphics");
        printObject(graphics.attributeValue("port"));
    }

    @Test
    public void testVmQuery() {
        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setUserAccount("student");
        MyCloudResult<Pagination<VmDTO>> result = vmManageService.query(queryVmCondition);
        printObject(result);
    }

    @Test
    public void testCloneVm() {
        String srcVmUuid = "04efb738-953f-4c74-b747-cf30ead3321f";
        VmDTO destVmDTO = new VmDTO();
        destVmDTO.setVmVcpu(2);
        destVmDTO.setVmMemory(2147483648L);
        destVmDTO.setUserAccount("teacher");
        destVmDTO.setShowType(ShowTypeEnum.SPICE);
        destVmDTO.setShowPassword("10041104");
        destVmDTO.setClassId(0);
        MyCloudResult<String> result = vmManageService.cloneVm(destVmDTO, srcVmUuid);
        printObject(result);
    }

    @Test
    public void test() {
        printObject(CommonUtil.createUuid());
    }
}
