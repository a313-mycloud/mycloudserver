/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.dal.dataobject.VmDO;

/**
 * 类VMManageMapper.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月30日 下午6:54:20
 */
public interface VmManageMapper {

    /**
     * 根据uuid获取虚拟机信息
     * 
     * @param vmUuid
     * @return
     */
    VmDO getVmByUuid(String vmUuid);

    //    VmDO getVmByMacAddr(String vmMacAddress);

    /**
     * 创建虚拟机
     * 
     * @param vmDO
     * @return
     */
    int createVm(VmDO vmDO);

    /**
     * 根据uuid删除虚拟机
     * 
     * @param vmUuid
     * @return
     */
    int deleteVmByUuid(String vmUuid);

    /**
     * 根据classId删除虚拟机
     * 
     * @param classId
     * @return
     */
    int deleteVmByClassId(@Param("classId") int classId);

    /**
     * 根据userAccount删除虚拟机
     * 
     * @param classId
     * @return
     */
    int deleteVmByUserAccount(String userAccount);

    /**
     * 更新虚拟机
     * 
     * @param vmDO
     * @return
     */
    int updateVm(VmDO vmDO);

    /**
     * 统计虚拟机数量
     * 
     * @param queryVmCondition
     * @return
     */
    int countQuery(QueryVmCondition queryVmCondition);

    /**
     * 查询虚拟机列表
     * 
     * @param queryVmCondition
     * @return
     */
    List<VmDO> query(QueryVmCondition queryVmCondition);

    /**
     * 根据lanIp获取虚拟机信息
     *
     * @param vmLanIp
     * @return
     */
    VmDO getVmByLanIp(String vmLanIp);

}
