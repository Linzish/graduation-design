package me.unc.ldms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @Description StorageService测试类
 * @Date 2020/3/4 14:42
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StorageServiceTest {

    @Autowired
    private StorageService storageService;
    @Autowired
    private DistributionService distributionService;

    @Test
    public void selectWareHouseTest() {
        storageService.selectWareHouse("202003041586109084");
    }

    @Test
    public void storageTest() {
        /*if (storageService.storage("fs0001", "202003041586109084")) {
            System.out.println("ok");
        }*/
        /*if (storageService.storage("fs0005", "202003041586109084")) {
            System.out.println("ok");
        }*/
        /*if (storageService.storage("zj0005", "202003041586109084")) {
            System.out.println("ok");
        }*/
        if (storageService.storage("zj0002", "202003041586109084")) {
            System.out.println("ok");
        }

        List<Map<String, String>> list = distributionService.distributionTracking("202003041586109084");
        list.forEach(map -> map.forEach((key, value) -> System.out.println(key + ":" + value)));
    }

    @Test
    public void outboundTest() {
        /*if (storageService.outbound("fs0001", "202003041586109084")) {
            System.out.println("ok");
        }*/
        /*if (storageService.outbound("fs0005", "202003041586109084", null)) {
            System.out.println("ok");
        }*/
        /*if (storageService.outbound("zj0005", "202003041586109084", null)) {
            System.out.println("ok");
        }*/
        if (storageService.outbound("zj0002", "202003041586109084", null)) {
            System.out.println("ok");
        }

        List<Map<String, String>> list = distributionService.distributionTracking("202003041586109084");
        list.forEach(map -> map.forEach((key, value) -> System.out.println(key + ":" + value)));
    }

}
