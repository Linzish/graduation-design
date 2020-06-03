package me.unc.ldms.service;

import me.unc.ldms.dto.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description UserService测试类
 * @Date 2020/2/20 14:51
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void selectAllUser() {
        List<User> users = userService.loadAllUser();
        users.forEach(System.out::println);
    }

    @Test
    public void selectUserByWid() {
        List<User> users = userService.listUserByWid("zj0002");
        users.forEach(System.out::println);
    }

}
