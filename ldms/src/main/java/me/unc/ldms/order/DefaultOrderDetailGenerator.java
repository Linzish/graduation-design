package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.utils.order.*;
import me.unc.ldms.vo.OrderVO;

import java.util.Objects;

/**
 * @Description 默认订单详细生成
 * @Date 2020/2/9 15:04
 * @author LZS
 * @version v1.0
 */
public class DefaultOrderDetailGenerator extends BaseOrderGenerator {

    public DefaultOrderDetailGenerator(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    @Override
    public Order generateOrder(OrderVO orderVO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderDetail generateOrderDetail(Order order, OrderVO orderVO) {
        super.generateOrderDetail(order, orderVO);

        //物流物品类型&物品类型标记
        if (orderVO.getType() != null) {
            switch (Objects.requireNonNull(Type.getTypeByKey(orderVO.getType()))) {
                case DAILY:
                    this.orderDetail.setType(Type.DAILY.ordinal());
                    break;
                case FOOD:
                    this.orderDetail.setType(Type.FOOD.ordinal());
                    break;
                case FILE:
                    this.orderDetail.setType(Type.FILE.ordinal());
                    break;
                case DIGITAL:
                    this.orderDetail.setType(Type.DIGITAL.ordinal());
                    break;
                case CLOTHING:
                    this.orderDetail.setType(Type.CLOTHING.ordinal());
                    break;
                case OTHER:
                    this.orderDetail.setType(Type.OTHER.ordinal());
                default:
                    break;
            }
        } else {
            throw new IllegalArgumentException("物流物品类型不能为空");
        }
        if (orderVO.getTypeTag() == null) {
            this.orderDetail.setTypeTag(TypeTag.NORMAL.ordinal());
        } else {
            switch (TypeTag.getTypeTagByKey(orderVO.getTypeTag())) {
                case NORMAL:
                    this.orderDetail.setTypeTag(TypeTag.NORMAL.ordinal());
                    break;
                case FRAGILE:
                    this.orderDetail.setTypeTag(TypeTag.FRAGILE.ordinal());
                    break;
                case PRECIOUS:
                    this.orderDetail.setTypeTag(TypeTag.PRECIOUS.ordinal());
                    break;
                case DANGEROUS:
                    this.orderDetail.setTypeTag(TypeTag.DANGEROUS.ordinal());
                    break;
                case EMERGENCY:
                    this.orderDetail.setTypeTag(TypeTag.EMERGENCY.ordinal());
                    break;
            }
        }

        //包装
        this.orderDetail.setPackaging(Packaging.PACKAGED.ordinal());

        //物流类型
        switch (ExpressType.getExpressTypeByKey(orderVO.getExpressType())) {
            case NORMAL:
                this.orderDetail.setExpressType(ExpressType.NORMAL.ordinal());
                break;
            case URGENT:
                this.orderDetail.setExpressType(ExpressType.URGENT.ordinal());
                this.orderDetail.setPrice(this.orderDetail.getPrice() + 5);
                break;
            case NEXT_MORNING:
                this.orderDetail.setExpressType(ExpressType.NEXT_MORNING.ordinal());
                this.orderDetail.setPrice(this.orderDetail.getPrice() + 10);
                break;
        }


        //支付方式&是否支付
        this.orderDetail.setPayWay(PayWay.ONLINE.ordinal());
        this.orderDetail.setIsPay(IsPay.WAITING.ordinal());

        //保价
        if (orderVO.getValuation() != 0) {
            this.orderDetail.setValuation(orderVO.getValuation());
        } else {
            this.orderDetail.setValuation(0);
        }


        //地理位置
        //this.orderDetail.setCityPosition(CityPosition.getCityPositionByKey(orderVO.getCityPosition()).ordinal());

        return this.orderDetail;
    }

//    @Override
//    public OrderDetail addPrice() {
//        return null;
//    }

}
