package me.unc.ldms.security;

import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.Role;
import me.unc.ldms.dto.User;
import me.unc.ldms.dto.UserMain;
import me.unc.ldms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 构建真正用于SpringSecurity登录的安全用户(UserDetails)
 * @Date 2020/2/19 16:22
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 重写UserDetailsService接口中的loadUserByUsername方法，通过该方法查询对应的用户
     * 返回对象 UserDetails 是 Spring Security 的一个核心接口
     * 其中定义一些可以获取用户名、密码、权限等的认证相关信息的方法
     * @param s username用户名
     * @return UserDetails对象
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("AuthenticationService 验证用户信息");
        //调用持久化层接口的方法查找用户
        User user = userService.login(s, null);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        //创建一个List，用于保存用户权限，GrantedAuthority对象代表赋予当前用户的权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        //获取当前用户的权限
        List<Role> roles = userService.getUserRolesById(user.getId());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRid()));
        }
        UserMain userMain = userService.getUserMainByUid(user.getUid());
        //此处需要返回org.springframework.security.core.userdetails.User
        //该类是Spring Security内部的实现，专门用于保存用户名、密码、权限等认证信息
        //这里返回用户名、密码和权限
//        return new org.springframework.security.core.userdetails.User(user.getUid(), user.getPassword(), authorities);
        //自定义一个org.springframework.security.core.userdetails.User 添加一些需要的信息
        UserSecurityModel userSecurityModel = new UserSecurityModel(user.getUid(), user.getPassword(), authorities);
        userSecurityModel.setUser(user);
        userSecurityModel.setUserMain(userMain);

        return userSecurityModel;
    }

}
