package org.mycloudserver.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mycloudserver.common.constants.StoreConstants;

public class CopyImageFileUtils
{
	public static final String PATH=StoreConstants.IMAGE_POOL_PATH;
	public static final String PASS=StoreConstants.SCP_COMMAND_PASSWORD;
	public static boolean copyImageToHost(Runtime rt,String fileName,String destIP) 
		throws IOException, InterruptedException{
		String command="sshpass  -p "+ PASS+"   scp  -o StrictHostKeyChecking=no  "+PATH+ fileName	
				+"  luojie@"+destIP.trim()+":"+PATH;
		Process process = rt.exec(command);
		InputStream stderr = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		System.out.println("copying imageFile "+fileName+" to "+destIP+" ...");
		while ( (line = br.readLine()) != null){
			System.out.println("error message "+line);
		}
		int exitVal = process.waitFor();
		if(exitVal!=0){
//			System.out.println("copy imageFile "+fileName+" to "+destIP+" failed");
			return false;
		}
		return true;
	}
	
	public static boolean copyImageFromHost(Runtime rt,String fileName,String srcIP) 
			throws IOException, InterruptedException{
		String command="sshpass   -p  "+PASS+"    scp  -o StrictHostKeyChecking=no   luojie@"+srcIP.trim()+":"+PATH+fileName
				+"  "+PATH;  
		Process process = rt.exec(command);
		InputStream stderr = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		System.out.println("copying imageFile "+fileName+" from "+srcIP+" ...");
		while ( (line = br.readLine()) != null){
			System.out.println("error message "+line);
		}
		int exitVal = process.waitFor();
		if(exitVal!=0){
//			System.out.println("copy imageFile "+fileName+" to "+destIP+" failed");
			return false;
		}
		return true;
	}

}

