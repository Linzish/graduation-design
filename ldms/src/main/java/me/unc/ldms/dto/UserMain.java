package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 用户信息实体类（待拓展）
 * @Date 2020/2/7 15:03
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "usermain")
public class UserMain implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                //主键id
    private String uid;            //用户id
    private String organization;   //所属组织
    private int vip;               //是否vip
    private String wid;            //仓储id

}
