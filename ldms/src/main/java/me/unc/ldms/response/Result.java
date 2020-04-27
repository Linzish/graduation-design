package me.unc.ldms.response;

import lombok.Data;

/**
 * @Description 结果返回域模型类
 * @Date 2020/3/6 10:24
 * @author LZS
 * @version v1.0
 */
@Data
public class Result {

    private int status;  //结果状态码
    private String msg;  //结果信息
    private Object obj;  //结果数据对象

    public Result() {
    }

    public Result(int status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

}
