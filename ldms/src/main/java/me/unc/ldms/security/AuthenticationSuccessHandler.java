package me.unc.ldms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.unc.ldms.dto.UserMain;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.utils.StateType;
import me.unc.ldms.vo.LoginMsg;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author LZS
 * @version v1.0
 * @Description 权限认证操作类
 * @Date 2020/2/19 16:15
 */
@Slf4j
@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //spring-security通过RedirectStrategy对象复杂所有重定向事务
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    /**
     * 重写handle()方法，方法中通过RedirectStrategy对象重定向到指定的URL
     * 权限控制核心
     * @param request        http请求
     * @param response       http应答
     * @param authentication 认证对象
     * @throws IOException
     */
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        //通过determineTargetUrl方法返回需要跳转的Url
        String targetUrl = determineTargetUrl(authentication);
        //重定向请求到指定的URL
        //redirectStrategy.sendRedirect(request, response, targetUrl);

        //拓展
        UserSecurityModel userModel = (UserSecurityModel) authentication.getPrincipal();
        me.unc.ldms.dto.User user = userModel.getUser();
        UserMain userMain = userModel.getUserMain();

        LoginMsg loginMsg = new LoginMsg(userModel.getUsername(), targetUrl);
        loginMsg.setWid(userMain.getWid());
        loginMsg.setUsername(user.getUsername());
        loginMsg.setPhone(user.getPhone());

        Result result = ResultBuilder.buildResult(StateType.OK, "登录成功", loginMsg);
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = response.getWriter();
        out.write(om.writeValueAsString(result));
        out.flush();
        out.close();

        log.info("Authentication success redirect");
    }

    /**
     * 从Authentication对象中提取当前登录用户的角色，并根据器角色返回适当的URl
     *
     * @param authentication 认证对象
     * @return 角色对应跳转的url
     */
    protected String determineTargetUrl(Authentication authentication) {
        String url = "";

        //获取当前登录用户的角色权限集合
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        //角色集合
        List<String> roles = new ArrayList<>();

        //将角色名称添加到List集合
        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        //判断不同角色跳转到不同的Url
        if (isAdmin(roles)) {
            url = "/admin";
        } else if (isKeeper(roles)) {
            url = "/keeper";
        } else if (isChecker(roles)) {
            url = "/checker";
        } else if (isCourier(roles)) {
            url = "/courier";
        } else if (isUser(roles)) {
            url = "/customer";
        } else {
            url = "/accessDenied";
        }

        //System.out.println("url = " + url);
        log.info("Authentication url = " + url);
        return url;
    }

    //判断身份
    private boolean isUser(List<String> roles) {
        return roles.contains("ROLE_NORMAL");   //普通用户
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ROOT");     //root
    }

    private boolean isKeeper(List<String> roles) {
        return roles.contains("ROLE_KEEPER");   //仓管
    }

    private boolean isChecker(List<String> roles) {
        return roles.contains("ROLE_CHECKER");  //采购，审核
    }

    private boolean isCourier(List<String> roles) {
        return roles.contains("ROLE_COURIER");    //配送
    }

}
