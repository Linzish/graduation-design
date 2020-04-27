package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.vo.OrderVO;

/**
 * @Description 订单基本信息生成
 * @Date 2020/2/8 18:45
 * @author LZS
 * @version v1.0
 */
public class OrderGenerator extends BaseOrderGenerator {

    public OrderGenerator(Order order) {
        this.order = order;
    }

    @Override
    public Order generateOrder(OrderVO orderVO) {
        return super.generateOrder(orderVO);
    }

    @Override
    public OrderDetail generateOrderDetail(Order order, OrderVO orderVO) {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public OrderDetail addPrice() {
//        return null;
//    }
}
