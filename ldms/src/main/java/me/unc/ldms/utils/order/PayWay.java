package me.unc.ldms.utils.order;

/**
 * @Description 支付方式
 * @Date 2020/2/8 16:48
 * @author LZS
 * @version v1.0
 */
public enum PayWay {

    ONLINE("在线支付"),
    DELIVERY("货到付款");

    private String value;

    PayWay(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PayWay getPayWayByKey(String key) {
        for (PayWay way : PayWay.values()) {
            if (key.equals(way.value)) {
                return way;
            }
        }
        return null;
    }

    public static PayWay getPayWayByOrdinal(int ord) {
        return PayWay.values()[ord];
    }

}
