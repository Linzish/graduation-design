package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.vo.OrderVO;

/**
 * @Description 保价类型订单生成器
 * @Date 2020/2/9 21:44
 * @author LZS
 * @version v1.0
 */
public class ValuationOrderDetailGenerator extends ExtendOrderGenerator {

    public ValuationOrderDetailGenerator(BaseOrderGenerator orderGen) {
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

        double valuation = this.orderDetail.getValuation();
        double price = this.orderDetail.getPrice();
        double extraPrice = 0;
        if (valuation > 0 && valuation < 500) {
            extraPrice = valuation / 100;
        } else if (valuation > 500 && valuation < 1000) {
            extraPrice = (valuation / 100) * 2;
        } else if (valuation > 1000) {
            extraPrice = (valuation / 1000) * 5;
        }
        this.orderDetail.setPrice(price + extraPrice);

        return this.orderDetail;
    }

}
