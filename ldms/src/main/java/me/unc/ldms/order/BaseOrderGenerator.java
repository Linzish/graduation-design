package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.order.Status;
import me.unc.ldms.vo.OrderVO;

import java.util.Date;

/**
 * @Description 通用订单生成器
 * @Date 2020/2/8 17:09
 * @author LZS
 * @version v1.0
 */
public abstract class BaseOrderGenerator implements IOrder {

    protected Order order;
    protected OrderDetail orderDetail;

    public Order getOrder() {
        return this.order;
    }

    public OrderDetail getOrderDetail() {
        return this.orderDetail;
    }

    @Override
    public Order generateOrder(OrderVO orderVO) {
        //Order order = new Order();
        this.order.setUid(orderVO.getUid());
        this.order.setStartAddress(orderVO.getStartAddress());
        this.order.setDestinationAddress(orderVO.getDestinationAddress());
        this.order.setStatus(Status.AUDIT.ordinal());
        Date date = new Date();
        this.order.setGenDate(date);

        String str = GeneralUtils.parseToYYYYMMDDStr(date);
        int hash = GeneralUtils.genHash(str + orderVO.getUid());
        this.order.setOid(str + hash);

        return this.order;
    }

    @Override
    public OrderDetail generateOrderDetail(Order order, OrderVO orderVO) {
        //OrderDetail orderDetail = new OrderDetail();
        //this.orderDetail.setUid(orderVO.getUid());
        this.orderDetail.setOid(order.getOid());
        this.orderDetail.setStartName(orderVO.getStartName());
        this.orderDetail.setStartPhone(orderVO.getStartPhone());
        this.orderDetail.setDestinationName(orderVO.getDestinationName());
        this.orderDetail.setDestinationPhone(orderVO.getDestinationPhone());
        this.orderDetail.setWeight(orderVO.getWeight());
        if (orderVO.getNote() != null) {
            this.orderDetail.setNote(orderVO.getNote());
        }

        //收费价格，默认12 15 18 25
        /*switch (Objects.requireNonNull(CityPosition.getCityPositionByKey(orderVO.getCityPosition()))) {
            case SAME_CITY:
                this.orderDetail.setPrice(12);
                break;
            case IN_PROVINCE:
                this.orderDetail.setPrice(15);
                break;
            case OUTSIDE_PROVINCE:
                this.orderDetail.setPrice(18);
                break;
            case OVERSEAS:
                this.orderDetail.setPrice(25);
                break;
        }*/

        this.orderDetail.setPrice(12);

        return this.orderDetail;
    }

    //public abstract OrderDetail addPrice();

}
