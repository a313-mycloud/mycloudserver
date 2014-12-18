/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.storemanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;

/**
 * 类IDiskManageService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月27日 下午10:33:21
 */
public interface IDiskManageService {

    /**
     * 根据uuid获取硬盘信息
     * 
     * @param diskUuid
     * @return
     */
    public MyCloudResult<DiskDTO> getDiskByUuid(String diskUuid);

    /**
     * 创建新的硬盘，必须设置：diskName、diskTotalSize、userAccount 可选：attachVmUuid、desc
     * 
     * @param diskDTO
     * @return 返回新的硬盘的uuid
     */
    public MyCloudResult<String> createDisk(DiskDTO diskDTO);

    /**
     * 更新硬盘，只能更新diskName、attachVmUuid、desc
     * 
     * @param diskDTO
     * @return
     */
    public MyCloudResult<Boolean> updateDisk(DiskDTO diskDTO);

    /**
     * 根据uuid删除硬盘
     * 
     * @param diskUuid
     * @return
     */
    public MyCloudResult<Boolean> deleteDiskByUuid(String diskUuid);

    /**
     * 根据条件统计硬盘数量
     * 
     * @param queryDiskCondition
     * @return
     */
    public MyCloudResult<Integer> countQuery(QueryDiskCondition queryDiskCondition);

    /**
     * 根据条件分页查询硬盘列表
     * 
     * @param queryDiskCondition
     * @return
     */
    public MyCloudResult<Pagination<DiskDTO>> query(QueryDiskCondition queryDiskCondition);
}
