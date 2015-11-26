package org.dlut.mycloudserver.service.vmmanage;

import org.dlut.mycloudserver.dal.dataobject.VmDO;
import org.mycloudserver.common.util.CopyImageFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//
//import java.io.IOException;
//
//import org.dlut.mycloudserver.dal.dataobject.VmDO;
//import org.mycloudserver.common.util.CopyImageFileUtils;
//import org.slf4j.Logger;
//
//public class CopyImageFromHostTask implements Runnable{
//	private Runtime runtime;
//	 private VmManage            vmManage;
//	 private String vmUuid;
//     private String hostIp;
//     private int lastHostId;
//     private  Logger       log ;
//
//	public CopyImageFromHostTask(Runtime runtime, VmManage vmManage,
//			String vmUuid, String hostIp, Logger log, int lastHostId) {
//		super();
//		this.runtime = runtime;
//		this.vmManage = vmManage;
//		this.vmUuid = vmUuid;
//		this.hostIp = hostIp;
//		this.log = log;
//		this.lastHostId = lastHostId;
//	}
//
//
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		VmDO vmDO=new VmDO();
//		vmDO.setVmUuid(vmUuid);
//		vmDO.setIsCanRead(0);
//		this.vmManage.updateVm(vmDO);
//		
//		try {
//			CopyImageFileUtils.copyImageFromHost(this.runtime,
//					this.vmManage.getVmByUuid(vmUuid).getImageUuid()
//					,hostIp);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			log.error(e.toString());
//			
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			log.error(e.toString());
//		}
//		
//		vmDO.setVmUuid(vmUuid);
//		vmDO.setLastHostId(lastHostId);
//		vmDO.setImageVersion(this.vmManage.getVmByUuid(vmUuid).getImageVersion()+1);
//		vmDO.setIsCanRead(1);
//		this.vmManage.updateVm(vmDO);
//	}
//}
/**
 * 类CopyImageFromHostTask.java的实现描述：TODO 异步线程，将镜像上传到主机 上传完毕后，版本号加1，文件重新设置为可读
 * 
 * @author luojie 2015年9月28日 上午10:57:47
 */
public class CopyImageFromHostTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(CopyImageFromHostTask.class);
    private String        vmUuid;
    private String        hostIp;
    private VmManage      vmManage;

    /**
     * @param vmUuid
     * @param hostIp
     * @param vmManage
     */
    public CopyImageFromHostTask(String vmUuid, String hostIp, VmManage vmManage) {
        super();
        this.vmUuid = vmUuid;
        this.hostIp = hostIp;
        this.vmManage = vmManage;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        VmDO vmDO = this.vmManage.getVmByUuid(vmUuid);
        //        VmDO vmDO = new VmDO();
        //        vmDO.setVmUuid(vmUuid);
        vmDO.setIsCanRead(0);
        this.vmManage.updateVm(vmDO);
        //        String imageUuid = this.vmManage.getVmByUuid(vmUuid).getImageUuid();
        String imageUuid = vmDO.getImageUuid();
        try {
            CopyImageFileUtils.copyImageFromHost(Runtime.getRuntime(), imageUuid, hostIp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("从" + this.hostIp + "拷贝镜像" + imageUuid + "到文件系统失败");
        }
        //        int version = this.vmManage.getVmByUuid(vmUuid).getImageVersion() + 1;
        int version = vmDO.getImageVersion() + 1;
        vmDO.setImageVersion(version);
        vmDO.setIsCanRead(1);
        this.vmManage.updateVm(vmDO);
        log.info("从" + this.hostIp + "拷贝镜像" + imageUuid + "到文件系统成功--" + "文件系统版本号为" + version);
    }
}
