package me.unc.ldms.vo;

import lombok.Data;

/**
 * @Description 登录信息
 * @Date 2020/3/9 18:27
 * @author LZS
 * @version v1.0
 */
@Data
public class LoginMsg {

    private String uid;
    private String targetUrl;
    private String wid;
    private String username;

    public LoginMsg(String uid, String targetUrl) {
        this.uid = uid;
        this.targetUrl = targetUrl;
    }

}
