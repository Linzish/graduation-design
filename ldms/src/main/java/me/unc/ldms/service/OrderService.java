package me.unc.ldms.service;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.vo.OrderVO;

import java.util.List;

/**
 * @Description 订单业务逻辑接口
 * @Date 2020/2/8 17:19
 * @author LZS
 * @version v1.0
 */
public interface OrderService {

    Order getOrderByOid(String oid);

    OrderDetail getOrderDetailByOid(String oid);

    void generateOrder(OrderVO orderVO);

    List<Order> getOrdersByUid(String uid, boolean enable);

    List<Order> getOrdersByToUid(String uid, boolean enable);

    void audit(String oid);

    List<Order> listDisableOrders();

    List<Order> listDisableOrdersByUid(String uid);

    boolean deleteById(String oid);

    boolean disableOrder(String oid);

    boolean enableOrder(String oid);

    boolean completeOrder(String oid);

    List<Order> listOrders();

    OrderVO selectOrderVO(String oid);

    boolean updateOrderMsg(OrderVO orderVO) throws Exception;

}
