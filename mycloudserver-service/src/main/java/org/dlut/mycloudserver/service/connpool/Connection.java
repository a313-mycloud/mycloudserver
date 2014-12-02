/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.connpool;

import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;

/**
 * libvirt连接的接口
 * 
 * @author luojie 2014年11月26日 下午3:06:49
 */
public interface Connection {

    /**
     * 根据名称获取对应的存储池
     * 
     * @param storagePoolName
     * @return
     */
    public StoragePool getStoragePoolByName(String storagePoolName) throws LibvirtException;

    /**
     * 从xmlDesc中启动虚拟机
     * 
     * @param xmlDesc
     * @return
     */
    public Domain startVm(String xmlDesc) throws LibvirtException;

    /**
     * 强制关机
     * 
     * @param vmUuid
     * @return
     * @throws LibvirtException
     */
    public boolean destroyVm(String vmUuid) throws LibvirtException;

    /**
     * 关闭连接
     * 
     * @return
     */
    public int close() throws LibvirtException;

}
