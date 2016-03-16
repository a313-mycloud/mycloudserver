package org.dlut.mycloudserver.service.vmmanage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dlut.mycloudserver.client.common.ErrorEnum;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.vmmanage.VmDTO;
import org.dlut.mycloudserver.client.common.vmmanage.VmStatusEnum;
import org.dlut.mycloudserver.client.service.vmmanage.IVmManageService;
import org.dlut.mycloudserver.service.hostmanage.HostManage;
import org.mycloudserver.common.network.NetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类CopyImageFromHostTask.java的实现描述：TODO 异步线程，将镜像上传到主机 上传完毕后，版本号加1，文件重新设置为可读
 * 
 * @author luojie 2015年9月28日 上午10:57:47
 */
public class SetVMCloseTask implements Runnable {
    private static Logger    log             = LoggerFactory.getLogger(SetVMCloseTask.class);
    private String           vmUuid;
    private IVmManageService vmManageService;
    private VmManage         vmManage;
    private HostManage       hostManage;
    private ExecutorService  executorService = Executors.newSingleThreadExecutor();

    /**
     * @param vmUuid
     * @param hostIp
     * @param vmManage
     */
    public SetVMCloseTask(String vmUuid, IVmManageService vmManageService, VmManage vmManage, HostManage hostManage) {
        super();
        this.vmUuid = vmUuid;
        this.vmManageService = vmManageService;
        this.vmManage = vmManage;
        this.hostManage = hostManage;
    }

    @Override
    public void run() {
        MyCloudResult<VmDTO> result = vmManageService.getVmByUuid(vmUuid);
        if (!result.isSuccess()) {
            log.error("获取虚拟机" + vmUuid + "失败，原因：" + result.getMsgInfo());
        } else {
            VmDTO vmDTO = result.getModel();
            String lastHostIp = this.hostManage.getHostById(vmDTO.getHostId()).getHostIp();

            //从网关上删除虚拟机地址映射
            String ips = vmDTO.getShowPort(); //外网IP;内网IP
            if (ips.split(";").length != 2)
                log.error(ErrorEnum.VM_SHOWPORT_ILLEGAL.getErrDesc());
            else {
                String pub_port = ips.split(";")[0].split(":")[1];
                String pri_ipport = ips.split(";")[1];
                String result1 = NetworkService.addOrMinusMapping("-1", pri_ipport, pub_port);
                if ("0".equals(result1))
                    log.error(ErrorEnum.VM_ADDRESSMAPPING_FAIL.getErrDesc());
                log.info("cancle gateway mapping for " + pub_port + "--" + pri_ipport);
            }

            vmDTO.setVmStatus(VmStatusEnum.CLOSED);
            vmDTO.setLastHostId(vmDTO.getHostId());
            vmDTO.setHostId(0);
            vmDTO.setShowPort(0 + "");
            MyCloudResult<Boolean> updateResult = vmManageService.updateVm(vmDTO);
            if (!updateResult.isSuccess()) {
                log.error("更新虚拟机" + vmDTO + "失败，原因：" + updateResult.getMsgInfo());
            }
            /**
             * 开启异步线程，将镜像上传到主机 上传完毕后，版本号加1，文件重新设置为可读
             */
            /************* start **************************************/
            executorService.submit(new CopyImageFromHostTask(vmUuid, lastHostIp, this.vmManage));
            /************** end *************************************/

            log.info("检测到虚拟机" + vmDTO.getVmUuid() + "--" + vmDTO.getVmName() + "关机");
        }
    }
}
