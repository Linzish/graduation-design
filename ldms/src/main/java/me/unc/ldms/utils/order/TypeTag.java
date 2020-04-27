package me.unc.ldms.utils.order;

/**
 * @Description 订单种类标记
 * @Date 2020/2/8 16:16
 * @author LZS
 * @version v1.0
 */
public enum TypeTag {

    NORMAL("正常"),
    EMERGENCY("紧急物资"),
    FRAGILE("易碎物品"),
    DANGEROUS("危险物品"),
    PRECIOUS("贵重物品");

    private String value;

    TypeTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TypeTag getTypeTagByKey(String key) {
        for (TypeTag tag : TypeTag.values()) {
            if (key.equals(tag.value)) {
                return tag;
            }
        }
        return NORMAL;
    }

    public static TypeTag getTypeTagByOrdinal(int ord) {
        return TypeTag.values()[ord];
    }

}
