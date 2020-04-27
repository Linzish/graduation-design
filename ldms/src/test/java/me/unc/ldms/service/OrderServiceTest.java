package me.unc.ldms.service;

import me.unc.ldms.utils.order.*;
import me.unc.ldms.vo.OrderVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description OrderService测试类
 * @Date 2020/2/10 14:47
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void generateOrderTest() {
        OrderVO orderVO = new OrderVO();
        orderVO.setUid("root001");
        orderVO.setStartAddress("广东省佛山市南海区桂城街道南海万科广场");
        orderVO.setDestinationAddress("广东省湛江市麻章区湖光镇海大路1号广东海洋大学");
        orderVO.setStartName("root");
        orderVO.setStartPhone("13047090958");
        orderVO.setDestinationName("lzs");
        orderVO.setDestinationPhone("13047090958");
        orderVO.setWeight(2.0);
        orderVO.setType(Type.DAILY.getValue());
        orderVO.setExpressType(ExpressType.URGENT.getValue());
        orderVO.setCityPosition(CityPosition.IN_PROVINCE.getValue());
        orderVO.setTypeTag(TypeTag.NORMAL.getValue());
        orderVO.setPackaging(Packaging.PACKAGED.getValue());
        orderVO.setPayWay(PayWay.ONLINE.getValue());
        orderVO.setNote("无");

        orderService.generateOrder(orderVO);

        System.out.println(orderService.getOrderByOid(orderVO.getOid()));
        System.out.println(orderService.getOrderDetailByOid(orderVO.getOid()));

    }

    @Test
    public void selectOrderVOTest() {
        OrderVO orderVO = orderService.selectOrderVO("202003041586109084");
        System.out.println(orderVO);
    }

    @Test
    public void completeTest() {
        orderService.completeOrder("202003041586109084");
    }

}
