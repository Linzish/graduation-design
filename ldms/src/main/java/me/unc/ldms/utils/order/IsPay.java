package me.unc.ldms.utils.order;

/**
 * @Description 支付状态
 * @Date 2020/2/9 15:13
 * @author LZS
 * @version v1.0
 */
public enum IsPay {

    PAYED("已支付"),
    UNFINISHED("未支付"),
    WAITING("等待"),
    CONTRACT("合约");

    private String value;

    IsPay(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IsPay getPayByKey(String key) {
        for (IsPay pay : IsPay.values()) {
            if (key.equals(pay.value)) {
                return pay;
            }
        }
        return null;
    }

    public static IsPay getPayByOrdinal(int ord) {
        return IsPay.values()[ord];
    }

}
