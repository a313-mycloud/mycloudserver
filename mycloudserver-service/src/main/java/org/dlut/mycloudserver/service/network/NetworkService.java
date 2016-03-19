/*
 * Copyright 2016 a313 All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.network;

import java.util.HashMap;

import org.mycloudserver.common.constants.StoreConstants;

import com.alibaba.fastjson.JSONObject;

/**
 * 类NetworkService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2016年3月15日 上午9:38:50
 */
public class NetworkService {

    /**
     * 在网关上做虚拟机地址映射
     * 
     * @param action add "1" minus "-1"
     * @param pri_ipport
     * @param pub_port when add , not necessary
     * @return
     */
    public static String addOrMinusMapping(String action, String pri_ipport, String pub_port) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", action);
        params.put("pri_ipport", pri_ipport);
        params.put("pub_port", pub_port);
        String result = "";
        try {
            result = HttpRequest.post(StoreConstants.DOMAPPINGSERVER, params);
        } catch (Exception e) {
            return "0";
        }
        if ("0".equals(JSONObject.parseObject(result).getString("isSuccess"))) {
            return "0";
        }
        return JSONObject.parseObject(result).getString("port");
    }

    public static String getIPFromDHCP(String macAddress) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("macAddress", macAddress);
        long startTime = System.currentTimeMillis();
        String isSuccess = "0";
        String result = "";
        try {
            while (System.currentTimeMillis() - startTime <= Long.parseLong(StoreConstants.DHCPTIME)) {
                result = HttpRequest.post(StoreConstants.GETIPBYSERVERSERVER, params);
                isSuccess = JSONObject.parseObject(result).getString("isSuccess");
                if ("1".equals(isSuccess))
                    break;
                Thread.sleep(1000);
            }
            if ("1".equals(isSuccess))
                return JSONObject.parseObject(result).getString("ip");
            else
                return "0";
        } catch (Exception e) {
            return "0";
        }
    }
}
