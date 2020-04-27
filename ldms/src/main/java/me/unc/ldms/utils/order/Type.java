package me.unc.ldms.utils.order;

/**
 * @Description 订单类型枚举类
 * @Date 2020/2/8 15:52
 * @author LZS
 * @version v1.0
 */
public enum Type {

    DAILY("日用品"),
    FOOD("食物"),
    FILE("文件"),
    DIGITAL("数码产品"),
    CLOTHING("衣物"),
    OTHER("其他");

    private String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Type getTypeByKey(String key) {
        for (Type type : Type.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return null;
    }

    public static Type getTypeByOrdinal(int ord) {
        return Type.values()[ord];
    }

}
