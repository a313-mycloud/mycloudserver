/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.dal.dataobject.VmDO;
import org.dlut.mycloudserver.dal.mapper.VmManageMapper;
import org.springframework.stereotype.Service;

/**
 * 类VmManage.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月30日 下午8:12:47
 */
@Service
public class VmManage {

    @Resource(name = "vmManageMapper")
    private VmManageMapper vmManageMapper;

    public VmDO getVmByUuid(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return null;
        }

        return vmManageMapper.getVmByUuid(vmUuid);
    }

    public boolean createVm(VmDO vmDO) {
        if (vmDO == null) {
            return false;
        }

        return vmManageMapper.createVm(vmDO) == 1 ? true : false;
    }

    public boolean deleteVmByUuid(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return false;
        }

        return vmManageMapper.deleteVmByUuid(vmUuid) == 1 ? true : false;
    }

    public boolean updateVm(VmDO vmDO) {
        if (vmDO == null) {
            return false;
        }

        return vmManageMapper.updateVm(vmDO) == 1 ? true : false;
    }

    public int countQuery(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return 0;
        }

        return vmManageMapper.countQuery(queryVmCondition);
    }

    public List<VmDO> query(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return new ArrayList<VmDO>();
        }

        return vmManageMapper.query(queryVmCondition);
    }
}
