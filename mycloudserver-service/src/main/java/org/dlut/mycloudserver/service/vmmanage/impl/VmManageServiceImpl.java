package org.dlut.mycloudserver.service.vmmanage.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.Pagination;
import org.dlut.mycloudserver.client.common.classmanage.ClassDTO;
import org.dlut.mycloudserver.client.common.hostmanage.QueryHostCondition;
import org.dlut.mycloudserver.client.common.storemanage.DiskDTO;
import org.dlut.mycloudserver.client.common.storemanage.QueryDiskCondition;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.common.usermanage.UserDTO;
import org.dlut.mycloudserver.client.common.vmmanage.*;
import org.dlut.mycloudserver.client.service.classmanage.IClassManageService;
import org.dlut.mycloudserver.client.service.storemanage.IDiskManageService;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.client.service.usermanage.IUserManageService;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.dal.dataobject.HostDO;
import org.dlut.mycloudserver.dal.dataobject.VmDO;
import org.dlut.mycloudserver.service.connpool.Connection;
import org.dlut.mycloudserver.service.connpool.IMutilHostConnPool;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.dlut.mycloudserver.service.network.IpMacPool;
import org.dlut.mycloudserver.service.network.NetworkService;
import org.dlut.mycloudserver.service.schedule.IScheduler;
import org.dlut.mycloudserver.service.storemanage.DiskVO;
import org.dlut.mycloudserver.service.vmmanage.VmManage;
import org.dlut.mycloudserver.service.vmmanage.convent.VmConvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.mycloudserver.common.constants.StoreConstants;
import org.mycloudserver.common.constants.VmConstants;
import org.mycloudserver.common.util.CommonUtil;
import org.mycloudserver.common.util.FileUtil;
import org.mycloudserver.common.util.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类VmManageServiceImpl.java的实现描述：
 * 
 * @author luojie 2014年12月1日 下午1:01:27
 */
@Service("vmManageService")
public class VmManageServiceImpl implements IVmManageService {

    private static Logger       log = LoggerFactory.getLogger(VmManageServiceImpl.class);

    @Resource(name = "vmManage")
    private VmManage            vmManage;

    @Resource(name = "hostManage")
    private HostManage          hostManage;

    @Resource(name = "userManageService")
    private IUserManageService  userManageService;

    @Resource(name = "imageManageService")
    private IImageManageService imageManageService;

    @Resource(name = "classManageService")
    private IClassManageService classManageService;

    @Resource(name = "mutilHostConnPool")
    private IMutilHostConnPool  mutilHostConnPool;

    @Resource(name = "scheduler")
    private IScheduler          scheduler;

    @Resource(name = "diskManageService")
    private IDiskManageService  diskManageService;

    @Resource(name = "ipMacPool")
    private IpMacPool           ipMacPool;

    @Override
    public MyCloudResult<VmDTO> getVmByUuid(String vmUuid) {
        VmDO vmDO = vmManage.getVmByUuid(vmUuid);
        VmDTO vmDTO = VmConvent.conventToVmDTO(vmDO);
        if (vmDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }

        return MyCloudResult.successResult(vmDTO);
    }

