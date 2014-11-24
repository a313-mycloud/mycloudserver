/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类FileUtil.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月23日 下午7:54:58
 */
public class FileUtil {

    private static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取镜像的格式以及总的大小
     * 
     * @param filePath
     * @return 第0个元素为镜像格式，类型为StoreFormat，第二个元素为镜像总大小，类型为long
     */
    public static Object[] getStoreFormatAndSize(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }

        String command = "qemu-img info " + filePath;
        Process process;
        Object[] result = new Object[2];
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strs = line.split(":", 2);
                String key = strs[0].trim();
                if (key.equals("file format")) {
                    StoreFormat storeFormat = StoreFormat.getStoreFormatByValue(strs[1].trim());
                    result[0] = storeFormat;
                    continue;
                }
                if (key.equals("virtual size")) {
                    String[] subStrs = strs[1].split(" |\\(");
                    if (subStrs.length >= 4) {
                        long totalSize = Long.parseLong(subStrs[3]);
                        result[1] = totalSize;
                    }
                    continue;
                }
            }
        } catch (IOException e) {
            log.error("error message", e);
            return null;
        }
        return result;

    }
}
