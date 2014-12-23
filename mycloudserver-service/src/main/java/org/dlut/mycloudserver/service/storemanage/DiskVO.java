/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.storemanage;

import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;

/**
 * 类DiskVO.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月23日 下午8:29:06
 */
public class DiskVO {

    /**
     * 硬盘详细信息
     */
    private DiskDTO diskDTO;

    /**
     * 硬盘挂载到虚拟机上的设备名称
     */
    private String  devName;

    public DiskDTO getDiskDTO() {
        return diskDTO;
    }

    public void setDiskDTO(DiskDTO diskDTO) {
        this.diskDTO = diskDTO;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

}
