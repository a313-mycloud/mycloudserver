package org.dlut.mycloudserver.service.network;

import javax.annotation.Resource;

import org.dlut.mycloudserver.service.vmmanage.VmManage;
import org.mycloudserver.common.constants.StoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 类IpMacPoolUtil.java的实现描述：TODO 类实现描述
 */
@Service("ipMacPoolUtil")
public class IpMacPoolUtil {
    private static final String MAC_ADDRESS_TEMPLATE = "52:54:%s:%s:%s:%s";
    private static Logger       log                  = LoggerFactory.getLogger(IpMacPoolUtil.class);

    @Resource(name = "vmManage")
    private VmManage            vmManage;

    /**
     * the first four numbers of mac address is 52:54 ; the last eight numbers
     * of mac address is the hex of ip ;if return "0",execute fail
     * 
     * @param START_IP_ADDRESS
     * @param END_IP_ADDRESS
     */
    public String getMacFromPool() {
        String START_IP_ADDRESS = StoreConstants.DHCP_START_IPADDRESS;
        String END_IP_ADDRESS = StoreConstants.DHCP_END_IPADDRESS;

        if (START_IP_ADDRESS.split("\\.").length != 4 && END_IP_ADDRESS.split("\\.").length != 4) {
            log.error("the ip range of dhcp is illegal");
            return "0";
        }

        String[] start_ip_address = START_IP_ADDRESS.split("\\.");
        String[] end_ip_address = END_IP_ADDRESS.split("\\.");
        long startNum = Long.parseLong(start_ip_address[0]) * 256 * 256 * 256 + Long.parseLong(start_ip_address[1])
                * 256 * 256 + Long.parseLong(start_ip_address[2]) * 256 + Long.parseLong(start_ip_address[3]);
        long endNum = Long.parseLong(end_ip_address[0]) * 256 * 256 * 256 + Long.parseLong(end_ip_address[1]) * 256
                * 256 + Long.parseLong(end_ip_address[2]) * 256 + Long.parseLong(end_ip_address[3]);

        long range = endNum - startNum;
        if (range > Integer.MAX_VALUE) {
            log.error("the ip range of dhcp is too large");
            return "0";
        }
        long randomVal = 0, newVal;
        int[] newIp;
        int index, tmp;
        String macAddr;
        while (true) {
            if (randomVal > range)
                randomVal = randomVal - range - 1;
            newVal = startNum + randomVal;
            newIp = new int[] { 0, 0, 0, 0 };
            index = 3;
            tmp = 0;
            while (newVal != 0) {
                tmp = (int) (newVal % 256);
                newIp[index--] = tmp;
                newVal = (newVal - tmp) / 256;
            }
            macAddr = String.format(MAC_ADDRESS_TEMPLATE, getFormatHexString(newIp[0]), getFormatHexString(newIp[1]),
                    getFormatHexString(newIp[2]), getFormatHexString(newIp[3]));
            if (this.vmManage.getVmByMacAddr(macAddr) == null)
                return macAddr;
            randomVal++;
        }
    }

    public String getIpByMac(String macAddr) {
        String[] macs = macAddr.split(":");
        int ipOne = Integer.parseInt(macs[2], 16);
        int ipTwo = Integer.parseInt(macs[3], 16);
        int ipThree = Integer.parseInt(macs[4], 16);
        int ipFour = Integer.parseInt(macs[5], 16);
        return ipOne + "." + ipTwo + "." + ipThree + "." + ipFour;
    }

    private String getFormatHexString(int num) {
        String result = Integer.toHexString(num);
        if (result.length() < 2) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }
}
