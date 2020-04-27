package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 权限信息实体类
 * @Date 2020/2/7 15:01
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "role")
public class Role implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;             //主键id
    private String rid;         //权限描述（权限识别）

}
