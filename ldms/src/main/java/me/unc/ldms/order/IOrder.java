package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.vo.OrderVO;

/**
 * @Description 订单基类
 * @Date 2020/2/8 14:52
 * @author LZS
 * @version v1.0
 */
public interface IOrder {

    Order generateOrder(OrderVO orderVO);

    OrderDetail generateOrderDetail(Order order, OrderVO orderVO);

}
