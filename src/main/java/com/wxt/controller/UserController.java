package com.wxt.controller;

import com.wxt.pojo.Result;
import com.wxt.pojo.User;
import com.wxt.service.UserService;
import com.wxt.utils.JwtUtil;
import com.wxt.utils.Md5Util;
import com.wxt.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username,@Pattern(regexp = "^\\S{5,16}$") String password){

            //        查询用户
            User u= userService.findByUserName(username);
            if(u==null){
//        没有占用
//        注册用户
                userService.register(username,password);
                return Result.success();
            }else {
//            占用
                return Result.error("用户名已被占用");
        }
    }
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username,@Pattern(regexp = "^\\S{5,16}$") String password){
//        根据用户名查询用户
        User loginUser=userService.findByUserName(username);
//        判断用户是否存在
        if (loginUser==null){
            return Result.error("用户名错误");
        }
//        密码是否正确   loginUser对象中的password是密文
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
//            登录成功
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            String token=JwtUtil.genToken(claims);
//            把Token存储到Redis中
            ValueOperations<String,String> operations=stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name = "Authorization") String token*/){
//        根据用户名查询用户
        /*Map<String,Object> map=JwtUtil.parseToken(token);
        String username=(String) map.get("username");*/
        Map<String,Object> map= ThreadLocalUtil.get();
        String username=(String) map.get("username");
        User user= userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){

        userService.update(user);
        return Result.success();
    }


    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
//        校验参数，validate不能满足
        String oldPwd= params.get("old_pwd");
        String newPwd= params.get("new_pwd");
        String rePwd= params.get("re_pwd");

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }
//        原密码是否正确
//        调用userService根据用户名拿到原密码，再和old_pwd比对
        Map<String,Object> map=ThreadLocalUtil.get();
        String username=(String) map.get("username");
        User loginUser= userService.findByUserName(username);
        if(! loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码不正确");
        }
//        newPwd和rePwd是否一致
        if (! rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }
//        调用service完成密码更新
        userService.updatePwd(newPwd);
//        解决旧密码对应token令牌下修改密码不重新登录再用旧令牌仍可查看信息
//        删除Redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }
}
