package me.unc.ldms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.utils.StateType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description 认证失败处理类
 * @Date 2020/3/9 15:59
 * @author LZS
 * @version v1.0
 */
@Component
public class AuthenticationFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Result result = ResultBuilder.failResult("账号密码错误！");
        httpServletResponse.setStatus(StateType.FORBIDDEN.getCode());
        ObjectMapper om = new ObjectMapper();
        PrintWriter out = httpServletResponse.getWriter();
        out.write(om.writeValueAsString(result));
        out.flush();
        out.close();
    }
}
