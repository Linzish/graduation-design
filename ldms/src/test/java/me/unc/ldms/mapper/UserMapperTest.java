package me.unc.ldms.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import me.unc.ldms.dto.User;
import me.unc.ldms.dto.UserMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description UserMapper测试类
 * @Date 2020/2/7 15:53
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserMainMapper userMainMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void select1() {
        User user = userMapper.selectById(2);
        System.out.println(user);
    }

    @Test
    public void select2() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "root");
        map.put("password", "root");
        List<User> users = userMapper.selectByMap(map);
        User user = users.get(0);
        System.out.println(user);
    }

    @Test
    public void insert1() throws ParseException {
        User user = new User();
        user.setUid("zj0002");
        user.setUsername("zj0002");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setPhone("12345678910");
        user.setTime(new Date());
        userMapper.insert(user);
        System.out.println(user);
    }

    @Test
    public void insertMain() {
        User user = userMapper.selectById(1);
        UserMain userMain = new UserMain();
        userMain.setUid(user.getUid());
        userMain.setOrganization("超级管理员");
        userMain.setVip(1);
        userMainMapper.insert(userMain);
    }

    @Test
    public void update1() {
        User user = new User();
        user.setUid("test001");
        user.setPassword("456789");
        userMapper.update(user, new UpdateWrapper<User>().eq("uid", user.getUid()));
    }

    @Test
    public void update2() {
        User user = new User();
        user.setUid("root001");
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.update(user, new UpdateWrapper<User>().eq("uid", user.getUid()));
    }

    @Test
    public void passwordAuth() {
        User user = userMapper.selectById(1);
        System.out.println(user);
        System.out.println(passwordEncoder.matches("123456", user.getPassword()));
    }

}
