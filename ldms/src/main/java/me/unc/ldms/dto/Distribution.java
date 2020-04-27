package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description 物流配送信息实体类
 * @Date 2020/2/29 15:24
 * @author LZS
 * @version v1.0
 */
@Data
@TableName("distribution")
public class Distribution {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                          //主键id
    private String oid;                      //订单id
    @TableField(value = "start_location")
    private String startLocation;            //起始地址(坐标)
    @TableField(value = "start_address")
    private String startAddress;             //起始地址
    @TableField(value = "start_warehouse")
    private String startWarehouse;           //起始仓库点
    @TableField(value = "start_point")
    private String startPoint;               //起始转运点
    @TableField(value = "end_point")
    private String endPoint;                 //终点转运点
    @TableField(value = "end_warehouse")
    private String endWarehouse;             //终点仓库
    @TableField(value = "end_location")
    private String endLocation;              //目的地（坐标）
    @TableField(value = "end_address")
    private String endAddress;               //目的地
    private int status;                      //状态（备用）
//    private String tid;                      //运输id
    private int tracking;                    //物流跟踪索引
    @TableField(value = "tracking_msg")
    private String trackingMsg;              //物流跟踪信息

}
