package me.unc.ldms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @Description DistributionService测试类
 * @Date 2020/4/21 10:09
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DistributionServiceTest {

    @Autowired
    private DistributionService distributionService;

    @Test
    public void distributionTrackingTest() {
        List<Map<String, String>> list = distributionService.distributionTracking("202003041586109084");
        list.forEach(map -> map.forEach((key, value) -> System.out.println(key + ":" + value)));
    }

}
