/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.diskmanage.impl;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.client.service.storemanage.IDiskManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;

/**
 * 类DiskManageServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月18日 下午8:53:14
 */
public class DiskManageServiceImplTest extends BaseTestCase {

    @Resource(name = "diskManageService")
    private IDiskManageService diskManageService;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#getDiskByUuid(java.lang.String)}
     * .
     */
    @Test
    public void testGetDiskByUuid() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#createDisk(org.dlut.mycloudserver.client.common.storemanage.DiskDTO)}
     * .
     */
    @Test
    public void testCreateDisk() {
        DiskDTO diskDTO = new DiskDTO();
        diskDTO.setDiskName("test");
        diskDTO.setUserAccount("student");
        diskDTO.setDiskTotalSize(2147483648L);
        MyCloudResult<String> result = diskManageService.createDisk(diskDTO);
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#updateDisk(org.dlut.mycloudserver.client.common.storemanage.DiskDTO)}
     * .
     */
    @Test
    public void testUpdateDisk() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#deleteDiskByUuid(java.lang.String)}
     * .
     */
    @Test
    public void testDeleteDiskByUuid() {
        String diskUuid = "5d9130a3-d85c-46fa-a540-59c6243ef7e1";
        MyCloudResult<Boolean> result = diskManageService.deleteDiskByUuid(diskUuid);
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#countQuery(org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition)}
     * .
     */
    @Test
    public void testCountQuery() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.DiskManageServiceImpl#query(org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition)}
     * .
     */
    @Test
    public void testQuery() {
        QueryDiskCondition queryDiskCondition = new QueryDiskCondition();
        queryDiskCondition.setUserAccount("student");
        queryDiskCondition.setLimit(5);
        queryDiskCondition.setOffset(0);
        MyCloudResult<Pagination<DiskDTO>> result = diskManageService.query(queryDiskCondition);
        printObject(result);
    }

}