    /**
     * 创建新的虚拟机，必须设置vmName, vmVcpu、vmMemory、imageUuid、userAccount、showType、
     * showPassword ，classId(0表示没有课程),parentVmUuid(如果没有，则设为“”),isTemplateVm,
     * isPublicTemplate, masterDiskBusType, interfaceType,systemType 可选：desc
     * isCanRead默认为1 lastHostId默认为-1 imageVersion默认为0
     */
    @Override
    public MyCloudResult<String> createVm(VmDTO vmDTO) {
        if (vmDTO == null || vmDTO.getVmVcpu() == null || vmDTO.getVmMemory() == null
                || StringUtils.isBlank(vmDTO.getImageUuid()) || StringUtils.isBlank(vmDTO.getUserAccount())
                || vmDTO.getShowType() == null || StringUtils.isBlank(vmDTO.getShowPassword())
                || vmDTO.getClassId() == null || vmDTO.getParentVmUuid() == null || vmDTO.getIsTemplateVm() == null
                || vmDTO.getIsPublicTemplate() == null || vmDTO.getVmNetworkType() == null
                || vmDTO.getMasterDiskBusType() == null || vmDTO.getInterfaceType() == null
                || vmDTO.getSystemType() == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        //验证课程是否存在
        if (vmDTO.getClassId() != 0) {
            MyCloudResult<ClassDTO> classResult = classManageService.getClassById(vmDTO.getClassId());
            if (!classResult.isSuccess()) {
                log.warn("课程 " + vmDTO.getClassId() + "不存在");
                return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
            }
        }

        // 验证用户是否存在
        MyCloudResult<UserDTO> userResult = userManageService.getUserByAccount(vmDTO.getUserAccount());
        if (!userResult.isSuccess()) {
            log.warn("用户 " + vmDTO.getUserAccount() + " 不存在");
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_EXIST);
        }

        String vmUuid = CommonUtil.createUuid();
        vmDTO.setVmUuid(vmUuid);
        vmDTO.setHostId(0);
        vmDTO.setShowPort(0 + "");
        vmDTO.setVmStatus(VmStatusEnum.CLOSED);
        vmDTO.setImageFormat(StoreFormat.QCOW2);
        vmDTO.setImageTotalSize((long) 0);
        vmDTO.setVmMacAddress(0 + "");
        VmDO vmDO = VmConvent.conventToVmDO(vmDTO);
        if (!vmManage.createVm(vmDO)) {
            log.error("创建虚拟机 " + vmDO + "失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_CREATE_FAIL);
        }
        return MyCloudResult.successResult(vmUuid);
    }

    /**
     * 判断镜像是否和虚拟机绑定
     * 
     * @param imageUuid
     * @return
     */
    private boolean isImageBindVm(String imageUuid) {
        if (StringUtils.isBlank(imageUuid)) {
            return false;
        }
        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setImageUuid(imageUuid);
        int totalCount = vmManage.countQuery(queryVmCondition);
        return totalCount != 0;
    }

    /**
     * 获取需要挂载到vmUuid上的所有硬盘列表
     * 
     * @param vmUuid
     * @return
     */
    private List<DiskVO> getNeedAttachDiskList(String vmUuid) {
        List<DiskVO> diskVOList = new ArrayList<DiskVO>();
        if (StringUtils.isBlank(vmUuid)) {
            return diskVOList;
        }
        QueryDiskCondition queryDiskCondition = new QueryDiskCondition();
        queryDiskCondition.setAttachVmUuid(vmUuid);
        queryDiskCondition.setOffset(0);
        queryDiskCondition.setLimit(100);
        MyCloudResult<Pagination<DiskDTO>> result = diskManageService.query(queryDiskCondition);
        if (!result.isSuccess()) {
            log.error("查询硬盘列表失败，原因：" + result.getMsgInfo());
            return diskVOList;
        }
        List<DiskDTO> diskDTOList = result.getModel().getList();
        for (int i = 0; i < diskDTOList.size(); i++) {
            DiskVO diskVO = new DiskVO();
            diskVO.setDiskDTO(diskDTOList.get(i));
            char devIndex = (char) ('b' + i);
            diskVO.setDevName("sd" + devIndex);
            diskVOList.add(diskVO);
        }
        return diskVOList;
    }

    /**
     * 模板镜像不能启动虚拟机
     */
    @Override
    public MyCloudResult<Boolean> startVm(String vmUuid) {

        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> vmResult = getVmByUuid(vmUuid);
        if (!vmResult.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = vmResult.getModel();
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }

        // 如果是模板虚拟机，则不允许启动
        if (vmDTO.getIsTemplateVm()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_TEMPLATE_CAN_NOT_START);
        }

        Integer bestHostId = scheduler.getBestHostId(vmDTO);
        HostDO hostDO = this.hostManage.getHostById(bestHostId);
        if (bestHostId == null) {
            log.error("启动虚拟机" + vmDTO + "时，获取最佳物理机id失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_GET_BEST_HOST_FIAL);
        }
        log.info("虚拟机调度在Ip为" + hostDO.getHostIp() + "的主机上");

        // 获取需要挂载的硬盘
        List<DiskVO> diskVOList = getNeedAttachDiskList(vmUuid);
        //get the mac address from Mac Pool
        String macAddress = this.ipMacPool.getMacFromPool();

        //generate a new image by srcVmUuid in bestHostId
        MyCloudResult<VmDTO> srcVmResult = getVmByUuid(vmDTO.getParentVmUuid());
        if (!srcVmResult.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO srcVmDTO = srcVmResult.getModel();
        String newImageUuid = cloneImageByLibvirt(bestHostId, srcVmDTO.getImagePath(), srcVmDTO.getImageTotalSize());

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("vmUuid", vmDTO.getVmUuid());
        context.put("vmMemory", vmDTO.getVmMemory() / 1024);// 单位为KB
        context.put("vmVcpu", vmDTO.getVmVcpu());
        context.put("imagePath", StoreConstants.IMAGE_POOL_PATH + newImageUuid);
        context.put("showType", vmDTO.getShowType());
        context.put("showPassword", vmDTO.getShowPassword());
        context.put("diskList", diskVOList);
        context.put("vmNetworkType", vmDTO.getVmNetworkType());
        context.put("vmMacAddress", macAddress);
        context.put("masterDiskBusType", vmDTO.getMasterDiskBusType());
        context.put("interfaceType", vmDTO.getInterfaceType());
        context.put("systemType", vmDTO.getSystemType());
        String xmlDesc = TemplateUtil.renderTemplate(VmConstants.VOLUME_TEMPLATE_PATH, context);

        //starting the vm  --start
        Connection conn = mutilHostConnPool.getConnByHostId(bestHostId);
        try {
            if (conn == null) {
                log.error("获取连接失败");
                return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
            }
            Domain domain = conn.startVm(xmlDesc);
            if (domain == null) {
                log.error("创建虚拟机" + vmUuid + "失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_START_FAIL);
            }
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult(ErrorEnum.VM_START_FAIL);
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        log.info("started vm " + vmUuid);

        String ip = this.ipMacPool.getIpByMac(macAddress);
        log.info("the LAN IP of vm is " + ip);

        //在网关上做虚拟机地址映射--start
        String pri_ipport = null;
        if (vmDTO.getSystemType().getValue() == SystemTypeEnum.WINDOWS.getValue())
            pri_ipport = ip + ":3389";
        else
            pri_ipport = ip + ":22";
        String result = NetworkService.addOrMinusMapping("1", pri_ipport, "");
        if ("0".equals(result)) {
            MyCloudResult<Boolean> res = this.killVmOnHost(vmUuid, bestHostId);
            if (!res.isSuccess())
                log.warn("调用vmManageService.killVmOnHost()出错，" + res.getMsgCode() + ":" + res.getMsgInfo());
            log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
            return MyCloudResult.failedResult(ErrorEnum.VM_ADDRESSMAPPING_FAIL);
        }
        log.info("the WAN IP of  vm is " + result);
        //在网关上做虚拟机地址映射---end

        // 在数据库中更新虚拟机
        vmDTO.setVmStatus(VmStatusEnum.RUNNING);
        vmDTO.setHostId(bestHostId);
        vmDTO.setShowPort(result + ";" + pri_ipport);
        vmDTO.setVmMacAddress(macAddress);
        vmDTO.setImageUuid(newImageUuid);
        vmDTO.setImageTotalSize(srcVmDTO.getImageTotalSize());

        if (!updateVmIn(vmDTO)) {
            log.error("在数据库中更新vm失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        log.info("start successfully ");

        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 强制关闭虚拟机
     */
    @Override
    public MyCloudResult<Boolean> forceShutDownVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();

        if (vmDTO.getVmStatus() == VmStatusEnum.CLOSED) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }

        //first----从网关上删除虚拟机地址映射
        String ips = vmDTO.getShowPort(); //外网IP;内网IP
        if (ips.split(";").length != 2)
            log.error(ErrorEnum.VM_SHOWPORT_ILLEGAL.getErrDesc());
        else {
            String pub_port = ips.split(";")[0].split(":")[1];
            String pri_ipport = ips.split(";")[1];
            String result1 = NetworkService.addOrMinusMapping("-1", pri_ipport, pub_port);
            if ("0".equals(result1)) {
                log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
            }
            log.info("cancle gateway mapping for " + pub_port + "--" + pri_ipport);
        }

        //--shutdown the vm
        Connection conn = mutilHostConnPool.getConnByHostId(vmDTO.getHostId());
        if (conn == null) {
            log.error("获取连接失败");
            return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
        }
        try {
            if (!conn.destroyVm(vmUuid)) {
                return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
            }
            // 在数据库中更新虚拟机状态
            vmDTO.setVmStatus(VmStatusEnum.CLOSED);
            vmDTO.setLastHostId(vmDTO.getHostId());
            vmDTO.setHostId(0);//在设置为0之前,先吧他的值放到lastHostId中去
            vmDTO.setShowPort(0 + "");
            vmDTO.setVmMacAddress(0 + "");
            vmDTO.setImageUuid(0 + "");
            if (!updateVmIn(vmDTO)) {
                log.error("在数据库中更新vm失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
            }
            return MyCloudResult.successResult(Boolean.TRUE);
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }

    }

    /**
     * 更新虚拟机
     * 
     * @param vmDTO
     * @return
     */
    private boolean updateVmIn(VmDTO vmDTO) {
        VmDO vmDO = VmConvent.conventToVmDO(vmDTO);
        if (vmDO == null) {
            return false;
        }

        return vmManage.updateVm(vmDO);
    }

    @Override
    public MyCloudResult<Integer> countQuery(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int count = vmManage.countQuery(queryVmCondition);
        return MyCloudResult.successResult(count);
    }

    @Override
    public MyCloudResult<Pagination<VmDTO>> query(QueryVmCondition queryVmCondition) {
        if (queryVmCondition == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        int totalCount = vmManage.countQuery(queryVmCondition);
        List<VmDO> vmDOList = vmManage.query(queryVmCondition);
        List<VmDTO> vmDTOList = VmConvent.coventToVmDTOList(vmDOList);
        Pagination<VmDTO> pagination = new Pagination<VmDTO>(queryVmCondition.getOffset(), queryVmCondition.getLimit(),
                totalCount, vmDTOList);
        return MyCloudResult.successResult(pagination);
    }

    /**
     * 必须设置vmName, vmVcpu、vmMemory、userAccount、showType、showPassword，classId,
     * isTemplateVM
     */
    @Override
    public MyCloudResult<String> cloneVm(VmDTO destVmDTO, String srcVmUuid) {
        if (StringUtils.isBlank(srcVmUuid) || destVmDTO == null || StringUtils.isBlank(destVmDTO.getVmName())
                || destVmDTO.getVmVcpu() == null || destVmDTO.getVmMemory() == null
                || StringUtils.isBlank(destVmDTO.getUserAccount()) || destVmDTO.getShowType() == null
                || StringUtils.isBlank(destVmDTO.getShowPassword()) || destVmDTO.getClassId() == null
                || destVmDTO.getIsTemplateVm() == null || destVmDTO.getIsPublicTemplate() == null
                || destVmDTO.getVmNetworkType() == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        MyCloudResult<VmDTO> result = getVmByUuid(srcVmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + srcVmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO srcVmDTO = result.getModel();
        if (!srcVmDTO.getIsTemplateVm()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_ONLY_CLONE_FROM_TEMPLATE);
        }

        destVmDTO.setImageUuid(0 + "");
        destVmDTO.setParentVmUuid(srcVmUuid);
        // 沿用父虚拟机的主硬盘总线类型和网卡类型
        destVmDTO.setMasterDiskBusType(srcVmDTO.getMasterDiskBusType());
        destVmDTO.setInterfaceType(srcVmDTO.getInterfaceType());
        destVmDTO.setSystemType(srcVmDTO.getSystemType());

        MyCloudResult<String> createResult = this.createVm(destVmDTO);
        if (!createResult.isSuccess()) {
            log.error("创建虚拟机" + destVmDTO + "失败，原因：" + createResult.getMsgInfo());
            return MyCloudResult.failedResult(createResult.getMsgCode(), createResult.getMsgInfo());
        }
        return MyCloudResult.successResult(createResult.getModel());
    }

    /**
     * 利用libvirt克隆新的镜像，返回新镜像的uuid
     * 
     * @param srcImagePath
     * @param imageTotalSize
     * @return
     */
    private String cloneImageByLibvirt(int hostId, String srcImagePath, long imageTotalSize) {
        Connection conn = mutilHostConnPool.getConnByHostId(hostId);
        if (conn == null) {
            log.error("获取--" + hostId + "--libvirt连接失败");
            return null;
        }
        String newImageUuid = CommonUtil.createUuid();
        String newImagePath = StoreConstants.IMAGE_POOL_PATH + newImageUuid;
        try {
            StoragePool pool = conn.getStoragePoolByName(StoreConstants.IMAGE_POOL_NAME);
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("name", newImageUuid);
            context.put("uuid", newImageUuid);
            context.put("newImagePath", newImagePath);
            context.put("srcImagePath", srcImagePath);
            context.put("imageSize", imageTotalSize);
            String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.IMAGE_VOLUME_TEMPLATE_PATH, context);
            pool.storageVolCreateXML(xmlDesc, 0);
        } catch (LibvirtException e) {
            log.error("利用libvirt克隆失败", e);
            return null;
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }
        return newImageUuid;
    }

    @Override
    public MyCloudResult<Boolean> deleteVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.error("虚拟机" + vmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        if (!deleteVmByVmDTO(result.getModel())) {
            log.error("删除虚拟机失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteVmByClassId(int classId) {

        MyCloudResult<ClassDTO> result = this.classManageService.getClassById(classId);
        if (!result.isSuccess()) {
            log.error("课程" + classId + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
        }
        if (!this.vmManage.deleteVmByClassId(classId)) {
            log.error("删除虚拟机失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> deleteVmByUserAccount(String userAccount) {

        MyCloudResult<UserDTO> result = this.userManageService.getUserByAccount(userAccount);
        if (!result.isSuccess()) {
            log.error("用户" + userAccount + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_EXIST);
        }
        if (!this.vmManage.deleteVmByUserAccount(userAccount)) {
            log.error("删除虚拟机失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_DELETE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 删除被templateVmUuid克隆出来的所有虚拟机
     * 
     * @param templateVmUuid
     * @return
     */
    private boolean deleteVmCloneByTemplateVm(VmDTO templateVmDTO) {
        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setParentVmUuid(templateVmDTO.getVmUuid());
        queryVmCondition.setOffset(0);
        queryVmCondition.setLimit(500);
        MyCloudResult<Pagination<VmDTO>> result = query(queryVmCondition);
        if (!result.isSuccess()) {
            log.error("获取虚拟机列表失败，原因：" + result.getMsgInfo());
            return false;
        }
        for (VmDTO vmDTO : result.getModel().getList()) {
            if (!deleteVmByVmDTO(vmDTO)) {
                log.error("删除虚拟机" + vmDTO.getVmUuid() + "失败");
                return false;
            }
        }
        return true;
    }

    /**
     * 删除虚拟机，如果该虚拟机为模板虚拟机，则会将从该模板虚拟机克隆出来的虚拟机全部删除
     * 
     * @param vmUuid
     * @return
     */
    private boolean deleteVmByVmDTO(VmDTO deleteVmDTO) {
        if (deleteVmDTO == null) {
            return false;
        }
        if (deleteVmDTO.getIsTemplateVm()) {
            // 如果是模板虚拟机，则需要将从该模板虚拟机克隆出来的虚拟机全部删除
            if (!deleteVmCloneByTemplateVm(deleteVmDTO)) {
                return false;
            }
            // 刪除模板虚拟机与课程的关系
            MyCloudResult<Boolean> result = classManageService.deleteAllClassWithTemplateVm(deleteVmDTO.getVmUuid());
            if (!result.isSuccess()) {
                log.error("删除模板虚拟机与课程的关系失败，原因：" + result.getMsgInfo());
                return false;
            }
        }
        // 将虚拟机上的所有硬盘卸载
        MyCloudResult<Boolean> detachResult = detachAllDiskByVmDTO(deleteVmDTO);
        if (!detachResult.isSuccess()) {
            log.error("将所有硬盘从虚拟机" + deleteVmDTO + "上卸载失败，原因：" + detachResult.getMsgInfo());
        }
        // 如果虚拟机还在运行，则先强制关闭虚拟机
        if (deleteVmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            forceShutDownVmWithoutCopy(deleteVmDTO.getVmUuid());
        }
        // 在数据库中删除虚拟机
        return vmManage.deleteVmByUuid(deleteVmDTO.getVmUuid());
    }

    @Override
    public MyCloudResult<Boolean> updateVm(VmDTO vmDTO) {
        if (vmDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        if (!updateVmIn(vmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 根据domainDescXml获取未使用的硬盘设备名称
     * 
     * @param domainDescXml
     * @return
     */
    private String getNewDevNameByDomainDescXml(String domainDescXml) {
        if (StringUtils.isBlank(domainDescXml)) {
            return null;
        }

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new StringReader(domainDescXml));
            Element root = document.getRootElement();
            @SuppressWarnings("rawtypes")
            List diskElements = root.element("devices").elements("disk");
            Set<String> existDevNameSet = new HashSet<String>();
            for (int i = 0; i < diskElements.size(); i++) {
                Element diskElement = (Element) diskElements.get(i);
                existDevNameSet.add(diskElement.element("target").attributeValue("dev"));
            }
            for (int i = 0; i < 25; i++) {
                char charIndex = (char) ('b' + i);
                String devName = "sd" + charIndex;
                if (!existDevNameSet.contains(devName)) {
                    return devName;
                }
            }
        } catch (DocumentException e) {
            log.error("解析虚拟机描述文件失败", e);
        }
        return null;
    }

    /**
     * 根据domainDescXml获取硬盘对应的设备名
     * 
     * @param domainDescXml
     * @param diskDTO
     * @return
     */
    private String getDiskDevNameByDomainDescXml(String domainDescXml, DiskDTO diskDTO) {
        if (StringUtils.isBlank(domainDescXml)) {
            return null;
        }

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new StringReader(domainDescXml));
            Element root = document.getRootElement();
            @SuppressWarnings("rawtypes")
            List diskElements = root.element("devices").elements("disk");
            for (int i = 0; i < diskElements.size(); i++) {
                Element diskElement = (Element) diskElements.get(i);
                String diskPath = diskElement.element("source").attributeValue("file");
                if (diskPath.equals(diskDTO.getDiskPath())) {
                    return diskElement.element("target").attributeValue("dev");
                }
            }
        } catch (DocumentException e) {
            log.error("error message", e);
        }

        return null;
    }

    @Override
    public MyCloudResult<Boolean> attachDisk(String vmUuid, String diskUuid) {
        MyCloudResult<DiskDTO> diskResult = diskManageService.getDiskByUuid(diskUuid);
        if (!diskResult.isSuccess()) {
            log.warn("硬盘" + diskUuid + "不存在");
            return MyCloudResult.failedResult(diskResult.getMsgCode(), diskResult.getMsgInfo());
        }
        MyCloudResult<VmDTO> vmResult = getVmByUuid(vmUuid);
        if (!vmResult.isSuccess()) {
            log.warn("虚拟机" + vmUuid + "不存在");
            return MyCloudResult.failedResult(vmResult.getMsgCode(), vmResult.getMsgInfo());
        }
        DiskDTO diskDTO = diskResult.getModel();
        if (!StringUtils.isBlank(diskDTO.getAttachVmUuid())) {
            log.warn("硬盘" + diskUuid + "已挂载到虚拟机上");
            return MyCloudResult.failedResult(ErrorEnum.DISK_HAS_ATTACH_VM);
        }
        VmDTO vmDTO = vmResult.getModel();
        if (!attachDiskToVmByLibvirt(vmDTO, diskDTO)) {
            log.warn("将硬盘" + diskDTO + "挂载到" + vmDTO + "失败");
            return MyCloudResult.failedResult(ErrorEnum.DISK_ATTACH_FAIL);
        }
        diskDTO.setAttachVmUuid(vmUuid);
        MyCloudResult<Boolean> updateResult = diskManageService.updateDisk(diskDTO);
        if (!updateResult.isSuccess()) {
            log.error("更新硬盘" + diskDTO + "失败，原因：" + updateResult.getMsgInfo());
            return MyCloudResult.failedResult(updateResult.getMsgCode(), updateResult.getMsgInfo());
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 利用libvirt将硬盘挂载到虚拟机上
     * 
     * @param vmDTO
     * @param diskDTO
     * @return
     */
    private boolean attachDiskToVmByLibvirt(VmDTO vmDTO, DiskDTO diskDTO) {
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            Connection conn = mutilHostConnPool.getConnByHostId(vmDTO.getHostId());
            if (conn == null) {
                return false;
            }
            try {
                Domain domain = conn.getDomainByName(vmDTO.getVmUuid());
                String domainDescXml = domain.getXMLDesc(0);
                String devName = getNewDevNameByDomainDescXml(domainDescXml);
                if (StringUtils.isBlank(devName)) {
                    log.error("没有找到合适的设备名称来为硬盘挂载");
                    return false;
                }
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("diskFormat", diskDTO.getDiskFormat().getValue());
                context.put("diskBusType", diskDTO.getDiskBusType());
                context.put("diskPath", diskDTO.getDiskPath());
                context.put("devName", devName);
                String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.DISK_TEMPLATE_PATH, context);
                domain.attachDevice(xmlDesc);
            } catch (LibvirtException e) {
                log.error("硬盘" + diskDTO.getDiskUuid() + "挂载到虚拟机" + vmDTO.getVmUuid() + "失败", e);
                return false;
            } finally {
                try {
                    conn.close();
                } catch (LibvirtException e) {
                    log.error("error message", e);
                }
            }
        }
        return true;
    }

    /**
     * 利用libvirt将硬盘从虚拟机中卸载
     * 
     * @param vmDTO
     * @param diskDTO
     * @return
     */
    private boolean detachDiskFromVmByLibvirt(VmDTO vmDTO, DiskDTO diskDTO) {
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            Connection conn = mutilHostConnPool.getConnByHostId(vmDTO.getHostId());
            if (conn == null) {
                log.error("获取本地失败");
                return false;
            }
            try {
                Domain domain = conn.getDomainByName(vmDTO.getVmUuid());
                String domainDescXml = domain.getXMLDesc(0);
                String devName = getDiskDevNameByDomainDescXml(domainDescXml, diskDTO);
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("diskFormat", diskDTO.getDiskFormat().getValue());
                context.put("diskPath", diskDTO.getDiskPath());
                context.put("devName", devName);
                String xmlDesc = TemplateUtil.renderTemplate(StoreConstants.DISK_TEMPLATE_PATH, context);
                domain.detachDevice(xmlDesc);
            } catch (LibvirtException e) {
                log.error("硬盘" + diskDTO.getDiskUuid() + "从虚拟机" + vmDTO.getVmUuid() + "卸载失败", e);
                return false;
            } finally {
                try {
                    conn.close();
                } catch (LibvirtException e) {
                    log.error("error message", e);
                }
            }
        }
        return true;
    }

    @Override
    public MyCloudResult<Boolean> detachDisk(String diskUuid) {
        if (StringUtils.isBlank(diskUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<DiskDTO> diskResult = diskManageService.getDiskByUuid(diskUuid);
        if (!diskResult.isSuccess()) {
            log.warn("硬盘" + diskUuid + "不存在");
            return MyCloudResult.failedResult(diskResult.getMsgCode(), diskResult.getMsgInfo());
        }
        DiskDTO diskDTO = diskResult.getModel();
        String vmUuid = diskDTO.getAttachVmUuid();
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        MyCloudResult<VmDTO> vmResult = getVmByUuid(vmUuid);
        if (!vmResult.isSuccess()) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        VmDTO vmDTO = vmResult.getModel();

        if (!detachDiskByDiskDTOAndVmDTO(diskDTO, vmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.DISK_UPDATE_FAIL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    private boolean detachDiskByDiskDTOAndVmDTO(DiskDTO diskDTO, VmDTO vmDTO) {
        if (!detachDiskFromVmByLibvirt(vmDTO, diskDTO)) {
            log.warn("将硬盘" + diskDTO + "从虚拟机" + vmDTO + "中卸载失败");
            return false;
        }
        diskDTO.setAttachVmUuid("");
        MyCloudResult<Boolean> updateResult = diskManageService.updateDisk(diskDTO);
        if (!updateResult.isSuccess()) {
            log.error("在数据库更新硬盘" + diskDTO + "失败，原因：" + updateResult.getMsgInfo());
            return false;
        }
        return true;
    }

    /**
     * 将虚拟机转化为模板虚拟机
     */
    @Override
    public MyCloudResult<Boolean> changeToTemplateVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + vmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();
        if (vmDTO.getIsTemplateVm()) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        // 运行中的虚拟机不能转化为模板虚拟机
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            return MyCloudResult.failedResult(ErrorEnum.VM_RUNNING_CAN_NOT_CHANGE_TO_TEMPLATE);
        }

        /**
         * 模板虚拟机需要作为模板使用，因此需要在每个节点上都有该模板虚拟机
         */
        /*********************** start ****************************/

        /************************ end ***************************/
        vmDTO.setIsTemplateVm(Boolean.TRUE);
        if (!updateVmIn(vmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 将虚拟机转化为公有模板虚拟机
     */
    @Override
    public MyCloudResult<Boolean> changeToPublicTemplateVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + vmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();
        if (vmDTO.getIsTemplateVm() && vmDTO.getIsPublicTemplate()) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        // 运行中的虚拟机不能转化为模板虚拟机
        if (vmDTO.getVmStatus() == VmStatusEnum.RUNNING) {
            return MyCloudResult.failedResult(ErrorEnum.VM_RUNNING_CAN_NOT_CHANGE_TO_TEMPLATE);
        }
        vmDTO.setIsTemplateVm(Boolean.TRUE);
        vmDTO.setIsPublicTemplate(Boolean.TRUE);
        if (!updateVmIn(vmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 将模板虚拟机变为非模板虚拟机，此接口会将所有从该模板虚拟机克隆的虚拟机全部删除
     */
    @Override
    public MyCloudResult<Boolean> changeToNonTempalteVm(String templateVmUuid) {
        if (StringUtils.isBlank(templateVmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(templateVmUuid);
        if (!result.isSuccess()) {
            log.warn("虚拟机" + templateVmUuid + "不存在");
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO templateVmDTO = result.getModel();
        if (!templateVmDTO.getIsTemplateVm()) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }
        // 删除从该模板虚拟机克隆出来的所有虚拟机
        if (!deleteVmCloneByTemplateVm(templateVmDTO)) {
            log.error("删除模板虚拟机" + templateVmDTO + "下面的所有虚拟机失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_DELETE_FAIL);
        }
        templateVmDTO.setIsTemplateVm(Boolean.FALSE);
        if (!updateVmIn(templateVmDTO)) {
            return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    private MyCloudResult<Boolean> detachAllDiskByVmDTO(VmDTO vmDTO) {
        QueryDiskCondition queryDiskCondition = new QueryDiskCondition();
        queryDiskCondition.setAttachVmUuid(vmDTO.getVmUuid());
        queryDiskCondition.setOffset(0);
        queryDiskCondition.setLimit(100);
        MyCloudResult<Pagination<DiskDTO>> result = diskManageService.query(queryDiskCondition);
        if (!result.isSuccess()) {
            return MyCloudResult.failedResult(result.getMsgCode(), result.getMsgInfo());
        }
        for (DiskDTO diskDTO : result.getModel().getList()) {
            detachDiskByDiskDTOAndVmDTO(diskDTO, vmDTO);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    @Override
    public MyCloudResult<Boolean> detachAllDiskFromVm(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        return detachAllDiskByVmDTO(result.getModel());
    }

    @Override
    public MyCloudResult<StoreFormat> getImageFormat(String filePath) {
        if (StringUtils.isBlank(filePath))
            return MyCloudResult.failedResult(ErrorEnum.UPLOAD_IMAGE_NOT_EXIST);
        Object[] objs = FileUtil.getStoreFormatAndSize(filePath);
        if (objs == null)
            return MyCloudResult.failedResult(ErrorEnum.UPLOAD_IMAGE_FORMAT_INVALID);
        return MyCloudResult.successResult((StoreFormat) objs[0]);

    }

    /**
     * 强制关闭虚拟机 without copy
     */

    private MyCloudResult<Boolean> forceShutDownVmWithoutCopy(String vmUuid) {
        if (StringUtils.isBlank(vmUuid)) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_NULL);
        }
        MyCloudResult<VmDTO> result = getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.warn("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        VmDTO vmDTO = result.getModel();

        if (vmDTO.getVmStatus() == VmStatusEnum.CLOSED) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }

        //从网关上删除虚拟机地址映射
        String ips = vmDTO.getShowPort(); //外网IP;内网IP
        if (ips.split(";").length != 2)
            log.error(ErrorEnum.VM_SHOWPORT_ILLEGAL.getErrDesc());
        else {
            String pub_port = ips.split(";")[0].split(":")[1];
            String pri_ipport = ips.split(";")[1];
            String result1 = NetworkService.addOrMinusMapping("-1", pri_ipport, pub_port);
            if ("0".equals(result1)) {
                log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
            }
            log.info("cancle gateway mapping for " + pub_port + "--" + pri_ipport);
        }

        //shutdown the vm
        Connection conn = mutilHostConnPool.getConnByHostId(vmDTO.getHostId());
        if (conn == null) {
            log.error("获取连接失败");
            return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
        }
        try {
            if (!conn.destroyVm(vmUuid)) {
                return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
            }

            // 在数据库中更新虚拟机状态
            vmDTO.setVmStatus(VmStatusEnum.CLOSED);
            vmDTO.setLastHostId(vmDTO.getHostId());
            vmDTO.setHostId(0);//在设置为0之前,先吧他的值放到lastHostId中去
            vmDTO.setShowPort(0 + "");
            vmDTO.setVmMacAddress(0 + "");
            vmDTO.setImageUuid(0 + "");
            if (!updateVmIn(vmDTO)) {
                log.error("在数据库中更新vm失败");
                return MyCloudResult.failedResult(ErrorEnum.VM_UPDATE_FIAL);
            }
            return MyCloudResult.successResult(Boolean.TRUE);
        } catch (LibvirtException e) {
            log.error("error message", e);
            return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
        } finally {
            try {
                conn.close();
            } catch (LibvirtException e) {
                log.error("error message", e);
            }
        }

    }

    @Override
    public MyCloudResult<Boolean> isCanDelete(String ipAddress, String imageUuid) {
        QueryHostCondition queryHostCondition = new QueryHostCondition();
        queryHostCondition.setHostIp(ipAddress);
        if (this.hostManage.countQuery(queryHostCondition) <= 0) {
            //return MyCloudResult.failedResult(ErrorEnum.HOST_NOT_EXIST);
            //代表不是系统的物理机节点,不可删除
            return MyCloudResult.successResult(Boolean.FALSE);
        }
        //获取当前ipAddress对应的hostId
        int hostId = this.hostManage.query(queryHostCondition).get(0).getHostId();

        QueryVmCondition queryVmCondition = new QueryVmCondition();
        queryVmCondition.setImageUuid(imageUuid);
        int count = this.vmManage.countQuery(queryVmCondition);
        //系统中不存在该镜像,可以删除
        if (count <= 0)
            return MyCloudResult.successResult(Boolean.TRUE);

        VmDO vmDO = this.vmManage.query(queryVmCondition).get(0);
        if (!vmDO.getIsPublicTemplate() && !vmDO.getIsTemplateVm() && (vmDO.getLastHostId() != hostId)
                && (vmDO.getHostId() != hostId)) {
            return MyCloudResult.successResult(Boolean.TRUE);
        }

        return MyCloudResult.successResult(Boolean.FALSE);

    }

    @Override
    public MyCloudResult<Boolean> killVmOnHost(String vmUuid, int hostId) {
        Connection conn = mutilHostConnPool.getConnByHostId(hostId);
        if (conn == null) {
            log.error("获取连接失败");
            return MyCloudResult.failedResult(ErrorEnum.GET_CONN_FAIL);
        }
        try {
            if (!conn.destroyVm(vmUuid)) {
                return MyCloudResult.failedResult(ErrorEnum.VM_DESTROY_FAIL);
            }
        } catch (LibvirtException e) {
            // TODO Auto-generated catch block
            log.error("error message", e);
        }
        return MyCloudResult.successResult(Boolean.TRUE);
    }

    /**
     * 创建新的虚拟机，必须设置vmName, vmVcpu、vmMemory、imageUuid、userAccount、showType、
     * showPassword ，classId(0表示没有课程),parentVmUuid(如果没有，则设为“”),isTemplateVm,
     * isPublicTemplate, masterDiskBusType, interfaceType,systemType 可选：desc
     * isCanRead默认为1 lastHostId默认为-1 imageVersion默认为0
     */
    @Override
    public MyCloudResult<String> createImage(VmDTO vmDTO) {
        if (vmDTO == null || vmDTO.getVmVcpu() == null || vmDTO.getVmMemory() == null
                || StringUtils.isBlank(vmDTO.getImageUuid()) || StringUtils.isBlank(vmDTO.getUserAccount())
                || vmDTO.getShowType() == null || StringUtils.isBlank(vmDTO.getShowPassword())
                || vmDTO.getClassId() == null || vmDTO.getParentVmUuid() == null || vmDTO.getIsTemplateVm() == null
                || vmDTO.getIsPublicTemplate() == null || vmDTO.getVmNetworkType() == null
                || vmDTO.getMasterDiskBusType() == null || vmDTO.getInterfaceType() == null
                || vmDTO.getSystemType() == null || vmDTO.getImageTotalSize() == null) {
            return MyCloudResult.failedResult(ErrorEnum.PARAM_IS_INVAILD);
        }

        //验证课程是否存在
        if (vmDTO.getClassId() != 0) {
            MyCloudResult<ClassDTO> classResult = classManageService.getClassById(vmDTO.getClassId());
            if (!classResult.isSuccess()) {
                log.warn("课程 " + vmDTO.getClassId() + "不存在");
                return MyCloudResult.failedResult(ErrorEnum.CLASS_NOT_EXIST);
            }
        }

        // 验证用户是否存在
        MyCloudResult<UserDTO> userResult = userManageService.getUserByAccount(vmDTO.getUserAccount());
        if (!userResult.isSuccess()) {
            log.warn("用户 " + vmDTO.getUserAccount() + " 不存在");
            return MyCloudResult.failedResult(ErrorEnum.USER_NOT_EXIST);
        }

        String vmUuid = CommonUtil.createUuid();
        vmDTO.setVmUuid(vmUuid);
        vmDTO.setHostId(0);
        vmDTO.setShowPort(0 + "");
        vmDTO.setVmStatus(VmStatusEnum.CLOSED);
        vmDTO.setImageFormat(StoreFormat.QCOW2);
        vmDTO.setVmMacAddress(0 + "");
        VmDO vmDO = VmConvent.conventToVmDO(vmDTO);
        if (!vmManage.createVm(vmDO)) {
            log.error("创建虚拟机 " + vmDO + "失败");
            return MyCloudResult.failedResult(ErrorEnum.VM_CREATE_FAIL);
        }
        return MyCloudResult.successResult(vmUuid);
    }

    public MyCloudResult<MetaData> getMetadataByIp(String vmLanIp){
        VmDO vmDO=vmManage.getVmByLanIp(vmLanIp);
        VmDTO vmDTO = VmConvent.conventToVmDTO(vmDO);
        if (vmDTO == null) {
            return MyCloudResult.failedResult(ErrorEnum.VM_NOT_EXIST);
        }
        MetaData metaData=new MetaData();
        metaData.setHostName("ssdut"+(Integer.parseInt(vmLanIp.split(".")[3])-100));
        metaData.setHostUserName(vmDTO.getUserAccount());
        metaData.setHostPassword(vmDTO.getShowPassword());
        return MyCloudResult.successResult(metaData);
    }


}
