package me.unc.ldms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 应用控制层
 * @Date 2020/2/19 15:46
 * @author LZS
 * @version v1.0
 */
@Slf4j
@Controller
public class AppController {

    /*@RequestMapping("/")
    public String index() {
        return login();
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("user", getUsername());
        model.addAttribute("role", getAuthority());
        return "admin";
    }

    @RequestMapping("/keeper")
    public String keeper(Model model) {
        model.addAttribute("user", getUsername());
        model.addAttribute("role", getAuthority());
        return "keeper";
    }

    @RequestMapping("/checker")
    public String checker(Model model) {
        model.addAttribute("user", getUsername());
        model.addAttribute("role", getAuthority());
        return "checker";
    }

    @RequestMapping("/courier")
    public String courier(Model model) {
        model.addAttribute("user", getUsername());
        model.addAttribute("role", getAuthority());
        return "courier";
    }

    @RequestMapping("/home")
    public String normal(Model model) {
        model.addAttribute("user", getUsername());
        model.addAttribute("role", getAuthority());
        return "home";
    }

    @RequestMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        //Authentication是一个接口，用来表示用户认账信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //如果用户认证信息不为空，注销
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response,auth);
        }
        //重定向到login页面
        return "redirect:/login?logout";
    }

    *//**
     * 获取当前用户名称
     * @return
     *//*
    private String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //System.out.println("username = " + username);
        log.info("Authentication username = " + username);
        return username;
    }

    *//**
     * 获取当前用户权限
     * @return
     *//*
    private String getAuthority() {
        //获得Authentication对象，表示用户认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = new ArrayList<>();
        //将角色名称添加到List集合
        for (GrantedAuthority a : authentication.getAuthorities()) {
            roles.add(a.getAuthority());
        }
        //System.out.println("role = " + roles);
        log.info("Authentication role = " + roles);
        return roles.toString();
    }*/

}
