package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description 运输信息实体类
 * @Date 2020/4/18 17:42
 * @author LZS
 * @version v1.0
 */
@Data
@TableName("transport")
public class Transport {

    private int id;                    //主键id
    private String tid;                //运输id
    @TableField(value = "car_id")
    private String carId;              //车牌
    @TableField(value = "start_address")
    private String startAddress;       //起点地址
    @TableField(value = "end_address")
    private String endAddress;         //终点位置
    @TableField(value = "departure_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date departureTime;        //出发时间
    private String uid;                //负责人id
    private String description;        //概述(1-1)
    private String wid;                //负责仓储点
    private int status;                //状态

}
