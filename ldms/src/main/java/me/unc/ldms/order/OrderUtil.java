package me.unc.ldms.order;

import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.OrderDetail;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.order.*;
import me.unc.ldms.vo.OrderVO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 订单转换工具
 * @Date 2020/3/12 10:05
 * @author LZS
 * @version v1.0
 */
public class OrderUtil {

    private static Map<String, Class<? extends Enum<?>>> ENUM_MAP;

    static {  //初始化数据，map中存储订单相关的枚举类
        ENUM_MAP = new HashMap<>();
        ENUM_MAP.put("status", Status.class);
        ENUM_MAP.put("type", Type.class);
        ENUM_MAP.put("typeTag", TypeTag.class);
        ENUM_MAP.put("packaging", Packaging.class);
        ENUM_MAP.put("expressType", ExpressType.class);
        ENUM_MAP.put("payWay", PayWay.class);
        ENUM_MAP.put("isPay", IsPay.class);
        ENUM_MAP.put("cityPosition", CityPosition.class);
    }

    /**
     * 把订单VO类转换成Order实体类
     * 如果传入Order实体类，代表根据传入的订单VO类更新Order实体类
     * @param orderVO 订单VO类
     * @param order order实体类
     * @param diff 订单VO类更新的字段名
     * @return order实体类
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws NoSuchFieldException NoSuchFieldException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static Order orderVOToOrderEntity(OrderVO orderVO, Order order, List<String> diff) throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Order reorder = null;
        if (order == null && diff == null) {
            reorder = new Order();
            reorder.setUid(orderVO.getUid());
            reorder.setOid(orderVO.getOid());
            reorder.setStartAddress(orderVO.getStartAddress());
            reorder.setDestinationAddress(orderVO.getDestinationAddress());
            reorder.setStatus(getOrdinalFromEnumByKey(Status.class, orderVO.getStatus()));
        } else {
            reorder = order;
            for (String s : diff) {
                Field of = Order.class.getDeclaredField(s);
                of.setAccessible(true);
                Field ovf = OrderVO.class.getDeclaredField(s);
                ovf.setAccessible(true);
                if (!ENUM_MAP.containsKey(s)) {
                    of.set(reorder, ovf.get(orderVO));
                } else {
                    of.set(reorder, getOrdinalFromEnumByKey(ENUM_MAP.get(s), (String) ovf.get(orderVO)));
                }
            }
        }

        return reorder;
    }

    /**
     * 把订单VO类转换成OrderDetail实体类
     * 如果传入OrderDetail实体类，代表根据传入的订单VO类更新OrderDetail实体类
     * @param orderVO 订单VO类
     * @param orderDetail OrderDetail实体类
     * @param diff 订单VO类更新的字段名
     * @return OrderDetail实体类
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws NoSuchFieldException NoSuchFieldException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public static OrderDetail orderVOToOrderDetailEntity(OrderVO orderVO, OrderDetail orderDetail, List<String> diff) throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        OrderDetail reorderDetail = null;
        if (orderDetail == null && diff == null) {
            reorderDetail = new OrderDetail();
            reorderDetail.setOid(orderVO.getOid());
            reorderDetail.setStartName(orderVO.getStartName());
            reorderDetail.setStartPhone(orderVO.getStartPhone());
            reorderDetail.setDestinationName(orderVO.getDestinationName());
            reorderDetail.setDestinationPhone(orderVO.getDestinationPhone());
            reorderDetail.setPrice(orderVO.getPrice());
            reorderDetail.setWeight(orderVO.getWeight());
            reorderDetail.setValuation(orderVO.getValuation());
            reorderDetail.setNote(orderVO.getNote());
            reorderDetail.setType(getOrdinalFromEnumByKey(Type.class, orderVO.getType()));
            reorderDetail.setTypeTag(getOrdinalFromEnumByKey(TypeTag.class, orderVO.getTypeTag()));
            reorderDetail.setPackaging(getOrdinalFromEnumByKey(Packaging.class, orderVO.getPackaging()));
            reorderDetail.setExpressType(getOrdinalFromEnumByKey(ExpressType.class, orderVO.getExpressType()));
            reorderDetail.setPayWay(getOrdinalFromEnumByKey(PayWay.class, orderVO.getPayWay()));
            reorderDetail.setIsPay(getOrdinalFromEnumByKey(IsPay.class, orderVO.getIsPay()));
            reorderDetail.setCityPosition(getOrdinalFromEnumByKey(CityPosition.class, orderVO.getCityPosition()));
        } else {
            reorderDetail = orderDetail;
            for (String s : diff) {
                Field odf = OrderDetail.class.getDeclaredField(s);
                odf.setAccessible(true);
                Field ovf = OrderVO.class.getDeclaredField(s);
                ovf.setAccessible(true);
                if (!ENUM_MAP.containsKey(s)) {
                    odf.set(reorderDetail, ovf.get(orderVO));
                } else {
                    odf.set(reorderDetail, getOrdinalFromEnumByKey(ENUM_MAP.get(s), (String) ovf.get(orderVO)));
                }
            }
        }

        return reorderDetail;
    }

    /**
     * 把Order实体类转换成订单VO类
     * 如果传入订单VO类代表更新订单VO类
     * @param order Order实体类
     * @param orderVO 订单VO类
     * @return 订单VO类
     */
    public static void orderEntityToOrderVO(Order order, OrderVO orderVO) {
//        OrderVO reOrderVO = null;
//        if (orderVO == null) {
//            reOrderVO = new OrderVO();
//            reOrderVO.setUid(order.getUid());
//            reOrderVO.setOid(order.getOid());
//            reOrderVO.setStartAddress(order.getStartAddress());
//            reOrderVO.setDestinationAddress(order.getDestinationAddress());
//            reOrderVO.setGenDate(GeneralUtils.parseDateToStr(order.getGenDate(), "yyyy-MM-dd HH:mm"));
//        } else {
//            reOrderVO = orderVO;
//            reOrderVO.setStartAddress(order.getStartAddress());
//            reOrderVO.setDestinationAddress(order.getDestinationAddress());
//        }
//        reOrderVO.setStatus(Status.getOrderStatusByOrdinal(order.getStatus()).getValue());
//
//        return reOrderVO;

        orderVO.setUid(order.getUid());
        orderVO.setOid(order.getOid());
        orderVO.setStartAddress(order.getStartAddress());
        orderVO.setDestinationAddress(order.getDestinationAddress());
        orderVO.setGenDate(GeneralUtils.parseDateToStr(order.getGenDate(), "yyyy-MM-dd HH:mm"));
        orderVO.setStatus(Status.getOrderStatusByOrdinal(order.getStatus()).getValue());
        //return orderVO;
    }

