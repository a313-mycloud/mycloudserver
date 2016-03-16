/*
 * Copyright 2016 a313 All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.vmmanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.mycloudserver.common.constants.StoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类KillVMTask.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2016年3月15日 下午1:18:02
 */
public class KillVMTask implements Runnable {
    private static Logger    log = LoggerFactory.getLogger(KillVMTask.class);
    private String           vmUuid;
    private IVmManageService vmManageService;
    private int              hostId;

    public KillVMTask(String vmUuid, IVmManageService vmManageService, int hostId) {
        super();
        this.vmUuid = vmUuid;
        this.vmManageService = vmManageService;
        this.hostId = hostId;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(Long.parseLong(StoreConstants.DHCPTIME));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            log.error("error message", e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            log.error("error message", e);
        }
        MyCloudResult<VmDTO> result = this.vmManageService.getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.error("调用vmManageService.getVmByUuid()出错，" + result.getMsgCode() + ":" + result.getMsgInfo());
        } else {
            if (result.getModel().getVmStatus().getStatus() == VmStatusEnum.CLOSED.getStatus()) {
                MyCloudResult<Boolean> result1 = this.vmManageService.killVmOnHost(vmUuid, hostId);
                if (!result1.isSuccess()) {
                    log.warn("调用vmManageService.forceShutDown()出错，" + result.getMsgCode() + ":" + result.getMsgInfo());
                }
                log.info("shutdown the vm --" + vmUuid + " running on " + hostId);
            }
        }
    }

}
