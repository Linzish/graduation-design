package me.unc.ldms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Role;
import me.unc.ldms.dto.User;
import me.unc.ldms.dto.UserMain;
import me.unc.ldms.dto.UserRole;
import me.unc.ldms.mapper.RoleMapper;
import me.unc.ldms.mapper.UserMainMapper;
import me.unc.ldms.mapper.UserMapper;
import me.unc.ldms.mapper.UserRoleMapper;
import me.unc.ldms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用户服务业务逻辑实现类
 * @Date 2020/2/10 18:31
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserMainMapper userMainMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息实体类
     */
    @Override
    public User login(String username, String password) {
        log.info("Calling UserService [login]");
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    /**
     * 根据uid获取用户详细信息
     * @param uid 用户id
     * @return 用户详细信息
     */
    @Override
    public UserMain getUserMainByUid(String uid) {
        log.info("Calling UserService [getUserMainByUid]");
        return userMainMapper.selectOne(new QueryWrapper<UserMain>().eq("uid", uid));
    }

    /**
     * 根据uid获取用户信息
     * @param uid 用户id
     * @return 用户信息
     */
    @Override
    public User getUserByUid(String uid) {
        log.info("Calling UserService [getUserByUid]");
        return userMapper.selectOne(new QueryWrapper<User>().eq("uid", uid));
    }

    /**
     * 添加用户
     * @param user 用户实体类
     */
    @Override
    @Transactional
    public boolean addUser(User user) {
        log.info("Calling UserService [addUser]");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserMain userMain = new UserMain();
        userMain.setUid(user.getUid());
        return userMapper.insert(user) == 1 && userMainMapper.insert(userMain) == 1;
    }

    /**
     * 添加用户（员工）
     * @param user 用户实体类
     * @param wid 仓储点id
     * @return 是否添加成功
     */
    @Override
    public boolean addUserWithWid(User user, String wid) {
        log.info("Calling UserService [addUserWithWid]");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserMain userMain = new UserMain();
        userMain.setUid(user.getUid());
        userMain.setWid(wid);
        return userMapper.insert(user) == 1 && userMainMapper.insert(userMain) == 1;
    }

    /**
     * 更新用户基本信息
     * @param user 用户实体类
     */
    @Override
    public boolean updateUserBase(User user) {
        log.info("Calling UserService [updateUserBase]");
        return userMapper.update(user, new UpdateWrapper<User>().eq("username", user.getUsername())) == 1;
    }

    /**
     * 更新用户详细信息
     * @param userMain 用户详细信息实体类
     */
    @Override
    public boolean updateUserMain(UserMain userMain) {
        log.info("Calling UserService [updateUserMain]");
        return userMainMapper.update(userMain, new UpdateWrapper<UserMain>().eq("uid", userMain.getUid())) == 1;
    }

    /**
     * （逻辑）删除用户
     * @param uid 用户id
     * @return 影响数据行
     */
    @Override
    public boolean disableUser(String uid) {
        log.info("Calling UserService [disableUser]");
        User disableUser = new User();
        disableUser.setEnable(-1);
        return userMapper.update(disableUser, new UpdateWrapper<User>().eq("uid", uid)) == 1;
    }

    /**
     * （物理）删除用户
     * @param uid 用户id
     * @return 影响数据行
     */
    @Override
    public boolean deleteUser(String uid) {
        log.info("Calling UserService [deleteUser]");
        return userMapper.delete(new QueryWrapper<User>().eq("uid", uid)) == 1;
    }

    /**
     * 获取用户角色权限
     * @param id 用户表主键id
     * @return 权限集合
     */
    @Override
    @Transactional
    public List<Role> getUserRolesById(int id) {
        log.info("Calling UserService [getUserRolesById]");
        List<UserRole> roles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", id));
        List<Integer> ids = new ArrayList<>();
        for (UserRole role : roles) {
            ids.add(role.getRoleId());
        }
        return roleMapper.selectBatchIds(ids);
    }

    /**
     * 添加用户权限
     * @param uid 用户id
     * @param rid 权限id
     */
    @Override
    @Transactional
    public boolean addUserRole(String uid, String rid) {
        log.info("Calling UserService [addUserRole]");
        User user = getUserByUid(uid);
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("rid", rid));
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        return userRoleMapper.insert(userRole) == 1;
    }

    /**
     * 查询全部用户
     * @return 用户列表
     */
    @Override
    public List<User> loadAllUser() {
        log.info("Calling UserService [loadAllUser]");
        return userMapper.selectList(new QueryWrapper<User>());
    }

    /**
     * 查询全部权限
     * @return 权限信息列表
     */
    @Override
    public List<Role> loadAllRole() {
        log.info("Calling UserService [loadAllRole]");
        return roleMapper.selectList(new QueryWrapper<Role>());
    }

    /**
     * 查询仓储点员工
     * @param wid 仓储点id
     * @return 仓储点员工列表
     */
    @Override
    public List<User> listUserByWid(String wid) {
        log.info("Calling UserService [listUserByWid]");
        List<UserMain> userMains = userMainMapper.selectList(new QueryWrapper<UserMain>().select("uid").eq("wid", wid));
        List<User> users = new ArrayList<>();
        for (UserMain userMain : userMains) {
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("uid", userMain.getUid()));
            users.add(user);
        }
        return users;
    }
}
