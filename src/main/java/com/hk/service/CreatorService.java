package com.hk.service;

import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.repository.CreatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author smallHK
 * 2019/5/9 16:42
 */
@Service
public class CreatorService {


    private CreatorRepository creatorRepository;

    public CreatorService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    /**
     * 注册作者
     * 注册成功，返回注册成功页面
     * 注册失败，返回注册失败页面
     */
    public void registerCreator(String penName, String password) {
        Creator creator = new Creator();
        creator.setPenName(penName);
        creator.setPassword(password);
        creatorRepository.save(creator);

    }

    /**
     * 检验笔名是否已经存在
     *
     * true，笔名已存在
     */
    @GetMapping(path = "checkPenNameExist")
    public boolean existPenName(@RequestParam("penname") String penName) {
        return creatorRepository.countCreatorByPenName(penName) > 0;
    }

}
