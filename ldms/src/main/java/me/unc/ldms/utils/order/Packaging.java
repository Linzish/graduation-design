package me.unc.ldms.utils.order;

/**
 * @Description 包装服务
 * @Date 2020/2/8 16:23
 * @author LZS
 * @version v1.0
 */
public enum Packaging {

    PACKAGED("已包装"),
    NO_PACKAGING("未包装");

    private String value;

    Packaging(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Packaging getPackagingByKey(String key) {
        for (Packaging packaging : Packaging.values()) {
            if (key.equals(packaging.value)) {
                return packaging;
            }
        }
        return null;
    }

    public static Packaging getPackagingByOrdinal(int ord) {
        return Packaging.values()[ord];
    }

}
