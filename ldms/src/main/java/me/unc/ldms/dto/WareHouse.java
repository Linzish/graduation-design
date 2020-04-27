package me.unc.ldms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description 仓库地点实体类
 * @Date 2020/2/11 11:49
 * @author LZS
 * @version v1.0
 */
@Data
@TableName("warehouse")
public class WareHouse {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;                       //主键id
    private String wid;                   //仓库id
    @TableField(value = "w_position")
    private String wPosition;             //仓库位置（坐标）
    private String address;               //仓储位置
    private String name;                  //名字
    private String province;              //省份
    private String city;                  //直辖市
    private String district;              //区/街道/县
    @TableField(value = "location_type")
    private int locationType;             //地点类型

}
