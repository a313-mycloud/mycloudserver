package org.mycloudserver.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mycloudserver.common.constants.StoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyImageFileUtils
{
	  private static Logger       log                  = LoggerFactory.getLogger(CopyImageFileUtils.class);
	public static final String PATH=StoreConstants.IMAGE_POOL_PATH;
	public static final String REMOTEPATH=StoreConstants.IMAGE_POOL_REMOTE_PATH;
	public static final String PASS=StoreConstants.SCP_COMMAND_PASSWORD;
	/**
	 * 讲虚拟机镜像从文件系统拷贝到目标节点
	 * 等待操作
	 * @param rt
	 * @param fileName
	 * @param destIP
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean copyImageToHost(Runtime rt,String fileName,String destIP) 
		throws IOException, InterruptedException{
		//只有后台节点挂载了文件系统,这个时候,所有的计算机节点文件操作需要通过后台服务器
		//下面是这种模式下后台需要执行的命令
//		String command="sshpass  -p "+ PASS+"   scp  -o StrictHostKeyChecking=no  -p "+PATH+ fileName	
//				+"  root@"+destIP.trim()+":"+PATH;
//		System.out.println("执行命令"+command);
		
		//所有的节点都挂载了文件系统,这个时候,计算节点文件操作仅仅需要从计算节点本地
		//的一个文件夹拷贝到另一个文件夹,下面是这种模式下后台需要执行的命令
//		String command="sshpass   -p  "+PASS+"  ssh   luojie@"+destIP.trim()+"   'cp   -p   "
//				+REMOTEPATH+fileName+"   "+PATH+fileName+"    '";
		String command="sshpass   -p  "+PASS+"  ssh    -o   StrictHostKeyChecking=no    luojie@"+destIP.trim()+"  	sudo 	   cp   -p   "
				+REMOTEPATH+fileName+"   "+PATH+fileName+"    ";
		log.info("执行命令"+command);
		Process process = rt.exec(command);
		InputStream stderr = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
//		System.out.println("copying imageFile "+fileName+" to "+destIP+" ...");
		log.info("copying imageFile "+fileName+" to "+destIP+" ...");
		while ( (line = br.readLine()) != null){
//			System.out.println("error message "+line);
			log.info("error message "+line);
		}
		int exitVal = process.waitFor();
		if(exitVal!=0){
//			System.out.println("copy imageFile "+fileName+" to "+destIP+" failed");
			return false;
		}
		return true;
	}
	/**
	 * 将虚拟机镜像从目标节点拷贝到文件系统
	 * 等待操作
	 * @param rt
	 * @param fileName
	 * @param srcIP
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean copyImageFromHost(Runtime rt,String fileName,String srcIP) 
			throws IOException, InterruptedException{
//		String command="sshpass   -p  "+PASS+"  sudo   scp  -o StrictHostKeyChecking=no  -p   root@"+srcIP.trim()+":"+PATH+fileName
//				+"  "+PATH;  
//		System.out.println("执行命令"+command);
		String command="sshpass   -p  "+PASS+"  ssh    -o   StrictHostKeyChecking=no     luojie@"+srcIP.trim()+"   sudo    cp   -p   "
				+PATH+fileName+"   "+REMOTEPATH+fileName+"    ";
		log.info("执行命令"+command);
		Process process = rt.exec(command);
		InputStream stderr = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
//		System.out.println("copying imageFile "+fileName+" from "+srcIP+" ...");
		log.info("copying imageFile "+fileName+" from "+srcIP+" ...");
		while ( (line = br.readLine()) != null){
//			System.out.println("error message "+line);
			log.info("error message "+line);
		}
		int exitVal = process.waitFor();
		if(exitVal!=0){
//			System.out.println("copy imageFile "+fileName+" to "+destIP+" failed");
			return false;
		}
		//delete old image file
		command="sshpass   -p  "+PASS+"  ssh    -o   StrictHostKeyChecking=no     luojie@"+srcIP.trim()+"   sudo    rm  	 "   +PATH+fileName+" 	";
		log.info("执行命令"+command);
		process = rt.exec(command);
		stderr = process.getErrorStream();
		isr = new InputStreamReader(stderr);
		br = new BufferedReader(isr);
		line = null;
//		System.out.println("copying imageFile "+fileName+" from "+srcIP+" ...");
		log.info("delete  imageFile "+" in "+srcIP+":"+PATH+fileName);
		while ( (line = br.readLine()) != null){
//			System.out.println("error message "+line);
			log.info("error message "+line);
		}
		exitVal = process.waitFor();
		if(exitVal!=0){
//			System.out.println("copy imageFile "+fileName+" to "+destIP+" failed");
			return false;
		}
		return true;
	}

}

