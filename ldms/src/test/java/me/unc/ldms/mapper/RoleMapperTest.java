package me.unc.ldms.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import me.unc.ldms.dto.Role;
import me.unc.ldms.dto.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description RoleMapper测试类
 * @Date 2020/2/7 17:13
 * @author LZS
 * @version v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Test
    public void insert() {
        Role role = new Role();
        role.setRid("ROLE_ROOT");
        roleMapper.insert(role);
    }

    @Test
    public void select1() {
        Role role = roleMapper.selectById(1);
        System.out.println(role);
    }

    @Test
    public void select2() {
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("id", 1));
        System.out.println(role);
    }

    @Test
    public void select3() {
        List<UserRole> user_id = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", 1));
        System.out.println(user_id);
    }

    @Test
    public void addRole() {
        UserRole userRole = new UserRole();
        userRole.setUserId(2);
        userRole.setRoleId(2);
        userRoleMapper.insert(userRole);
    }

}
