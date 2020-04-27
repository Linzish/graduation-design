package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 订单详细信息实体类
 * @Date 2020/2/7 15:34
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "orderdetail")
public class OrderDetail implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                     //主键id
    //private String uid;                 //用户id
    private String oid;                 //订单id
    @TableField(value = "start_name")
    private String startName;           //寄件人姓名
    @TableField(value = "start_Phone")
    private String startPhone;          //寄件人电话
    @TableField(value = "destin_name")
    private String destinationName;     //收件人姓名
    @TableField(value = "destin_phone")
    private String destinationPhone;    //收件人电话
    private double price;               //收费
    private double weight;              //重量(kg)
    private int type;                   //物流物品类型
    @TableField(value = "type_tag")
    private int typeTag;                //物品类型标记
    private int packaging;              //是否包装(默认已包装)
    @TableField(value = "express_type")
    private int expressType;            //物流类型
    @TableField(value = "pay_way")
    private int payWay;                 //支付方式
    @TableField(value = "is_pay")
    private int isPay;                  //是否支付
    @TableField(value = "city_position")
    private int cityPosition;           //是否同城，省内或省外
    private double valuation;           //保价
    private String note;                //备注

}
