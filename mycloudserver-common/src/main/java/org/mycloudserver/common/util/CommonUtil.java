/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.util;

import java.io.StringReader;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类CommonUtil.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月23日 下午2:10:23
 */
public class CommonUtil {

    private static Logger       log          = LoggerFactory.getLogger(CommonUtil.class);

    public static final Pattern UUID_PATTERN = Pattern
                                                     .compile("[0-9a-z]{8}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{12}");

    public static String createUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断指定的字符串是否属于[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}
     * 
     * @param strUuid
     * @return
     */
    public static boolean isUuidFormat(String strUuid) {
        Matcher matcher = UUID_PATTERN.matcher(strUuid);
        return matcher.matches();
    }

    /**
     * 根据虚拟机的xml描述文件，获取对应的显示端口号
     * 
     * @param vmDescXml
     * @return
     */
    public static Integer getShowPortFromVmDescXml(String vmDescXml) {
        if (StringUtils.isBlank(vmDescXml)) {
            return null;
        }

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new StringReader(vmDescXml));
            Element root = document.getRootElement();
            String strPort = root.element("devices").element("graphics").attributeValue("port");
            int vncPort = Integer.parseInt(strPort);
            return vncPort;
        } catch (DocumentException e) {
            log.error("解析" + vmDescXml + "失败", e);
            return null;
        } catch (NumberFormatException e) {
            log.error("vnc端口号不是数字");
            return null;
        }
    }
}
