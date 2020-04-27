package me.unc.ldms.utils;

/**
 * @Description 通用常量集
 * @Date 2020/2/8 15:50
 * @author LZS
 * @version v1.0
 */
public class AppConstant {

    /**
     * 高德api key
     */
    public static final String API_KEY = "cfb6c0e660b1f5c18644e425d1db2d0b";

    /**
     * kafka 已审核订单topic
     */
    public static final String ORDER_AUDIT_TOPIC = "audit-msg";

    /**
     * kafka 订阅审核订单topic的消费者组名
     */
    public static final String ORDER_AUDIT_TOPIC_GROUP_ID = "auditGroup";

    /**
     * redis 存入订单地址信息后缀
     */
    public static final String REDIS_ORDER_ADDRESS_DATA_SUFFIX = "-address";

    /**
     * 用户注册时生成uid的前缀
     */
    public static final String USER_REGISTER_PREFIX = "yh_";

    /**
     * 用于迪杰斯特拉算法
     */
    public static final int MAX_VALUE = 1000007;

    /**
     * redis 存入订单物流配送信息后缀
     */
    public static final String ORDER_DISTRIBUTION_DATA_SUFFIX = "-distribution";

    /**
     * redis 存入订单物流配送跟踪信息后缀
     */
    public static final String ORDER_DISTRIBUTION_TRACKING_SUFFIX = "-tracking";

    /**
     * redis 存入订单物流配送跟踪信息后缀
     */
    public static final String ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX = "-tracking_index";

    /**
     * redis 存入订单基本信息后缀
     */
    public static final String ORDER_BASE_SUFFIX = "-base";

    /**
     * redis 存入订单详细信息后缀
     */
    public static final String ORDER_DETAIL_SUFFIX = "-detail";

    /**
     * kafka  通知揽件主题名
     */
    public static final String ORDER_COLLECTION_TOPIC = "collection-msg";

    /**
     * kafka  通知揽件消费者组名
     */
    public static final String ORDER_COLLECTION_TOPIC_GROUP_ID = "collectionGroup";

}
