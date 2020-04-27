package me.unc.ldms.utils.order;

/**
 * @Description 物流地点地理位置
 * @Date 2020/2/9 17:46
 * @author LZS
 * @version v1.0
 */
public enum CityPosition {

    SAME_CITY("同城"),
    IN_PROVINCE("省内"),
    OUTSIDE_PROVINCE("省外"),
    OVERSEAS("海外");

    private String value;

    CityPosition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CityPosition getCityPositionByKey(String key) {
        for (CityPosition type : CityPosition.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return null;
    }

    public static CityPosition getCityPositionByOrdinal(int ord) {
        return CityPosition.values()[ord];
    }

}
