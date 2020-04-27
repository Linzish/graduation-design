package me.unc.ldms.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 订单VO类
 * @Date 2020/2/8 14:54
 * @author LZS
 * @version v1.0
 */
@Data
public class OrderVO implements Serializable {

    private String uid;
    private String oid;
    private String startAddress;
    private String destinationAddress;
    private String startName;
    private String startPhone;
    private String destinationName;
    private String destinationPhone;
    private double price;
    private double weight;
    private String type;
    private String typeTag;
    private String packaging;
    private String expressType;
    private String payWay;
    private String cityPosition;
    private double valuation;
    private String note;
    private String status;
    private String genDate;
    private String isPay;
//    private String startLocation;
//    private String endLocation;

}
