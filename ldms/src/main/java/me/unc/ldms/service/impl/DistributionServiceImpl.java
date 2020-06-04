package me.unc.ldms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Distribution;
import me.unc.ldms.dto.WareHouse;
import me.unc.ldms.mapper.DistributionMapper;
import me.unc.ldms.mapper.WareHouseMapper;
import me.unc.ldms.service.DistributionService;
import me.unc.ldms.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 配送服务业务逻辑实现类
 * @Date 2020/2/10 18:30
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class DistributionServiceImpl implements DistributionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DistributionMapper distributionMapper;
    @Autowired
    private WareHouseMapper wareHouseMapper;

    /**
     * 物流信息跟踪
     * @param oid 订单id
     * @return 时间 - 事件
     */
    @Override
    public List<Map<String, String>> distributionTracking(String oid) {
        log.info("calling DistributionService [distributionTracking]");
        List<Map<String, String>> msgList = new ArrayList<>();
        List<Object> list = redisTemplate.opsForList().range(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX, 0, -1);
        Map<String, String> trackingMsg;
        if (list == null || list.size() == 0) {
            trackingMsg = new HashMap<>();
            trackingMsg.put("none", "未查询到相关物流信息");
            msgList.add(trackingMsg);
            return msgList;
        }
        for (int i = 0; i < list.size(); i+=2) {
            trackingMsg = new HashMap<>();
            trackingMsg.put("time", (String) list.get(i));
            trackingMsg.put("msg", (String) list.get(i + 1));
            msgList.add(trackingMsg);
        }

        return msgList;
    }

    /**
     * 仓储点处理新订单信息
     * @param oidKey 订单key值
     */
    @Override
    public void acceptOrder(String oidKey) {
        log.info("calling DistributionService [acceptOrder]");
        //key里存着订单id
        String oid = (String) redisTemplate.opsForValue().get(oidKey);
        //根据订单id获取信息
        Map<Object, Object> order = redisTemplate.opsForHash().entries(oid);
        //TODO -示意已接单（重点，实际情况可能复杂）
        

    }

    /**
     * 获取订单物流信息，用于初始化前端地图
     * @param oid 订单id
     * @return 地理信息
     */
    @Override
    public Map<String, Object> getDistributionMsg(String oid) {
        log.info("calling DistributionService [getDistributionMsg]");
        Map<String, Object> map = new HashMap<>();
        Map<Object, Object> address = redisTemplate.opsForHash().entries(oid + AppConstant.REDIS_ORDER_ADDRESS_DATA_SUFFIX);
        String startLocation = (String) address.get("startLocation");
        String endLocation = (String) address.get("endLocation");
        String[] startSp = startLocation.split(",");
        String[] endSp = endLocation.split(",");
        Double[] start = {Double.valueOf(startSp[0]), Double.valueOf(startSp[1])};
        Double[] end = {Double.valueOf(endSp[0]), Double.valueOf(endSp[1])};
        map.put("start", start);
        map.put("end", end);

        return map;
    }

    /**
     * 获取订单物流路径信息
     * @param oid 订单id
     * @return 订单物流路径信息
     */
    @Override
    public Distribution getOrderDistributionMsg(String oid) {
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>().eq("oid", oid));
        WareHouse startWarehouse = wareHouseMapper.selectOne(new QueryWrapper<WareHouse>().select("address").eq("wid", distribution.getStartWarehouse()));
        WareHouse startPoint = wareHouseMapper.selectOne(new QueryWrapper<WareHouse>().select("address").eq("wid", distribution.getStartPoint()));
        WareHouse endWarehouse = wareHouseMapper.selectOne(new QueryWrapper<WareHouse>().select("address").eq("wid", distribution.getEndWarehouse()));
        WareHouse endPoint = wareHouseMapper.selectOne(new QueryWrapper<WareHouse>().select("address").eq("wid", distribution.getEndPoint()));

        distribution.setStartWarehouse(startWarehouse.getAddress());
        distribution.setStartPoint(startPoint.getAddress());
        distribution.setEndWarehouse(endWarehouse.getAddress());
        distribution.setEndPoint(endPoint.getAddress());

        return distribution;
    }

}
