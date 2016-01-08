package org.dlut.mycloudserver.client.common.vmmanage;
public enum SystemTypeEnum {
	
	WINDOWS(1, "windows"),
    LINUX(2, "linux");

    private int    value;
    private String desc;

    public static SystemTypeEnum getSystemTypeByValue(int value) {
        for (SystemTypeEnum systemTypeEnum : SystemTypeEnum.values()) {
            if (value ==systemTypeEnum.getValue()) {
                return systemTypeEnum;
            }
        }

        return null;
    }

    private SystemTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
