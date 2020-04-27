package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description 用户权限表
 * @Date 2020/2/7 17:52
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "user_role")
public class UserRole {

    @TableId(value = "user_id")
    private int userId;
    @TableId(value = "role_id")
    private int roleId;

}
