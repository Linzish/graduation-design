package me.unc.ldms.utils.order;

/**
 * @Description 订单状态
 * @Date 2020/2/10 14:39
 * @author LZS
 * @version v1.0
 */
public enum Status {

    AUDIT("待审核"),
    GOING("进行中"),
    COMPLETE("已完成");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Status getOrderStatusByOrdinal(int ord) {
        return Status.values()[ord];
    }

    public static Status getOrderStatusByKey(String key) {
        for (Status status : Status.values()) {
            if (key.equals(status.value)) {
                return status;
            }
        }
        return null;
    }

}
