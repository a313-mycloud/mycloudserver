///*
// * Copyright 2014 etao.com All right reserved. This software is the
// * confidential and proprietary information of etao.com ("Confidential
// * Information"). You shall not disclose such Confidential Information and shall
// * use it only in accordance with the terms of the license agreement you entered
// * into with etao.com .
// */
//package org.dlut.mycloudserver.service.hostmanage;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.concurrent.TimeUnit;
//
//import javax.annotation.Resource;
//
//import org.dlut.mycloudserver.client.common.MyCloudResult;
//import org.dlut.mycloudserver.client.common.Pagination;
//import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
//import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
//import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
//import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
//import org.dlut.mycloudserver.dal.dataobject.HostDO;
//import org.mycloudserver.common.util.LibvirtUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class HostListener {
//
//    private static Logger      log = LoggerFactory.getLogger(HostListener.class);
//
//    @Resource(name = "hostManageService")
//    private IHostManageService hostManageService;
//
//    @Resource(name = "hostManage")
//    private HostManage         hostManage;
//
//    public void execute() {
//
////        List<HostDTO> hostList = getHostListFromDB();
////        HashMap<Integer, Integer> lossCounts = new HashMap<Integer, Integer>();
////        for (int i = 0; i < 3; i++) {
////            for (HostDTO hostDTO : hostList) {
////                if (!lossCounts.containsKey(hostDTO.getHostId()))
////                    lossCounts.put(hostDTO.getHostId(), 0);
////                if (!LibvirtUtil.canConnect(hostDTO.getHostIp(), 1, TimeUnit.SECONDS))
////                    lossCounts.put(hostDTO.getHostId(), lossCounts.get(hostDTO.getHostId()) + 1);
////            }
////        }
//
//        for (Entry<Integer, Integer> lossCount : lossCounts.entrySet()) {
//            if (this.hostExist(lossCount.getKey()) && lossCount.getValue() > 2
//                    && this.hostStatusIsRunning(lossCount.getKey())) {
//                HostDO hostDO = this.hostManage.getHostById(lossCount.getKey());
//                hostDO.setHostStatus(HostStatusEnum.CLOSED.getStatus());
//                this.hostManage.updateHost(hostDO);
//            } else if (this.hostExist(lossCount.getKey()) && lossCount.getValue() <= 2
//                    && !this.hostStatusIsRunning(lossCount.getKey())) {
//                HostDO hostDO = this.hostManage.getHostById(lossCount.getKey());
//                hostDO.setHostStatus(HostStatusEnum.RUNNING.getStatus());
//                this.hostManage.updateHost(hostDO);
//            }
//        }
//    }
//
//    private boolean hostStatusIsRunning(int hostId) {
//        HostDO hostDO = this.hostManage.getHostById(hostId);
//        if (hostDO.getHostStatus() == 0)
//            return false;
//        return true;
//    }
//
//    private boolean hostExist(int hostId) {
//        HostDO hostDO = this.hostManage.getHostById(hostId);
//        if (hostDO == null)
//            return false;
//        return true;
//    }
//
////    private List<HostDTO> getHostListFromDB() {
////        QueryHostCondition queryHostCondition = new QueryHostCondition();
////        queryHostCondition.setLimit(1000);
////        queryHostCondition.setOffset(0);
////        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
////        if (!result.isSuccess()) {
////            log.error("获取物理机列表失败，原因：" + result.getMsgInfo());
////            return new ArrayList<HostDTO>();
////        }
////        return result.getModel().getList();
////    }
//}
