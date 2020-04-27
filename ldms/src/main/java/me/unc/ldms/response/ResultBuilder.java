package me.unc.ldms.response;

import me.unc.ldms.utils.StateType;

/**
 * @Description 响应结果生成工厂类
 * @Date 2020/3/6 10:26
 * @author LZS
 * @version v1.0
 */
public class ResultBuilder {

    /**
     * 生成返回结果
     * @param resultCode http状态码
     * @param message 信息
     * @param data 数据
     * @return http响应结果
     */
    private static Result buildResult(int resultCode, String message, Object data) {
        return new Result(resultCode, message, data);
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @return http响应结果
     */
    public static Result successResult(Object data) {
        return buildResult(StateType.OK.getCode(), "成功", data);
    }

    /**
     * 成功返回结果，只有信息
     * @param msg 成功信息
     * @return http响应结果
     */
    public static Result successResultOnly(String msg) {
        return buildResult(StateType.OK.getCode(), msg, null);
    }

    /**
     * 失败返回结果
     * @param message 信息
     * @return http响应结果
     */
    public static Result failResult(String message) {
        return buildResult(StateType.BAD_REQUEST.getCode(), message, null);
    }

    /**
     * 通用返回结果
     * @param state http状态码枚举类
     * @param message 信息
     * @param data 数据
     * @return http响应结果
     */
    public static Result buildResult(StateType state, String message, Object data) {
        return buildResult(state.getCode(), message, data);
    }

}
