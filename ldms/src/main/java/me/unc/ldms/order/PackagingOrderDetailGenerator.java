package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.vo.OrderVO;

/**
 * @Description 加包装费
 * @Date 2020/2/9 21:22
 * @author LZS
 * @version v1.0
 */
public class PackagingOrderDetailGenerator extends ExtendOrderGenerator {

    public PackagingOrderDetailGenerator(BaseOrderGenerator orderGen) {
        super(orderGen);
        this.orderDetail = orderGen.getOrderDetail();
    }

    @Override
    public Order generateOrder(OrderVO orderVO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderDetail generateOrderDetail(Order order, OrderVO orderVO) {
        super.generateOrderDetail(order, orderVO);

        double price = this.orderDetail.getPrice();
        this.orderDetail.setPrice(price + 10);

        return this.orderDetail;
    }

}
