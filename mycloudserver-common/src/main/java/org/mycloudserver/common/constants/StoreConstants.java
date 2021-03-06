/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.constants;

/**
 * 类PathConstants.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月23日 下午11:15:48
 */
public class StoreConstants {

    /**
     * 镜像卷模板路径
     */
    public static final String IMAGE_VOLUME_TEMPLATE_PATH = "template/image-volume.xml";

    /**
     * 硬盘卷模板路径
     */
    public static final String DISK_VOLUME_TEMPLATE_PATH  = "template/disk-volume.xml";

    /**
     * 硬盘模板路径
     */
    public static final String DISK_TEMPLATE_PATH         = "template/disk.xml";

    /**
     * 镜像存储池的名称
     */
    public static final String IMAGE_POOL_NAME            = "mycloud-image-pool";

    /**
     * 镜像存储池的路径
     */
    public static final String IMAGE_POOL_PATH            = "/media/mycloud-pool/images/";

    /**
     * 镜像存储池的远程路径
     */
    public static final String IMAGE_POOL_REMOTE_PATH     = "/media/mycloud-remote/images/";

    /**
     * 硬盘存储池的名称
     */
    public static final String DISK_POOL_NAME             = "mycloud-disk-pool";

    /**
     * 硬盘存储池的路径
     */
    public static final String DISK_POOL_PATH             = "/media/mycloud-remote/disks/";

    public static final String SCP_COMMAND_PASSWORD       = "10041104";
    public static final String GETIPBYSERVERSERVER        = "http://192.168.1.1:8889";
    public static final String DOMAPPINGSERVER            = "http://192.168.1.1:8888";
    public static final String DHCPTIME                   = "120000";                       //ms
    public static final String DHCP_START_IPADDRESS       = "192.168.1.100";
    public static final String DHCP_END_IPADDRESS         = "192.168.1.200";
}
