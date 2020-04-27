package me.unc.ldms.utils;

/**
 * @Description 通用状态枚举类
 * @Date 2020/2/29 15:53
 * @author LZS
 * @version v1.0
 */
public enum GeneralState {

    NONE("无"),
    ENABLE("可用"),
    DISABLE("不可用");

    private String value;

    GeneralState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GeneralState getGeneralStateByKey(String key) {
        for (GeneralState type : GeneralState.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return null;
    }

    public static GeneralState getGeneralStateByOrdinal(int ord) {
        return GeneralState.values()[ord];
    }

}
