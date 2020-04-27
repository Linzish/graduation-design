package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description 用户基本信息实体类
 * @Date 2020/2/7 14:49
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "user")
public class User implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;             //主键id
    private String uid;         //用户id
    private String username;    //用户名（登录）
    private String password;    //登录密码
    private String phone;       //手机
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date time;          //注册时间
    private int enable;     //是否可用

//    public void setTime(Date time) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        this.time = sdf.parse(sdf.format(time));
//    }
}
