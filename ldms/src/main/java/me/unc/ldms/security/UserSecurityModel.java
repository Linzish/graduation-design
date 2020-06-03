package me.unc.ldms.security;

import lombok.Data;
import me.unc.ldms.dto.UserMain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @Description 对Spring Security的User对象再封装，添加一些业务需要的数据
 * @Date 2020/4/23 10:42
 * @author LZS
 * @version v1.0
 */
public class UserSecurityModel extends User {

    protected me.unc.ldms.dto.User user;

    protected UserMain userMain;

    public UserSecurityModel(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserSecurityModel(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public me.unc.ldms.dto.User getUser() {
        return user;
    }

    public void setUser(me.unc.ldms.dto.User user) {
        this.user = user;
    }

    public UserMain getUserMain() {
        return userMain;
    }

    public void setUserMain(UserMain userMain) {
        this.userMain = userMain;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*@Override
    public boolean equals(Object rhs) {
        return this.toString().equals(rhs.toString());
    }

    @Override
    public int hashCode() {
        return user.getUid().hashCode();
    }

    @Override
    public String toString() {
        return user.getUid();
    }*/
}
