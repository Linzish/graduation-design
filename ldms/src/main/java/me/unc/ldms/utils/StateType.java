package me.unc.ldms.utils;

/**
 * @Description http状态码
 * @Date 2020/2/8 16:05
 * @author LZS
 * @version v1.0
 */
public enum StateType {

    /**
     * 继续。客户端应继续其请求
     */
    CONTINUE(100, "Continue"),

    /**
     * 切换协议。服务器根据客户端的请求切换协议
     */
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),

    /**
     * 成功返回状态
     */
    OK(200,"OK"),

    /**
     * 永久移动。请求的资源已被永久的移动到新URI，返回信息会包括新的URI，浏览器会自动定向到新URI
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),

    /**
     * 临时移动。与301类似。但资源只是临时被移动。客户端应继续使用原有URI
     */
    FOUND(302, "Found"),

    /**
     * 请求格式错误
     */
    BAD_REQUEST(400,"bad request"),

    /**
     * 未授权 / 请求要求用户的身份认证
     */
    UNAUTHORIZED(401,"unauthorized"),

    /**
     * 没有权限 / 服务器理解请求客户端的请求，但是拒绝执行此请求
     */
    FORBIDDEN(403,"forbidden"),

    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404,"not found"),

    /**
     * 该http方法不被允许
     */
    NOT_ALLOWED(405,"method not allowed"),

    /**
     * 请求处理发送异常 / 服务器无法根据客户端请求的内容特性完成请求
     */
    PROCESSING_EXCEPTION(406,"Handling Exceptions"),

    /**
     * 请求处理未完成
     */
    PROCESSING_UNFINISHED(407,"To deal with unfinished"),

    /**
     * 登录过期 / 服务器等待客户端发送的请求时间过长，超时
     */
    BE_OVERDUE(408,"Be overdue"),

    /**
     * 用户未登录 / 服务器完成客户端的 PUT 请求时可能返回此代码，服务器处理请求时发生了冲突
     */
    CONFLICT(409,"Not logged in"),

    /**
     * 这个url对应的资源现在不可用
     */
    GONE(410,"gone"),

    /**
     * 请求的URI过长（URI通常为网址），服务器无法处理
     */
    REQUEST_URI_TOO_LARGE(414, "Request-URI Too Large"),

    /**
     * 请求类型错误
     */
    UNSUPPORTED_MEDIA_TYPE(415,"unsupported media type"),

    /**
     * 客户端请求的范围无效
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),

    /**
     * 校验错误时用，与400类同
     */
    UNPROCESSABLE_ENTITY(422,"unprocessable entity"),

    /**
     * 请求过多
     */
    TOO_MANY_REQUEST(429,"too many request"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /**
     * 服务器不支持请求的功能，无法完成请求
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),

    /**
     * 作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应
     */
    BAD_GATEWAY(502, "Bad Gateway"),

    /**
     * 超载或系统维护，服务器暂时的无法处理客户端的请求。
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),

    /**
     * 充当网关或代理的服务器，未及时从远端服务器获取请求
     */
    GATEWAY_TIME_OUT(504, "Gateway Time-out"),

    /**
     * 服务器不支持请求的HTTP协议的版本，无法完成处理
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported");

    private int code;
    private String value;

    StateType(int code,String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public int getCode() {
        return code;
    }

    public static Boolean isValidateStateType(String... stateType) {
        for (String s : stateType) {
            StateType[] value = StateType.values();
            boolean flag = false;
            for (StateType type : value) {
                if (type.value.equals(s)) {
                    flag = true;
                }
            }
            if (!flag) {
                return flag;
            }
        }
        return true;
    }

}
