package me.unc.ldms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import me.unc.ldms.distribution.DijkstraAlgorithm;
import me.unc.ldms.dto.Order;
import me.unc.ldms.dto.UserMain;
import me.unc.ldms.order.OrderUtil;
import me.unc.ldms.utils.AppConstant;
import me.unc.ldms.utils.GeneralUtils;
import me.unc.ldms.utils.SnowflakeUtils;
import me.unc.ldms.utils.order.Status;
import me.unc.ldms.vo.OrderVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author LZS
 * @version v1.0
 * @Description 基础测试
 * @Date 2020/2/8 14:44
 */
public class Test1 {

    @Test
    public void test1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        System.out.println(date);
        String test = "test001";
        int hash = GeneralUtils.genHash(date + test);
        System.out.println(hash);
        System.out.println(date + hash);
    }

    @Test
    public void test2() {
        UserMain userMain = new UserMain();
        userMain.setId(1);
        userMain.setUid("asdas223");
        userMain.setOrganization("me.unc");
        userMain.setVip(1);
        Map<String, Object> stringObjectMap = BeanUtils.beanToMap(userMain);
        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * 测试序列化，取出地理编码
     */
    @Test
    public void test3() {
        RestTemplate restTemplate = new RestTemplate();
        String start = "广东省佛山市南海区佛山市南海万科广场";
        //String end = "广东省湛江市麻章区湖光镇海大路1号广东海洋大学";
        String jsonData = restTemplate.getForObject("https://restapi.amap.com/v3/geocode/geo?address={1}&key={2}",
                String.class, start, AppConstant.API_KEY);
        Map<String, Object> dataMap = (Map<String, Object>) JSON.parse(jsonData);
        JSONObject jsonObject = null;
        for (int i = 0; i < ((JSONArray) dataMap.get("geocodes")).size(); i++) {
            jsonObject = ((JSONArray) dataMap.get("geocodes")).getJSONObject(i);
        }
        //System.out.println(jsonObject);
        Map<String, Object> innerMap = jsonObject.getInnerMap();
        for (Map.Entry<String, Object> entry : innerMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * 测试序列化取出距离测算值
     */
    @Test
    public void test4() {
        RestTemplate restTemplate = new RestTemplate();
        String start = "116.481028,39.989643|114.481028,39.989643|115.481028,39.989643";
        String end = "114.465302,40.004717";
        String jsonData = restTemplate.getForObject("https://restapi.amap.com/v3/distance?origins={1}&destination={2}&key={3}",
                String.class, start, end, AppConstant.API_KEY);
        Map<String, Object> dataMap = (Map<String, Object>) JSON.parse(jsonData);
        JSONObject jsonObject = null;
        for (int i = 0; i < ((JSONArray) dataMap.get("results")).size(); i++) {
            jsonObject = ((JSONArray) dataMap.get("results")).getJSONObject(i);
            System.out.println(Integer.parseInt((String) jsonObject.get("distance")));
        }

//        Map<String, Object> innerMap = jsonObject.getInnerMap();
//        for (Map.Entry<String, Object> entry : innerMap.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
    }

    @Test
    public void Snowflake() {
        Set<Long> ids = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            ids.add(SnowflakeUtils.genId());
        }
        for (Long id : ids) {
            System.out.println(id);
        }
        long end = System.currentTimeMillis();
        System.out.println("共生成id[" + ids.size() + "]个，花费时间[" + (end - start) + "]毫秒");
    }

    @Test
    public void DijkstraTest1() {
        int[][] matrix = new int[7][7];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = AppConstant.MAX_VALUE;
            }
        }
        matrix[0][1] = 6;
        matrix[1][2] = 5;
        matrix[0][3] = 2;
        matrix[3][1] = 7;
        matrix[3][4] = 5;
        matrix[1][5] = 3;
        matrix[4][5] = 5;
        matrix[4][6] = 1;
        matrix[5][2] = 2;

        DijkstraAlgorithm.dijkstra(matrix, 0);

    }

    @Test
    public void DijkstraTest2() {
        Random random = new Random();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 1; i < 5; i++) {
            map.put(0 + ":" + i, random.nextInt(100000));
            map.put(i + ":" + 5, random.nextInt(100000));
            map.put(i + ":" + 6, random.nextInt(100000));
//            map.put(i + ":" + 9, random.nextInt(100000));
//            map.put(i + ":" + 10, random.nextInt(100000));
//            map.put(i + ":" + 11, random.nextInt(100000));
//            map.put(i + ":" + 12, random.nextInt(100000));
        }
        map.put(5 + ":" + 7, random.nextInt(100000));
        map.put(5 + ":" + 8, random.nextInt(100000));
        map.put(6 + ":" + 7, random.nextInt(100000));
        map.put(6 + ":" + 8, random.nextInt(100000));
        for (int i = 9; i < 13; i++) {
            map.put(7 + ":" + i, random.nextInt(100000));
            map.put(8 + ":" + i, random.nextInt(100000));
            map.put(i + ":" + 13, random.nextInt(100000));
        }
        map.forEach((s, i) -> System.out.println(s + " -> " + i));
        int[][] ints = DijkstraAlgorithm.buildAdjacencyMatrix(map, 14);
        System.out.println(Arrays.deepToString(ints));
        DijkstraAlgorithm.dijkstra(ints, 0);
    }

    @Test
    public void testArr() {
        String[] arr1 = {"1"};
        String[] arr2 = new String[4];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = (i+2) + "";
        }
        arr1 = GeneralUtils.concat(arr1, arr2);
        /*for (Integer integer : arr1) {
            System.out.println(integer);
        }*/
        //String s1 = Arrays.stream(arr1).filter(s -> !s.isEmpty()).collect(Collectors.joining(","));

        AtomicReference<String> s = new AtomicReference<>("");
        Arrays.stream(arr2).forEach(s1 -> s.updateAndGet(v -> v + s1));
        System.out.println(s.get());

        String ss = String.join(",", arr1);
        System.out.println("[" + ss + "]");
        System.out.println(arr1.length);
    }

    @Test
    public void testMail() {
        GeneralUtils.mailService("xxxxx");
        //System.out.println(GeneralUtils.filterMailAddress("71a4@q126.com"));
    }

    @Test
    public void testRandomString() {
        for (int i = 0; i < 10; i++) {
            System.out.println(RandomStringUtils.randomAscii(8, 16));
        }
    }

    @Test
    public void enumUtilTest() throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
//        int value = OrderUtil.getOrdinalFromEnumByKey(Status.class, "已完成");
//        System.out.println(value);
//        Class<OrderStatus> orderStatusClass = OrderStatus.class;
//        Method method = orderStatusClass.getMethod("values");
//        System.out.println(method);
    }

    @Test
    public void compareUtilTest() throws Exception {
        OrderVO o1 = new OrderVO();
        OrderVO o2 = new OrderVO();
        o1.setUid("11111");
        o2.setUid("22222");
        o1.setOid("11111");
        o2.setOid("22222");
        o1.setStartAddress("xxxx");
        o2.setStartAddress("xxxx");
        List<String> obj = GeneralUtils.compareObj(o1, o2);
        for (String s : obj) {
            System.out.println(s);
        }
        Order order = OrderUtil.orderVOToOrderEntity(o1, null, null);
        System.out.println(order);
    }

    @Test
    public void testHash() {
        System.out.println(GeneralUtils.genHash("我是帅B"));
    }

}
