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
     * 硬盘存储池的名称
     */
    public static final String DISK_POOL_NAME             = "mycloud-disk-pool";

    /**
     * 硬盘存储池的路径
     */
    public static final String DISK_POOL_PATH             = "/media/mycloud-pool/disks/";
    
    public static final String SCP_COMMAND_PASSWORD="10041104";
}
