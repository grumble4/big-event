package com.wxt.service.impl;

import com.wxt.mapper.UserMapper;
import com.wxt.pojo.User;
import com.wxt.service.UserService;
import com.wxt.utils.Md5Util;
import com.wxt.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUserName(String username) {
        User u=userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
//        加密处理

        String md5String=Md5Util.getMD5String(password);
        //        添加
        userMapper.add(username,md5String);
    }

//    ctrl+O
    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
//        报红快捷键alt+enter
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer id=(Integer) map.get("id");
        userMapper.updateAvar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer id=(Integer) map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);
    }
}
