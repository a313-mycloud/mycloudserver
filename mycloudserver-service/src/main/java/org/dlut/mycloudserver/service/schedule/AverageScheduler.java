/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.hostmanage.HostDTO;
import org.dlut.mycloudserver.client.common.hostmanage.HostStatusEnum;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.vmmanage.QueryVmCondition;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.hostmanage.IHostManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类RandomScheduler.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月2日 下午8:19:15
 */
public class AverageScheduler implements IScheduler {

    private static Logger      log = LoggerFactory.getLogger(AverageScheduler.class);

    @Resource(name = "hostManageService")
    private IHostManageService hostManageService;
    
    @Resource(name = "vmManageService")
    private IVmManageService vmManageService;
    
    
    

    @Override
    public Integer getBestHostId(VmDTO vmDTO) {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setHostStatusEnum(HostStatusEnum.RUNNING);
        queryHostCondition.setLimit(1000);
        queryHostCondition.setOffset(0);
        MyCloudResult<Pagination<HostDTO>> result = hostManageService.query(queryHostCondition);
        if (!result.isSuccess()) {
            log.error("获取运行中的物理机列表失败，原因：" + result.getMsgInfo());
            return null;
        }
        List<HostDTO> hostList = result.getModel().getList();
        if (hostList.isEmpty()) {
            log.warn("没有正在运行的物理机");
            return null;
        }
        //获取正在运行的物理机的虚拟机个数,放到hostIdToVmCount中
        Map<Integer,Integer> hostIdToVmCount=new HashMap<Integer,Integer>();  
        for(HostDTO hostDTO:hostList){
        	hostIdToVmCount.put(hostDTO.getHostId(),0);
        }
        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setVmStatus(VmStatusEnum.RUNNING);
        queryVmCondition.setLimit(1000);
        queryVmCondition.setOffset(0);
        MyCloudResult<Pagination<VmDTO>> result1=this.vmManageService.query(queryVmCondition);
        if(!result1.isSuccess()){
            log.error("获取运行中的虚拟机列表失败，原因：" + result.getMsgInfo());
            return null;
        }
        List<VmDTO> vmList=result1.getModel().getList();
        if(!vmList.isEmpty()){
        	for(VmDTO vm:vmList){
        		int hostId=vm.getHostId();
        		//获取当前虚拟机所在的物理节点的虚拟机个数
        		int vmCount=hostIdToVmCount.get(hostId);
        		hostIdToVmCount.put(hostId, vmCount+1);
        	}
        }
        //将hosdIdToVmCount放到TreeMap中，并且颠倒k，v的顺序
        TreeMap<Integer,Integer> treeMap=new TreeMap<Integer,Integer>();
        
        for(int key:hostIdToVmCount.keySet()){
        	int value=hostIdToVmCount.get(key);
        	treeMap.put(value, key);
        }
        
        return treeMap.firstEntry().getValue();

    }
}
