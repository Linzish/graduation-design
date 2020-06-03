package me.unc.ldms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @Description Spring Security配置
 * @Date 2020/2/18 11:33
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //用于包装用户信息
    @Autowired
    private AuthenticationService authenticationService;
    //注入加密接口
    @Autowired
    private PasswordEncoder passwordEncoder;
    //注入用户认证接口
    @Autowired
    private AuthenticationProvider authenticationProvider;
    //注入认证处理成功类，验证用户成功后处理不同用户跳转不同的页面
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailHandler authenticationFailHandler;
    //权限不足处理类
    @Autowired
    private AuthenticationAccessDeniedHandler accessDeniedHandler;
    //等出处理
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        //创建DaoAuthenticationProvider对象
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //不要隐藏“用户未找到”异常
        provider.setHideUserNotFoundExceptions(false);
        //传入在authenticationService中自定义的认证方式
        //Spring Security会通过loadUserByUsername方法获取对应UserDetail进行认证
        provider.setUserDetailsService(authenticationService);
        //设置密码加密程序认证
        //Spring Security会自动将密码加密后与数据库比对。
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    /**
     * 设置认证方式
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置认证方式
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * 设置了登录页面，而且登录页面任何人都可以访问，然后设置了登录失败地址，也设置了注销请求
     * permitAll()表示该请求任何人都可以访问,
     * .anyRequest().authenticated()表示其他的请求都必须要有权限认证
     * @param http http信息
     * @throws Exception
     */
    @Override
    //TODO -权限拦截待完善
    protected void configure(HttpSecurity http) throws Exception {
        //System.out.println("AppSecurityConfigurer configure() 调用.......");
        log.info("SecurityConfig [configure] start");
        http.authorizeRequests()
                //spring-security 5.0 后需要过滤静态资源
                .antMatchers("/login", "/logout", "/css/**", "/js/**", "/img/*", "/fonts/**", "/index").permitAll()
//                .antMatchers("/home", "/").hasRole("USER")
//                .antMatchers("/**").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ROOT")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailHandler)
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessHandler(logoutHandler)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                .csrf().disable();
        /*http.sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login")
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry);*/
    }

}
