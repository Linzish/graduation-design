package me.unc.ldms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.distribution.DijkstraAlgorithm;
import me.unc.ldms.dto.Distribution;
import me.unc.ldms.dto.Storage;
import me.unc.ldms.dto.User;
import me.unc.ldms.dto.WareHouse;
import me.unc.ldms.mapper.DistributionMapper;
import me.unc.ldms.mapper.StorageMapper;
import me.unc.ldms.mapper.UserMapper;
import me.unc.ldms.mapper.WareHouseMapper;
import me.unc.ldms.service.StorageService;
import me.unc.ldms.utils.AppConstant;
import me.unc.ldms.utils.GeneralState;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.storage.LocationType;
import me.unc.ldms.utils.storage.PointIndexType;
import me.unc.ldms.websocket.StorageWebSocket;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description 仓储服务业务逻辑实现类
 * @Date 2020/2/10 18:31
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StorageMapper storageMapper;
    @Autowired
    private WareHouseMapper wareHouseMapper;
    @Autowired
    private DistributionMapper distributionMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ExecutorService asyncExecution;
    @Autowired
    private StorageWebSocket webSocket;

    /**
     * 路线规划
     * @param record kafka消息
     */
    @Override
    @KafkaListener(topics = AppConstant.ORDER_AUDIT_TOPIC, groupId = AppConstant.ORDER_AUDIT_TOPIC_GROUP_ID)
    public void routePlanning(ConsumerRecord<String, String> record) {
        log.info("calling StorageService [routePlanning]");
        //通过kafka获取消息通知
        log.info("StorageService [routePlanning]: get message from kafka, topic = [" + record.topic() + "]");
        //选择仓库点
        //异步进行
        asyncExecution.execute(() -> selectWareHouse(record.value()));
    }

    /**
     * 通知揽件
     * @param record kafka消息
     */
    @Override
    @KafkaListener(topics = AppConstant.ORDER_COLLECTION_TOPIC, groupId = AppConstant.ORDER_COLLECTION_TOPIC_GROUP_ID)
    public void collectionNotify(ConsumerRecord<String, String> record) {
        log.info("calling StorageService [collectionNotify]");
        //通过kafka获取消息通知
        log.info("StorageService [collectionNotify]: get message from kafka, topic = [" + record.topic() + "]");
        String[] value = record.value().split("-");
        String wid = value[0];
        String oid = value[1];
        //TODO -待处理仓储点不在线的问题
        webSocket.sendMsg(oid, wid);
    }

    /**
     * 入库
     * @param wid 仓库id
     * @param oid 订单id
     */
    @Override
    @Transactional
    public boolean storage(String wid, String oid, String createBy) {
        log.info("calling StorageService [storage]");
        Storage storage = new Storage();
        storage.setWid(wid);
        storage.setOid(oid);
        storage.setCreateBy(createBy);
        storage.setInDate(new Date());
        if (!Objects.equals(redisTemplate.opsForValue().get(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX), 3)) {
            storage.setIsTurn(1);
        } else {
            storage.setIsTurn(0);
        }
        //存mysql
        int i = storageMapper.insert(storage);
        //redis记录物流信息，使用 redis 的 list 操作
        String name = wareHouseMapper.getNameByWid(wid);

        if (Objects.equals(redisTemplate.opsForValue().get(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX), 0)) {
            redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX,
                    GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), distributionTrackingMsgFormatter(name, null, 0));
        } else {
            redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX,
                    GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), distributionTrackingMsgFormatter(name, null, 1));
        }
        //更新物流地点索引
        redisTemplate.opsForValue().increment(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX);

        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>().eq("oid", oid));
        distribution.setTracking(distribution.getTracking() + 1);
        int j = distributionMapper.updateById(distribution);

        return i == 1 && j == 1;
    }

    /**
     * 出库
     * @param wid 仓库id
     * @param oid 订单id
     */
    @Override
    @Transactional
    public boolean outbound(String wid, String oid, String tid, String uid, String createBy) {
        log.info("calling StorageService [outbound]");
        Storage storage = storageMapper.selectOne(new QueryWrapper<Storage>().eq("wid", wid).eq("oid", oid).eq("disable", 0));
        //修改数据库
        storage.setOutDate(new Date());
        storage.setIsOut(1);
        storage.setTid(tid);
        storage.setOutBy(createBy);
        int i = storageMapper.updateById(storage);
        //redis记录物流信息
        String name1 = wareHouseMapper.getNameByWid(wid);
        int j = (int) redisTemplate.opsForValue().get(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX);
        if (j < 4) {
            List<Object> range = redisTemplate.opsForList().range(oid + AppConstant.ORDER_DISTRIBUTION_DATA_SUFFIX, j + 1, j + 1);
            String name2 = wareHouseMapper.getNameByWid((String) range.get(0));
            redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX,
                    GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), distributionTrackingMsgFormatter(name1, name2, 2));
        } else {
            //uid
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("uid", uid));
            redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX,
                    GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), distributionTrackingMsgFormatter(name1, user.getRealName() + "-" + user.getPhone(), 3));
        }

        return i == 1;
    }

    /**
     * 更改仓储信息
     * @param storage 仓储信息
     */
    @Override
    public boolean modifyStorageMsg(Storage storage) {
        log.info("calling StorageService [modifyStorageMsg]");
        return storageMapper.update(storage, new UpdateWrapper<Storage>().eq("oid", storage.getOid()).eq("wid", storage.getWid())) == 1;
    }

    /**
     * 选择仓库
     * @param oid 订单id
     */
    @Override
    @Transactional
    public void selectWareHouse(String oid) {
        log.info("calling StorageService [selectWareHouse]");
        selectWareHouse0(oid);
        log.info("StorageService [selectWareHouse]: success complete, oid = [" + oid + "]");
    }

    /**
     * 查询当前仓库所有订单
     * @param wid 仓库id
     * @return 订单集合
     */
    @Override
    public List<String> selectWHsOrder(String wid) {
        log.info("calling StorageService [selectWHsOrder]");
        List<Storage> storageList = storageMapper.selectList(new QueryWrapper<Storage>().eq("wid", wid).eq("disable", 0));
        List<String> list = new ArrayList<>();
        for (Storage storage : storageList) {
            list.add(storage.getOid());
        }
        //可以优化，自定义一条mapper
        return list;
    }

    /**
     * 根据仓储点id查看当前仓储点的订单信息
     * @param wid 仓储点id
     * @return 仓储信息集合
     */
    @Override
    public List<Storage> selectWHsStorageMsg(String wid) {
        log.info("calling StorageService [selectWHsStorageMsg]");
        return storageMapper.selectList(new QueryWrapper<Storage>().eq("wid", wid).eq("disable", 0));
    }

    /**
     * 根据订单id和仓储id查询仓储信息
     * @param oid 订单号
     * @param wid 仓储id
     * @return 仓储信息
     */
    @Override
    public Storage selectOne(String oid, String wid) {
        log.info("calling StorageService [selectOne]");
        return storageMapper.selectOne(new QueryWrapper<Storage>().eq("oid", oid).eq("wid", wid).eq("disable", 0));
    }

    /**
     * 根据订单id查询物流信息
     * @param oid 订单号
     * @return 仓储信息集合
     */
    @Override
    public List<Storage> selectByOid(String oid) {
        log.info("calling StorageService [selectByOid]");
        return storageMapper.selectList(new QueryWrapper<Storage>().eq("oid", oid).eq("disable", 0));
    }

    /**
     * 选择仓库，生成订单物流配送全流程
     * @param oid 订单id
     */
    protected void selectWareHouse0(String oid) {
        if (oid == null) {
            throw new IllegalArgumentException("订单id为空");
        }
        //选择仓储点
        //1.redis取出信息（坐标信息）
        //String startLocation = (String) redisTemplate.opsForHash().get(oid + AppConstant.REDIS_ORDER_ADDRESS_DATA_SUFFIX, "startLocation");
        Map<Object, Object> address = redisTemplate.opsForHash().entries(oid + AppConstant.REDIS_ORDER_ADDRESS_DATA_SUFFIX);
        //String isSameCity = (String) redisTemplate.opsForHash().get(oid, "cityPosition");
        if (address.isEmpty()) {
            throw new IllegalArgumentException("找不到该订单信息，oid = " + oid);
        }

        //根据地理信息查询起点和终点的仓库
        List<WareHouse> startWareHouses = wareHouseMapper.selectList(new QueryWrapper<WareHouse>()
                .eq("province", address.get("startProvince"))
                .eq("city", address.get("startCity"))
                //.eq("district", address.get("startDistrict"))
                .eq("location_type", LocationType.WAREHOUSE.ordinal())
                .eq("status", GeneralState.ENABLE.ordinal()));
        List<WareHouse> endWareHouses = wareHouseMapper.selectList(new QueryWrapper<WareHouse>()
                .eq("province", address.get("endProvince"))
                .eq("city", address.get("endCity"))
                //.eq("district", address.get("endDistrict"))
                .eq("location_type", LocationType.WAREHOUSE.ordinal())
                .eq("status", GeneralState.ENABLE.ordinal()));

        //根据地理信息查询起点和终点的转运点
        List<WareHouse> startPoints = wareHouseMapper.selectList(new QueryWrapper<WareHouse>()
                .eq("province", address.get("startProvince"))
                .eq("city", address.get("startCity"))
                .eq("location_type", LocationType.TRANSSHIPMENT_POINT.ordinal())
                .eq("status", GeneralState.ENABLE.ordinal()));
        List<WareHouse> endPoints = wareHouseMapper.selectList(new QueryWrapper<WareHouse>()
                .eq("province", address.get("endProvince"))
                .eq("city", address.get("endCity"))
                .eq("location_type", LocationType.TRANSSHIPMENT_POINT.ordinal())
                .eq("status", GeneralState.ENABLE.ordinal()));

        //生成数据，数据序列化
        String[] addressIndex = {"start"};
        String[] end = {"end"};
        int[] listIndex = {startWareHouses.size(), startPoints.size(), endPoints.size(), endWareHouses.size()};

        //AtomicReference<String> sIndex = new AtomicReference<>("");
        List<String> sIndex = new ArrayList<>();
        startWareHouses.forEach(wareHouse -> sIndex.add(wareHouse.getId() + ""));
        startPoints.forEach(wareHouse -> sIndex.add(wareHouse.getId() + ""));
        endPoints.forEach(wareHouse -> sIndex.add(wareHouse.getId() + ""));
        endWareHouses.forEach(wareHouse -> sIndex.add(wareHouse.getId() + ""));
        String[] split = new String[sIndex.size()];
        sIndex.toArray(split);
        //合并
        addressIndex = GeneralUtils.concat(addressIndex, split);
        addressIndex = GeneralUtils.concat(addressIndex, end);
        log.info("[selectWareHouse] -> oid = " + oid + ",select tb_wh_id list = [" + String.join(", ", addressIndex) + "]");

        //生成数据，数据序列化
        //map<String, int>，String格式为 0:1 ，代表起始和终点，两个对应位置索引之间的距离，位置索引在这保存
        Map<String, Integer> indexDistance;

        //{start, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, end}
        //listIndex[0] = 4 -> startWareHouses.size()
        //listIndex[1] = 2 -> startPoints.size()
        //listIndex[2] = 2 -> endPoints.size()
        //listIndex[3] = 4 -> endWareHouses.size()

        //保持原设计！！ 起点-仓库-转运点-转运点-仓库-目的地

        //起始地到仓库
        String startWHs = genLocationStr(startWareHouses);
        indexDistance = distanceMeasurement(startWHs, (String) address.get("startLocation"), 0, 0, PointIndexType.START_TO_STARTWH);
        //仓库到转运点
        for (int i = 0; i < startPoints.size(); i++) {
            indexDistance.putAll(distanceMeasurement(startWHs, startPoints.get(i).getWPosition(), 0, (listIndex[0] + i + 1), PointIndexType.STARTWH_TO_STARTPOINT));
            //转运点到转运点
            for (int j = 0; j < endPoints.size(); j++) {
                indexDistance.putAll(distanceMeasurement(startPoints.get(i).getWPosition(), endPoints.get(j).getWPosition(), listIndex[0] + i + 1, listIndex[0] + listIndex[1] + j + 1, PointIndexType.STARTPOINT_TO_ENDPOINT));
            }
        }

        String endWH = genLocationStr(endWareHouses);
        //转运点到仓库
        for (int i = 0; i < endPoints.size(); i++) {
            indexDistance.putAll(distanceMeasurement(endWH, endPoints.get(i).getWPosition(), listIndex[0] + listIndex[1] + i + 1, listIndex[0] + listIndex[1] + listIndex[2] + 1, PointIndexType.ENDPOINT_TO_ENDWH));
        }
        //仓库到目的地
        indexDistance.putAll(distanceMeasurement(endWH, (String) address.get("endLocation"), listIndex[0] + listIndex[1] + listIndex[2] + 1, addressIndex.length -1, PointIndexType.ENDWH_TO_END));

        //生成代数矩阵，然后算法选择 --迪杰斯特拉算法
        //生成最短路径
        int[][] matrix = DijkstraAlgorithm.buildAdjacencyMatrix(indexDistance, addressIndex.length);
        String[] result = DijkstraAlgorithm.dijkstra(matrix, 0);

        //2.返回最短距离（路程），过滤，收集最短路径信息
        //3.信息存入redis
        //TODO -index数组数据合法性校验
        String[] index = result[addressIndex.length - 1].split("-");
        log.info("[selectWareHouse] -> oid = " + oid + ",distribution wh_id list = [" + String.join(", ", index) + "]");

        Distribution distribution = new Distribution();
        distribution.setOid(oid);
        String startAddress = (String) redisTemplate.opsForHash().get(oid, "startAddress");
        distribution.setStartLocation((String) address.get("startLocation"));
        distribution.setStartAddress(startAddress);
        distribution.setEndAddress((String) redisTemplate.opsForHash().get(oid, "destinationAddress"));
        distribution.setEndLocation((String) address.get("endLocation"));
        String startWid = wareHouseMapper.selectById(addressIndex[Integer.parseInt(index[1])]).getWid();
        distribution.setStartWarehouse(startWid);
        distribution.setStartPoint(wareHouseMapper.selectById(addressIndex[Integer.parseInt(index[2])]).getWid());
        distribution.setEndPoint(wareHouseMapper.selectById(addressIndex[Integer.parseInt(index[3])]).getWid());
        distribution.setEndWarehouse(wareHouseMapper.selectById(addressIndex[Integer.parseInt(index[4])]).getWid());

        //TODO -用于调试
        System.out.println(distribution);
        System.out.println("数据库操作");
        System.out.println("stop");

        distributionMapper.insert(distribution);
        //Map<String, Object> beanMap = BeanUtils.beanToMap(distribution);
        //redisTemplate.opsForHash().putAll(oid + AppConstant.ORDER_DISTRIBUTION_DATA_SUFFIX, beanMap);
        redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_DATA_SUFFIX,
                startAddress, startWid, distribution.getStartPoint(),
                distribution.getEndPoint(), distribution.getEndWarehouse(), distribution.getEndLocation());

        //redis记录物流信息跟踪
        redisTemplate.opsForList().rightPushAll(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_SUFFIX, GeneralUtils.parseDateToStr(new Date(), "yyyy-MM-dd hh:mm"), "等待揽件");
        redisTemplate.opsForValue().set(oid + AppConstant.ORDER_DISTRIBUTION_TRACKING_INDEX_SUFFIX, 0);

        //通知起点仓储点收货
        //redisTemplate.opsForValue().set(startWid + "-" + RandomStringUtils.randomAscii(8, 16), oid);
        //kafka发出揽件信息
        String msg = startWid + "-" + oid;
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(AppConstant.ORDER_COLLECTION_TOPIC, msg);
        //设置回调函数
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {  //失败
                log.error("kafka send message failure, ex = {}, topic = {}, data = {}", throwable, AppConstant.ORDER_COLLECTION_TOPIC, msg);
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {  //成功
                log.info("kafka send message success, topic = {}, data = {}", AppConstant.ORDER_COLLECTION_TOPIC, matrix);
            }
        });
    }

    /**
     * 生成连续起点坐标字符串，用于多起点距离测量
     * @param list 坐标点集合
     * @return 连续坐标字符串，格式为 “xxx|xxx|xxx”
     */
    private String genLocationStr(List<WareHouse> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                sb.append(list.get(i).getWPosition());
            } else {
                sb.append("|").append(list.get(i).getWPosition());
            }
        }
        return sb.toString();
    }

    /**
     * 距离测量，支持多源点到单终点距离测量
     * @param start 起始坐标
     * @param end 终点坐标
     * @param startIndex 起点索引
     * @param endIndex 终点索引
     * @param type 两点运输类型
     * @return 两索引点之间的距离(m)
     */
    protected Map<String, Integer> distanceMeasurement(String start, String end, int startIndex,int endIndex, PointIndexType type) {
        String jsonData = restTemplate.getForObject("https://restapi.amap.com/v3/distance?origins={1}&destination={2}&key={3}",
                String.class, start, end, AppConstant.API_KEY);
        Map<String, Object> dataMap = (Map<String, Object>) JSON.parse(jsonData);

        Map<String, Integer> indexDistance = new HashMap<>();
        JSONObject jsonObject = null;
        for (int i = 0; i < ((JSONArray) dataMap.get("results")).size(); i++) {
            jsonObject = ((JSONArray) dataMap.get("results")).getJSONObject(i);
            switch (type) {
                case START_TO_STARTWH:
                    indexDistance.put(startIndex + ":" + (i + 1), Integer.parseInt((String) jsonObject.get("distance")));
                    break;
                case STARTWH_TO_STARTPOINT:
                    indexDistance.put((i + 1) + ":" + endIndex, Integer.parseInt((String) jsonObject.get("distance")));
                    break;
                case STARTPOINT_TO_ENDPOINT:
                    indexDistance.put(startIndex + ":" + endIndex, Integer.parseInt((String) jsonObject.get("distance")));
                    break;
                case ENDPOINT_TO_ENDWH:
                    indexDistance.put(startIndex + ":" + (endIndex + i), Integer.parseInt((String) jsonObject.get("distance")));
                    break;
                case ENDWH_TO_END:
                    indexDistance.put((startIndex + i) + ":" + endIndex, Integer.parseInt((String) jsonObject.get("distance")));
                    break;
            }

        }
        return indexDistance;
    }

    /**
     * 物流信息记录格式化
     * @param start 本站
     * @param end 下一站
     * @param flag 标记
     * @return 物流信息
     */
    protected String distributionTrackingMsgFormatter(String start, String end, int flag) {
        if (flag == 0) {
            return "【" + start + "】已收件";
        } else if (flag == 1) {
            return "到达【" + start + "】";
        } else if (flag == 2) {
            return "离开【" + start + "】，下一站【" + end + "】";
        } else if (flag == 3) {
            String[] split = end.split("-");
            return "【" + start + "】安排配送，配送员【" + split[0] + "】，" + "电话【" + split[1] + "】";
        }
        return "";
    }

}
