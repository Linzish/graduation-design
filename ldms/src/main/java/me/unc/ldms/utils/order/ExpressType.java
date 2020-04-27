package me.unc.ldms.utils.order;

/**
 * @Description 物流类型
 * @Date 2020/2/8 16:28
 * @author LZS
 * @version v1.0
 */
public enum ExpressType {

    NORMAL("正常"),
    NEXT_MORNING("次晨"),
    URGENT("加急");

    private String value;

    ExpressType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExpressType getExpressTypeByKey(String key) {
        for (ExpressType type : ExpressType.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return NORMAL;
    }

    public static ExpressType getExpressTypeByOrdinal(int ord) {
        return ExpressType.values()[ord];
    }

}