    /**
     * 把OrderDetail实体类转换成订单VO类
     * 如果传入订单VO类代表更新订单VO类
     * @param orderDetail OrderDetail实体类
     * @param orderVO 订单VO类
     * @return 订单VO类
     */
    public static void orderDetailEntityToOrderVO(OrderDetail orderDetail, OrderVO orderVO) {
//        OrderVO reOrderVO = null;
//        if (orderVO == null) {
//            reOrderVO = new OrderVO();
//            reOrderVO.setStartName(orderDetail.getStartName());
//            reOrderVO.setStartPhone(orderDetail.getStartPhone());
//            reOrderVO.setDestinationName(orderDetail.getDestinationName());
//            reOrderVO.setDestinationPhone(orderDetail.getDestinationPhone());
//            reOrderVO.setPrice(orderDetail.getPrice());
//            reOrderVO.setWeight(orderDetail.getWeight());
//            reOrderVO.setValuation(orderDetail.getValuation());
//            reOrderVO.setNote(orderDetail.getNote());
//            reOrderVO.setType(Type.getTypeByOrdinal(orderDetail.getType()).getValue());
//            reOrderVO.setTypeTag(TypeTag.getTypeTagByOrdinal(orderDetail.getTypeTag()).getValue());
//            reOrderVO.setPackaging(Packaging.getPackagingByOrdinal(orderDetail.getPackaging()).getValue());
//            reOrderVO.setExpressType(ExpressType.getExpressTypeByOrdinal(orderDetail.getExpressType()).getValue());
//            reOrderVO.setPayWay(PayWay.getPayWayByOrdinal(orderDetail.getPayWay()).getValue());
//            reOrderVO.setIsPay(IsPay.getPayByOrdinal(orderDetail.getIsPay()).getValue());
//            reOrderVO.setCityPosition(CityPosition.getCityPositionByOrdinal(orderDetail.getCityPosition()).getValue());
//        } else {
//            reOrderVO = orderVO;
//        }
//
//
//        return reOrderVO;

        orderVO.setStartName(orderDetail.getStartName());
        orderVO.setStartPhone(orderDetail.getStartPhone());
        orderVO.setDestinationName(orderDetail.getDestinationName());
        orderVO.setDestinationPhone(orderDetail.getDestinationPhone());
        orderVO.setPrice(orderDetail.getPrice());
        orderVO.setWeight(orderDetail.getWeight());
        orderVO.setValuation(orderDetail.getValuation());
        orderVO.setNote(orderDetail.getNote());
        orderVO.setType(Type.getTypeByOrdinal(orderDetail.getType()).getValue());
        orderVO.setTypeTag(TypeTag.getTypeTagByOrdinal(orderDetail.getTypeTag()).getValue());
        orderVO.setPackaging(Packaging.getPackagingByOrdinal(orderDetail.getPackaging()).getValue());
        orderVO.setExpressType(ExpressType.getExpressTypeByOrdinal(orderDetail.getExpressType()).getValue());
        orderVO.setPayWay(PayWay.getPayWayByOrdinal(orderDetail.getPayWay()).getValue());
        orderVO.setIsPay(IsPay.getPayByOrdinal(orderDetail.getIsPay()).getValue());
        orderVO.setCityPosition(CityPosition.getCityPositionByOrdinal(orderDetail.getCityPosition()).getValue());
        //return orderVO;
    }

