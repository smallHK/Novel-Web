package com.hk.web;

import com.alibaba.fastjson.JSONObject;
import com.hk.repository.CreatorRepository;
import com.hk.entity.Creator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * smallHK
 * 2019/3/25 9:30
 */
@Controller
@RequestMapping("/creator")
public class UserManager {

    /**
     * 注册编辑
     */
    public JSONObject registerEditor() {
        return null;
    }


    CreatorRepository creatorRepository;

    public UserManager(CreatorRepository creatorRepository) {
        this.creatorRepository  = creatorRepository;
    }



    /**
     * 注册作者
     * 注册成功，返回注册成功页面
     * 注册失败，返回注册失败页面
     * @return 注册是否成功，返回新的页面
     */
    @PostMapping(path = "/register")
    public @ResponseBody  JSONObject registerCreator(@RequestParam Map<String, String> params) {

        String penName = params.get("penname");
        String password = params.get("password");
        Creator creator = new Creator();
        creator.setPenName(penName);
        creator.setPassword(password);
        try{
            creatorRepository.save(creator);
        } catch (Exception e) {
            return new JSONObject().fluentPut("msg", "failure!").fluentPut("status", "1");
        }
        return new JSONObject().fluentPut("msg", "success!").fluentPut("status", "0");
    }

    /**
     * 检验笔名是否已经存在
     * @return 0表示不存在相同笔名，1表示存在相同笔名
     */
    @GetMapping(path = "checkPenNameExist")
    public @ResponseBody  JSONObject existPenName(@RequestParam("penname") String penName) {

        Integer count = creatorRepository.countCreatorByPenName(penName);

        JSONObject result = new JSONObject();
        if(count > 0) {
            result.fluentPut("msg", "pen name exist!");
            result.fluentPut("status", "1");
        }else if(count == 0){
            result.fluentPut("msg", "ok!");
            result.fluentPut("status", "0");
        }

        return result;
    }




    public JSONObject registerReader() {

        return null;
    }


}
