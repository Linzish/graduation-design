package me.unc.ldms.utils.storage;

/**
 * @Description 仓储地点类型
 * @Date 2020/2/23 18:11
 * @author LZS
 * @version v1.0
 */
public enum LocationType {

    WAREHOUSE("仓储点"),
    TRANSSHIPMENT_POINT("转运点");

    private String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LocationType getLocationTypeByKey(String key) {
        for (LocationType type : LocationType.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return null;
    }

    public static LocationType getCityPositionByOrdinal(int ord) {
        return LocationType.values()[ord];
    }

}
