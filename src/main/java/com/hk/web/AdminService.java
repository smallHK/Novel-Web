package com.hk.web;

import com.alibaba.fastjson.JSONObject;
import com.hk.repository.AdminRepository;
import com.hk.entity.Admin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * smallHK
 * 2019/3/19 21:03
 *
 * 提供管理员服务
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminService {


    private AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * 根据输入的帐户信息
     * 返回判断结果
     *
     * @param username 用户名
     * @param pwd 密码
     * @return status=0登陆成功，status=1登陆失败
     */
    @GetMapping(path="/login")
    public JSONObject login(@RequestParam("username") String username, @RequestParam("password") String pwd) {


        JSONObject result = new JSONObject();

        Iterable<Admin> adminList = adminRepository.findAll();

        for (Admin admin :adminList) {
            if(admin.getUsername().equals(username) && admin.getPassword().equals(pwd)){
                return result.fluentPut("status", 0);
            }
        }

        return result.fluentPut("status", 1);

    }


}
