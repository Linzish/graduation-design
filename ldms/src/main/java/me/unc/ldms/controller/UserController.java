package me.unc.ldms.controller;

import me.unc.ldms.dto.User;
import me.unc.ldms.response.Result;
import me.unc.ldms.response.ResultBuilder;
import me.unc.ldms.service.UserService;
import me.unc.ldms.utils.AppConstant;
import me.unc.ldms.utils.SnowflakeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @Description 用户服务控制层
 * @Date 2020/2/10 18:33
 * @author LZS
 * @version v1.0
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(String username, String password, String phone, String wid) {
        User user = new User();
        //1.uid生成策略 （把uuid策略换成雪花算法）
        //user.setUid(AppConstant.USER_REGISTER_PREFIX + UUID.randomUUID().toString().replace("-", ""));
        user.setUid(AppConstant.USER_REGISTER_PREFIX + SnowflakeUtils.genId());
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setTime(new Date());
        //2.保存数据库
        //3.返回信息
        if (wid != null) {
            try {
                userService.addUserWithWid(user, wid);
                return ResultBuilder.successResultOnly("注册成功");
            } catch (Exception e) {
                return ResultBuilder.failResult("注册失败");
            }
        } else {
            try {
                userService.addUser(user);
                return ResultBuilder.successResultOnly("注册成功");
            } catch (Exception e) {
                return ResultBuilder.failResult("注册失败");
            }
        }
    }

}
