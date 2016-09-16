package org.dlut.mycloudserver.client.common.vmmanage;

import org.dlut.mycloudserver.client.common.BaseDTO;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MetaData extends BaseDTO {
    private static final long serialVersionUID = -3178457515916872323L;
    private String hostName;
    private String hostUserName;
    private String hostPassword;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostUserName() {
        return hostUserName;
    }

    public void setHostUserName(String hostUserName) {
        this.hostUserName = hostUserName;
    }

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "hostName='" + hostName + '\'' +
                ", hostUserName='" + hostUserName + '\'' +
                ", hostPassword='" + hostPassword + '\'' +
                '}';
    }
}
