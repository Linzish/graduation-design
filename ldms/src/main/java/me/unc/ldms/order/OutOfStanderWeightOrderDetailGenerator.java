package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.utils.order.CityPosition;
import me.unc.ldms.vo.OrderVO;

import java.util.Objects;

/**
 * @Description 超过标准（0.5kg）重量得订单详细
 * @Date 2020/2/9 17:25
 * @author LZS
 * @version v1.0
 */
public class OutOfStanderWeightOrderDetailGenerator extends ExtendOrderGenerator {

    public OutOfStanderWeightOrderDetailGenerator(BaseOrderGenerator baseGen) {
        super(baseGen);
        this.orderDetail = baseGen.getOrderDetail();
    }

    @Override
    public Order generateOrder(OrderVO orderVO) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderDetail generateOrderDetail(Order order, OrderVO orderVO) {
        super.generateOrderDetail(order, orderVO);

        double extraWeight = this.orderDetail.getWeight() - 0.5;
        double price = this.orderDetail.getPrice();
        double lowExtraPrice = 0;
        double highExtraPrice = 0;

        if (extraWeight < 0.5) {
            lowExtraPrice = extraWeight;
        } else {
            double v = extraWeight / 0.5;
            if (v > highExtraPrice) {
                highExtraPrice = v;
            }
        }

        switch (CityPosition.getCityPositionByOrdinal(this.orderDetail.getCityPosition())) {
            case SAME_CITY:
                this.orderDetail.setPrice(price + (lowExtraPrice + highExtraPrice) * 1.5);
                break;
            case IN_PROVINCE:
                this.orderDetail.setPrice(price + (lowExtraPrice + highExtraPrice) * 2);
                break;
            case OUTSIDE_PROVINCE:
                this.orderDetail.setPrice(price + (lowExtraPrice + highExtraPrice) * 2.5);
                break;
            case OVERSEAS:
                this.orderDetail.setPrice(price + (lowExtraPrice + highExtraPrice) * 3);
                break;
        }

        return this.orderDetail;
    }

}
