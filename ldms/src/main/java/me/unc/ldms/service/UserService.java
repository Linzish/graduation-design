package me.unc.ldms.service;

import me.unc.ldms.dto.Role;
import me.unc.ldms.dto.User;
import me.unc.ldms.dto.UserMain;

import java.util.List;

/**
 * @Description 用户服务业务逻辑接口
 * @Date 2020/2/10 18:31
 * @author LZS
 * @version v1.0
 */
public interface UserService {

    User login(String username, String password);

    UserMain getUserMainByUid(String uid);

    User getUserByUid(String uid);

    boolean addUser(User user);

    boolean addUserWithWid(User user, String wid);

    boolean updateUserBase(User user);

    boolean updateUserMain(UserMain userMain);

    boolean disableUser(String uid);

    boolean deleteUser(String uid);

    List<Role> getUserRolesById(int id);

    boolean addUserRole(String uid, String rid);

    List<User> loadAllUser();

    List<Role> loadAllRole();



}