    /**
     * 邮费计算
     * @param orderVO 订单VO类
     * @return 邮费
     */
    public static double posCalculator(OrderVO orderVO) {
        double price = 12;

        //ExpressType
        switch (ExpressType.getExpressTypeByKey(orderVO.getExpressType())) {
            case NORMAL:
                break;
            case URGENT:
                price += 5;
                break;
            case NEXT_MORNING:
                price += 10;
                break;
        }

        //weight
        if (orderVO.getWeight() > 0.5) {
            double extraWeight = orderVO.getWeight() - 0.5;
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
            switch (Objects.requireNonNull(CityPosition.getCityPositionByKey(orderVO.getCityPosition()))) {
                case SAME_CITY:
                    price += (lowExtraPrice + highExtraPrice) * 1.5;
                    break;
                case IN_PROVINCE:
                    price += (lowExtraPrice + highExtraPrice) * 2;
                    break;
                case OUTSIDE_PROVINCE:
                    price += (lowExtraPrice + highExtraPrice) * 2.5;
                    break;
                case OVERSEAS:
                    price += (lowExtraPrice + highExtraPrice) * 3;
                    break;
            }
        }

        //packaging
        if (orderVO.getPackaging().equals(Packaging.NO_PACKAGING.getValue())) {
            price += 10;
        }

        //Valuation
        if (orderVO.getValuation() > 0) {
            double valuation = orderVO.getValuation();
            double extraPrice = 0;
            if (valuation > 0 && valuation < 500) {
                extraPrice = valuation / 100;
            } else if (valuation > 500 && valuation < 1000) {
                extraPrice = (valuation / 100) * 2;
            } else if (valuation > 1000) {
                extraPrice = (valuation / 1000) * 5;
            }
            price += extraPrice;
        }

        //cityPosition
        switch (Objects.requireNonNull(CityPosition.getCityPositionByKey(orderVO.getCityPosition()))) {
            case SAME_CITY:
                break;
            case IN_PROVINCE:
                price += 3;
                break;
            case OUTSIDE_PROVINCE:
                price += 6;
                break;
            case OVERSEAS:
                price += 13;
                break;
        }

        return price;
    }

    /**
     * 根据key值获取枚举类的枚举索引
     * @param enumClass 枚举类元信息
     * @param key 枚举value值
     * @return 枚举索引
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchFieldException NoSuchFieldException
     */
    private static int getOrdinalFromEnumByKey(Class<? extends Enum<?>> enumClass, String key) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        Enum<?>[] constants = enumClass.getEnumConstants();
        Method getValue = enumClass.getMethod("getValue");
        for (Enum<?> constant : constants) {
            if (getValue.invoke(constant) == key) {
                return constant.ordinal();
            }
        }

        return 0;
    }

}
