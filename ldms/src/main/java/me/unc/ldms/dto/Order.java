package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 订单信息实体类
 * @Date 2020/2/7 15:29
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "orderbase")
public class Order implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                           //主键id
    private String uid;                       //用户id
    @TableField(value = "to_uid")
    private String toUid;                     //签收用户id
    private String oid;                       //订单id
    private int status;                       //订单状态
    @TableField(value = "start_address")
    private String startAddress;              //寄件地址
    @TableField(value = "destination_address")
    private String destinationAddress;        //收件地址
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @TableField(value = "gen_date")
    private Date genDate;                     //创建时间
    private int enable;                       //可用

}
