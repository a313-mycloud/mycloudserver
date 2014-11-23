/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.util;

import java.util.UUID;

/**
 * 类CommonUtil.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月23日 下午2:10:23
 */
public class CommonUtil {

    public static String createUuid() {
        return UUID.randomUUID().toString();
    }
}
