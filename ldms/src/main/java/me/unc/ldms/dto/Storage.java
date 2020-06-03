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
 * @Description 仓储信息实体类
 * @Date 2020/2/7 15:19
 * @author LZS
 * @version v1.0
 */
@Data
@TableName(value = "storage")
public class Storage implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                      //主键id
    private String wid;                  //仓储位置id
    private String oid;                  //订单id
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    @TableField(value = "in_date")
    private Date inDate;                 //入库时间
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    @TableField(value = "out_date")
    private Date outDate;                //出库时间
    @TableField(value = "is_turn")
    private int isTurn;                  //是否需要转运
    @TableField(value = "is_out")
    private int isOut;                   //是否出库
    private int disable;                 //删除标记
    private String tid;                  //运输id
    @TableField(value = "create_by")
    private String createBy;             //入库处理人
    @TableField(value = "out_by")
    private String outBy;             //出库处理人

}
