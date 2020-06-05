package me.unc.ldms.controller;

import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Order;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.service.OrderService;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.StateType;
import me.unc.ldms.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 订单服务控制层
 * @Date 2020/2/9 18:10
 * @author LZS
 * @version v1.0
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 生成订单接口
     * @param orderVO orderVO实体
     * @return 统一请求回应
     */
    @PostMapping("/genOrder")
    public Result genOrder(OrderVO orderVO) {
        //orderVO.setCityPosition(GeneralUtils.checkPosition(orderVO.getStartAddress(), orderVO.getDestinationAddress()).getValue());
        if (orderVO.getNote() == null) {
            orderVO.setNote("无");
        }
        try {
            orderService.generateOrder(orderVO);
            return ResultBuilder.successResultOnly("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Order build failure");
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "提交失败", null);
        }

    }

    /**
     * 列出所有订单接口
     * @return 订单集合
     */
    @GetMapping("/listOrder")
    public List<Order> listOrder() {
        return orderService.listOrders();
    }

    /**
     * 根据用户列出所有订单接口
     * @param uid 用户id
     * @return 订单集合
     */
    @GetMapping("/listOrderByUser")
    public List<Order> listOrderByUser(String uid) {
        return orderService.getOrdersByUid(uid, true);
    }

    /**
     * 查看待签收订单
     * @param uid 用户名
     * @return 订单集合
     */
    @GetMapping("/listOrderByToUser")
    public List<Order> listOrderByToUser(String uid) {
        return orderService.getOrdersByToUid(uid, true);
    }

    /**
     * 列出所有失效订单接口
     * @return 失效订单集合
     */
    @GetMapping("/listDisableOrder")
    public List<Order> listDisableOrder() {
        return orderService.listDisableOrders();
    }

    /**
     * 列出所有失效订单接口
     * @return 失效订单集合
     */
    @GetMapping("/listDisableOrderByUid")
    public List<Order> listDisableOrderByUid(String uid) {
        return orderService.listDisableOrdersByUid(uid);
    }

    /**
     * 获取订单信息接口
     * @param oid 订单id
     * @return OrderVO
     */
    @GetMapping("/selectVO")
    public OrderVO selectVOByOid(String oid) {
        return orderService.selectOrderVO(oid);
    }

    /**
     * 根据id查询订单
     * @param oid 订单id
     * @return Order
     */
    @GetMapping("/order")
    public Order selectByOid(String oid) {
        return orderService.getOrderByOid(oid);
    }

    /**
     * 置订单失效接口（逻辑删除）
     * @param oid 订单id
     * @return 统一请求回应
     */
    @PutMapping("/disableOrder")
    public Result disableOrder(String oid) {
        if (orderService.disableOrder(oid)) {
            return ResultBuilder.successResultOnly("操作成功");
        } else {
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
        }
    }

    /**
     * 还原订单
     * @param oid 订单id
     * @return 统一请求回应
     */
    @PutMapping("/enableOrder")
    public Result enableOrder(String oid) {
        if (orderService.enableOrder(oid)) {
            return ResultBuilder.successResultOnly("操作成功");
        } else {
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
        }
    }

    /**
     * 物理删除订单接口
     * @param id 订单id
     * @return 统一请求回应
     */
    @DeleteMapping("/delete")
    public Result deleteOrder(String id) {
        if (orderService.deleteById(id)) {
            return ResultBuilder.successResultOnly("操作成功");
        } else {
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
        }
    }

    /**
     * 更新订单接口
     * @param orderVO 订单信息实体类
     * @return 统一请求回应
     */
    @PutMapping("/update")
    public Result updateOrder(OrderVO orderVO) {
        try {
            if (orderService.updateOrderMsg(orderVO)) {
                return ResultBuilder.successResultOnly("操作成功");
            } else {
                return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Update order message failure");
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
        }
    }

    @PutMapping("/audit")
    public Result audit(String oid) {
        try {
            orderService.audit(oid);
            return ResultBuilder.successResultOnly("操作成功");
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Audit order failure");
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "操作失败", null);
        }
    }

    @PutMapping("/complete")
    public Result complete(String oid) {
        try {
            if (orderService.completeOrder(oid)) {
                return ResultBuilder.successResultOnly("签收成功");
            } else {
                return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "签收失败", null);
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Audit order failure");
            return ResultBuilder.buildResult(StateType.INTERNAL_SERVER_ERROR, "签收失败", null);
        }
    }

}
