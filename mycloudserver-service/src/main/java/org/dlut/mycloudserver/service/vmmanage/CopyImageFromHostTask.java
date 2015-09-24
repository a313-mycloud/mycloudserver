package org.dlut.mycloudserver.service.vmmanage;

import java.io.IOException;

import org.dlut.mycloudserver.dal.dataobject.VmDO;
import org.mycloudserver.common.util.CopyImageFileUtils;
import org.slf4j.Logger;

public class CopyImageFromHostTask implements Runnable{
	private Runtime runtime;
	 private VmManage            vmManage;
	 private String vmUuid;
     private String hostIp;
     private int lastHostId;
     private  Logger       log ;

	public CopyImageFromHostTask(Runtime runtime, VmManage vmManage,
			String vmUuid, String hostIp, Logger log, int lastHostId) {
		super();
		this.runtime = runtime;
		this.vmManage = vmManage;
		this.vmUuid = vmUuid;
		this.hostIp = hostIp;
		this.log = log;
		this.lastHostId = lastHostId;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		VmDO vmDO=new VmDO();
		vmDO.setVmUuid(vmUuid);
		vmDO.setIsCanRead(0);
		this.vmManage.updateVm(vmDO);
		
		try {
			CopyImageFileUtils.copyImageFromHost(this.runtime,
					this.vmManage.getVmByUuid(vmUuid).getImageUuid()
					,hostIp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.toString());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.toString());
		}
		
		vmDO.setVmUuid(vmUuid);
		vmDO.setLastHostId(lastHostId);
		vmDO.setImageVersion(this.vmManage.getVmByUuid(vmUuid).getImageVersion()+1);
		vmDO.setIsCanRead(1);
		this.vmManage.updateVm(vmDO);
	}
}
