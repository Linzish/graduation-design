package me.unc.ldms.utils.storage;

/**
 * @Description 配送图中，点对点的类型
 * @Date 2020/2/25 17:50
 * @author LZS
 * @version v1.0
 */
@SuppressWarnings("SpellCheckingInspection")
public enum PointIndexType {

    START_TO_STARTWH("起点到起点仓库"),
    STARTWH_TO_STARTPOINT("起点仓库到起点转运点"),
    STARTPOINT_TO_ENDPOINT("起点转运点到目的转运点"),
    STARTWH_TO_ENDWH("起点仓库到目的仓库"),
    ENDPOINT_TO_ENDWH("目的转运点到目的仓库"),
    ENDWH_TO_END("目的仓库到目的地");

    private String value;

    PointIndexType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PointIndexType getPointIndexTypeByKey(String key) {
        for (PointIndexType type : PointIndexType.values()) {
            if (key.equals(type.value)) {
                return type;
            }
        }
        return null;
    }

    public static PointIndexType getPointIndexTypeByOrdinal(int ord) {
        return PointIndexType.values()[ord];
    }

}
