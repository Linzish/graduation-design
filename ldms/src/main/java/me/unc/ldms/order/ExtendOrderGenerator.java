package me.unc.ldms.order;

import me.unc.ldms.dto.OrderDetail;

/**
 * @Description 订单生成器装饰类
 * @Date 2020/2/8 17:13
 * @author LZS
 * @version v1.0
 */
public abstract class ExtendOrderGenerator extends BaseOrderGenerator {

    private IOrder orderGen;

    public ExtendOrderGenerator(IOrder orderGen) {
        this.orderGen = orderGen;
    }

    public IOrder getOrderGen() {
        return orderGen;
    }

    public void setOrderGen(IOrder orderGen) {
        this.orderGen = orderGen;
    }

}
