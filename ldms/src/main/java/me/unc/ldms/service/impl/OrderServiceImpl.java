package me.unc.ldms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.mapper.OrderDetailMapper;
import me.unc.ldms.mapper.OrderMapper;
import me.unc.ldms.order.*;
import me.unc.ldms.service.OrderService;
import me.unc.ldms.utils.AppConstant;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.order.CityPosition;
import me.unc.ldms.utils.order.Status;
import me.unc.ldms.utils.order.Packaging;
import me.unc.ldms.utils.order.IsPay;
import me.unc.ldms.vo.OrderAddress;
import me.unc.ldms.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Description 订单服务业务逻辑实现类
 * @Date 2020/2/9 16:27
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ExecutorService asyncExecution;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 根据订单id获取订单基本信息
     * @param oid 订单id
     * @return Order-订单基本信息
     */
    @Override
    public Order getOrderByOid(String oid) {
        log.info("calling OrderService [getOrderByOid]");
        //TODO -待完善
        //redis

        //mysql
        return orderMapper.selectOne(new QueryWrapper<Order>().eq("oid", oid).eq("enable", 1));
    }

    /**
     * 根据订单id获取订单详细信息
     * @param oid 订单id
     * @return OrderDetail-订单详细信息
     */
    @Override
    public OrderDetail getOrderDetailByOid(String oid) {
        log.info("calling OrderService [getOrderDetailByOid]");
        //TODO -待完善
        //redis
        //mysql
        return orderDetailMapper.selectOne(new QueryWrapper<OrderDetail>().eq("oid", oid));
    }

    /**
     * 订单生成
     * @param orderVO 前台传值，订单信息
     */
    @Transactional
    @Override
    public void generateOrder(OrderVO orderVO) {
        log.info("calling OrderService [generateOrder]");
        OrderDetail orderDetail = new OrderDetail();
        //生成订单
        BaseOrderGenerator orderGenerator = new OrderGenerator(new Order());
        Order order = orderGenerator.generateOrder(orderVO);
        orderVO.setOid(order.getOid());    //获取oid
        orderVO.setGenDate(GeneralUtils.parseDateToStr(order.getGenDate(), "yyyy-MM-dd HH:mm"));

        //异步判断位置信息
        Future<?> submit = asyncExecution.submit(() -> checkPositionAndPersistence(orderVO));

        //生成订单详细
        BaseOrderGenerator orderDetailGen = new DefaultOrderDetailGenerator(orderDetail);
        orderDetail = orderDetailGen.generateOrderDetail(order, orderVO);
        //订单分类
        if (orderVO.getWeight() > 0.5) {  //超重
            orderDetailGen = new OutOfStanderWeightOrderDetailGenerator(orderDetailGen);
            orderDetail = orderDetailGen.generateOrderDetail(order, orderVO);
        } else if (orderVO.getPackaging().equals(Packaging.NO_PACKAGING.getValue())) {  //包装
            orderDetailGen = new PackagingOrderDetailGenerator(orderDetailGen);
            orderDetail = orderDetailGen.generateOrderDetail(order, orderVO);
        } else if (orderVO.getValuation() > 0) {  //保价
            orderDetailGen = new ValuationOrderDetailGenerator(orderDetailGen);
            orderDetail = orderDetailGen.generateOrderDetail(order, orderVO);
        }

        //阻塞，等待执行
        try {
            submit.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //加钱
        switch (Objects.requireNonNull(CityPosition.getCityPositionByKey(orderVO.getCityPosition()))) {
            case SAME_CITY:
                orderDetail.setCityPosition(CityPosition.SAME_CITY.ordinal());
                break;
            case IN_PROVINCE:
                orderDetail.setCityPosition(CityPosition.IN_PROVINCE.ordinal());
                orderDetail.setPrice(orderDetail.getPrice() + 3);
                break;
            case OUTSIDE_PROVINCE:
                orderDetail.setCityPosition(CityPosition.OUTSIDE_PROVINCE.ordinal());
                orderDetail.setPrice(orderDetail.getPrice() + 6);
                break;
            case OVERSEAS:
                orderDetail.setCityPosition(CityPosition.OVERSEAS.ordinal());
                orderDetail.setPrice(orderDetail.getPrice() + 13);
                break;
        }

        orderVO.setPrice(orderDetail.getPrice());
        orderVO.setStatus(Status.AUDIT.getValue());
        orderVO.setIsPay(IsPay.PAYED.getValue());
        //存redis
        Map<String, Object> beanMap = BeanUtils.beanToMap(orderVO);
        redisTemplate.opsForHash().putAll(orderVO.getOid(), beanMap);
        //redisTemplate.expire(orderVO.getOid(), 240, TimeUnit.SECONDS); //测试用

        //存mysql
        orderMapper.insert(order);
        orderDetailMapper.insert(orderDetail);

        //异步执行订单计算逻辑
        asyncExecution.execute(() -> access(orderVO.getOid()));
    }

    /**
     * 根据用户id查找所有订单
     * @param uid 用户id
     * @param enable 是否可用
     * @return 用户所有订单
     */
    @Override
    public List<Order> getOrdersByUid(String uid, boolean enable) {
        log.info("calling OrderService [getOrdersByUid]");
        if (enable) {
            return orderMapper.selectList(new QueryWrapper<Order>().eq("uid", uid).eq("enable", 1));
        } else {
            return orderMapper.selectList(new QueryWrapper<Order>().eq("uid", uid).eq("enable", 0));
        }
    }

    /**
     * 审核订单
     * @param oid 订单id
     */
    @Override
    public void audit(String oid) {
        log.info("calling OrderService [audit]");
        Order order = getOrderByOid(oid);
        order.setStatus(Status.GOING.ordinal());
        orderMapper.updateById(order);
    }

    /**
     * 查询全部失效订单
     * @return 失效订单集合
     */
    @Override
    public List<Order> listDisableOrders() {
        log.info("calling OrderService [listDisableOrders]");
        return orderMapper.selectList(new QueryWrapper<Order>().eq("enable", 0));
    }

    /**
     * 删除订单（物理）
     * @param id 订单id
     */
    @Override
    public boolean deleteById(String id) {
        log.info("calling OrderService [deleteById]");
        return orderMapper.delete(new QueryWrapper<Order>().eq("id", Integer.parseInt(id))) == 1;
    }

    /**
     * 删除订单（逻辑）
     * @param oid 订单id
     * @return 是否删除
     */
    @Override
    public boolean disableOrder(String oid) {
        log.info("calling OrderService [disableOrder]");
        redisTemplate.expire(oid + AppConstant.ORDER_DISTRIBUTION_DATA_SUFFIX, 10 * 60, TimeUnit.SECONDS);
        redisTemplate.expire(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX, 10 * 60, TimeUnit.SECONDS);
        redisTemplate.expire(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX, 10 * 60, TimeUnit.SECONDS);
        redisTemplate.expire(oid, 10 * 60, TimeUnit.SECONDS);
        redisTemplate.expire(oid + AppConstant.REDIS_ORDER_ADDRESS_DATA_SUFFIX, 10 * 60, TimeUnit.SECONDS);
        Order order = new Order();
        order.setEnable(0);
        return orderMapper.update(order, new QueryWrapper<Order>().eq("oid", oid)) == 1;
    }

    /**
     * 还原逻辑删除订单
     * @param oid 订单id
     * @return 是否还原
     */
    @Override
    public boolean enableOrder(String oid) {
        Order order = new Order();
        order.setEnable(1);
        return orderMapper.update(order, new QueryWrapper<Order>().eq("oid", oid)) == 1;
    }

    /**
     * 订单完成
     * @param oid 订单id
     */
    @Override
    public boolean completeOrder(String oid) {
        log.info("calling OrderService [completeOrder]");
        Order order = getOrderByOid(oid);
        order.setStatus(Status.COMPLETE.ordinal());
        int i = orderMapper.updateById(order);
        //记录物流信息
        redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX,
                GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), "已签收");
        //TODO -设置一些redis数据失效

        return i == 1;
    }

    /**
     * 查询所有订单
     * @return 订单列表
     */
    @Override
    public List<Order> listOrders() {
        log.info("calling OrderService [listOrders]");
        return orderMapper.selectList(new QueryWrapper<Order>().eq("enable", 1));
    }

    /**
     * 查找OrderVO
     * @param oid 订单id
     * @return OrderVO
     */
    @Override
    public OrderVO selectOrderVO(String oid) {
        log.info("calling OrderService [selectOrderVO]");
        OrderVO orderVO = null;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(oid);
        if (!map.isEmpty()) {
            Map<String, Object> dataMap = JSONObject.parseObject(JSON.toJSONString(map), new TypeReference<Map<String, Object>>() {});
            orderVO = BeanUtils.mapToBean(dataMap, OrderVO.class);
        } else {
            orderVO = new OrderVO();
            Order order = getOrderByOid(oid);
            OrderDetail orderDetail = getOrderDetailByOid(oid);
            OrderUtil.orderEntityToOrderVO(order, orderVO);
            OrderUtil.orderDetailEntityToOrderVO(orderDetail, orderVO);
            //限时存入
            Map<String, Object> beanMap = BeanUtils.beanToMap(orderVO);
            redisTemplate.opsForHash().putAll(oid, beanMap);
            redisTemplate.expire(oid, 2 * 3600, TimeUnit.SECONDS);
        }

        return orderVO;
    }

    /**
     * 更新订单信息
     * @param orderVO 订单vo
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean updateOrderMsg(OrderVO orderVO) throws Exception {
        log.info("calling OrderService [updateOrderMsg]");
        //计算价格
        orderVO.setPrice(OrderUtil.posCalculator(orderVO));
        //更新redis和mysql数据
        Map<String, Object> beanMap = BeanUtils.beanToMap(orderVO);
        redisTemplate.opsForHash().putAll(orderVO.getOid(), beanMap);
        return updateOrder(orderVO) && updateOrderDetail(orderVO);
    }

    /**
     * 开始计算订单信息
     * @param oid 订单id
     */
    protected void access(String oid) {
        //kafka发出通知，订单已审核，待揽件
        log.info("OrderService [audit] --- kafka sendMessage start");
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(AppConstant.ORDER_AUDIT_TOPIC, oid);
        //设置回调函数
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {  //失败
                log.error("kafka sendMessage failure, ex = {}, topic = {}, data = {}", throwable, AppConstant.ORDER_AUDIT_TOPIC, oid);
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {  //成功
                log.info("kafka sendMessage success, topic = {}, data = {}", AppConstant.ORDER_AUDIT_TOPIC, oid);
            }
        });
    }

    /**
     * 更新订单信息
     * @param orderVO 订单vo
     * @return 是否成功
     */
    protected boolean updateOrder(OrderVO orderVO) throws Exception {
        log.info("calling OrderService [updateOrder]");
        //转换再更新，更新详细一样
        Map<Object, Object> map = redisTemplate.opsForHash().entries(orderVO.getOid());
        if (!map.isEmpty()) {
            Map<String, Object> dataMap = JSONObject.parseObject(JSON.toJSONString(map), new TypeReference<Map<String, Object>>() {});
            OrderVO preOrderVO = BeanUtils.mapToBean(dataMap, OrderVO.class);
            Order preOrder = getOrderByOid(orderVO.getOid());
            Order order = OrderUtil.orderVOToOrderEntity(orderVO, preOrder, GeneralUtils.compareObj(preOrderVO, orderVO));
            Map<String, Object> beanMap = BeanUtils.beanToMap(orderVO);
            redisTemplate.opsForHash().putAll(orderVO.getOid(), beanMap);
            return orderMapper.updateById(order) == 1;
        } else {
            OrderVO preOrderVO = new OrderVO();
            Order preOrder = getOrderByOid(orderVO.getOid());
            OrderUtil.orderEntityToOrderVO(preOrder, preOrderVO);
            Order order = OrderUtil.orderVOToOrderEntity(orderVO, preOrder, GeneralUtils.compareObj(preOrderVO, orderVO));
            return orderMapper.updateById(order) == 1;
        }
    }

    /**
     * 更新订单详细信息
     * @param orderVO 订货vo
     * @return 是否成功
     */
    protected boolean updateOrderDetail(OrderVO orderVO) throws Exception {
        log.info("calling OrderService [updateOrderDetail]");
        Map<Object, Object> map = redisTemplate.opsForHash().entries(orderVO.getOid());
        if (!map.isEmpty()) {
            Map<String, Object> dataMap = JSONObject.parseObject(JSON.toJSONString(map), new TypeReference<Map<String, Object>>() {});
            OrderVO preOrderVO = BeanUtils.mapToBean(dataMap, OrderVO.class);
            OrderDetail preOrderDetail = getOrderDetailByOid(orderVO.getOid());
            OrderDetail orderDetail = OrderUtil.orderVOToOrderDetailEntity(orderVO, preOrderDetail, GeneralUtils.compareObj(preOrderVO, orderVO));
            Map<String, Object> beanMap = BeanUtils.beanToMap(orderVO);
            redisTemplate.opsForHash().putAll(orderVO.getOid(), beanMap);
            return orderDetailMapper.updateById(orderDetail) == 1;
        } else {
            OrderVO preOrderVO = new OrderVO();
            OrderDetail preOrderDetail = getOrderDetailByOid(orderVO.getOid());
            OrderUtil.orderDetailEntityToOrderVO(preOrderDetail, preOrderVO);
            OrderDetail orderDetail = OrderUtil.orderVOToOrderDetailEntity(orderVO, preOrderDetail, GeneralUtils.compareObj(preOrderVO, orderVO));
            return orderDetailMapper.updateById(orderDetail) == 1;
        }
    }

    /**
     * 分辨同城，省内或省外
     * @param orderVO 订单信息
     */
    private void checkPositionAndPersistence(OrderVO orderVO) {
        String startAddress = orderVO.getStartAddress();
        String destinationAddress = orderVO.getDestinationAddress();

        String startData = restTemplate.getForObject("https://restapi.amap.com/v3/geocode/geo?address={1}&key={2}",
                String.class, startAddress, AppConstant.API_KEY);
        Map<String, Object> dataMap1 = (Map<String, Object>) JSON.parse(startData);
        if (dataMap1.get("count").equals("0")) {
            throw new IllegalArgumentException("暂不支持海外业务");
        }
        JSONObject jsonObject1 = null;
        for (int i = 0; i < ((JSONArray) dataMap1.get("geocodes")).size(); i++) {
            jsonObject1 = ((JSONArray) dataMap1.get("geocodes")).getJSONObject(i);
        }
        Map<String, Object> startMap = jsonObject1.getInnerMap();

        String endData = restTemplate.getForObject("https://restapi.amap.com/v3/geocode/geo?address={1}&key={2}",
                String.class, destinationAddress, AppConstant.API_KEY);
        Map<String, Object> dataMap2 = (Map<String, Object>) JSON.parse(endData);
        if (dataMap2.get("count").equals("0")) {
            throw new IllegalArgumentException("暂不支持海外业务");
        }
        JSONObject jsonObject2 = null;
        for (int i = 0; i < ((JSONArray) dataMap2.get("geocodes")).size(); i++) {
            jsonObject2 = ((JSONArray) dataMap2.get("geocodes")).getJSONObject(i);
        }
        Map<String, Object> endMap = jsonObject2.getInnerMap();

        if (startMap.get("province").equals(endMap.get("province"))) {
            if (startMap.get("city").equals(endMap.get("city"))) {
                orderVO.setCityPosition(CityPosition.SAME_CITY.getValue());
            } else {
                orderVO.setCityPosition(CityPosition.IN_PROVINCE.getValue());
            }
        } else {
            orderVO.setCityPosition(CityPosition.OUTSIDE_PROVINCE.getValue());
        }

//        orderVO.setStartLocation((String) startMap.get("location"));
//        orderVO.setEndLocation((String) endMap.get("location"));

        //向redis存入地址地理位置信息
        OrderAddress orderAddress = new OrderAddress(
                (String) startMap.get("location"), (String) endMap.get("location"),
                (String) startMap.get("province"), (String) endMap.get("province"),
                (String) startMap.get("city"), (String) endMap.get("city"),
                (String) startMap.get("district"), (String) endMap.get("district")
        );
        Map<String, Object> map = BeanUtils.beanToMap(orderAddress);
        redisTemplate.opsForHash().putAll(orderVO.getOid() + AppConstant.REDIS_ORDER_ADDRESS_DATA_SUFFIX, map);

    }

}
