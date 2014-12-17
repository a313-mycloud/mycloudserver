/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.common.storemanage;

import java.io.Serializable;

/**
 * 类QueryDiskCondition.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年12月16日 下午7:58:58
 */
public class QueryDiskCondition implements Serializable {

    private static final long serialVersionUID = 5950911751518139918L;

    private String            diskUuid;

    private String            diskName;

    private StoreFormat       diskFormat;

    private String            attachVmUuid;

    private String            userAccount;

    private int               offset           = 0;

    private int               limit            = 10;

    public String getDiskUuid() {
        return diskUuid;
    }

    public void setDiskUuid(String diskUuid) {
        this.diskUuid = diskUuid;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public StoreFormat getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(StoreFormat diskFormat) {
        this.diskFormat = diskFormat;
    }

    public String getAttachVmUuid() {
        return attachVmUuid;
    }

    public void setAttachVmUuid(String attachVmUuid) {
        this.attachVmUuid = attachVmUuid;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
