package me.unc.ldms.vo;

import lombok.Data;

/**
 * @Description 订单地址地理位置信息
 * @Date 2020/2/13 15:44
 * @author LZS
 * @version v1.0
 */
@Data
public class OrderAddress {

    private String startLocation;     //起始坐标
    private String endLocation;       //目标坐标
    private String startProvince;     //起始省份
    private String endProvince;       //目标省份
    private String startCity;         //起始城市
    private String endCity;           //目的地城市
    private String startDistrict;     //起始县，辖，区
    private String endDistrict;       //目的地县，辖，区

    public OrderAddress(String startLocation, String endLocation, String startProvince, String endProvince, String startCity, String endCity, String startDistrict, String endDistrict) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startProvince = startProvince;
        this.endProvince = endProvince;
        this.startCity = startCity;
        this.endCity = endCity;
        this.startDistrict = startDistrict;
        this.endDistrict = endDistrict;
    }
}
