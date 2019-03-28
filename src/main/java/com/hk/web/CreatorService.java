package com.hk.web;

import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.entity.Novel;
import com.hk.entity.Volume;
import com.hk.repository.ChapterRepository;
import com.hk.repository.CreatorRepository;
import com.hk.repository.NovelRepository;
import com.hk.repository.VolumeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * smallHK
 * 2019/3/26 16:55
 * <p>
 * 为作者提供业务服务
 */
@Controller
@RequestMapping(path = "/creator")
public class CreatorService {


    private CreatorRepository creatorRepository;

    private NovelRepository novelRepository;

    private VolumeRepository volumeRepository;

    private ChapterRepository chapterRepository;

    public CreatorService(CreatorRepository creatorRepository, NovelRepository novelRepository,
                          VolumeRepository volumeRepository, ChapterRepository chapterRepository) {
        this.creatorRepository = creatorRepository;
        this.novelRepository = novelRepository;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
    }

    /**
     * 登陆功能
     * 登陆后并不会进行页面跳转，所以返回json
     * 已登录状态，不会具有登陆页面
     *
     * @param params 前台传入的参数
     * @return 返回对登陆状态的判断
     */
    @PostMapping(path = "/login")
    public @ResponseBody
    JSONObject login(@RequestParam Map<String, String> params, HttpSession session) {

        Iterable<Creator> creatorList = creatorRepository.findAll();
        String penname = params.get("penname");
        String password = params.get("password");
        for (Creator creator : creatorList) {
            if (creator.getPenName().equals(penname) && creator.getPassword().equals(password)) {
                session.setAttribute("login_penname", penname);
                session.setAttribute("login_creator_id", creator.getId());
                return new JSONObject().fluentPut("msg", "success!").fluentPut("status", "0");
            }
        }

        return new JSONObject().fluentPut("msg", "fail!").fluentPut("status", "1");
    }


    /**
     * 添加图书
     * @param params
     * @return
     */
    @PostMapping(path = "/addNovel")
    public @ResponseBody
    JSONObject createNovel(@RequestParam Map<String, String> params, HttpSession session) {

        Integer creator_id = (Integer) session.getAttribute("login_creator_id");
        String novelName = params.get("novel_title");
        String briefIntro = params.get("brief_intro");
        Novel novel = new Novel();
        novel.setAuthorId(creator_id);
        novel.setBriefIntro(briefIntro);
        novel.setNovelName(novelName);
        novel.setStatus(0);

        try {
            novelRepository.save(novel);
            return new JSONObject().fluentPut("status", 0).fluentPut("msg", "success!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject().fluentPut("status", 1).fluentPut("msg", "fail!");
    }


    /**
     * 显示作者的所有书籍
     * @return 成功返回小说列表
     */
    @GetMapping(path = "/listAllNovel")
    public @ResponseBody
    JSONObject  listAllNovelByCreatorId(HttpSession session) {

        Integer creator_id = (Integer) session.getAttribute("login_creator_id");

        try {
            List<Novel> novelList = novelRepository.findAllByAuthorId(creator_id);
            if(novelList == null) novelList = new ArrayList<>();
            return new JSONObject().fluentPut("status", 0)
                    .fluentPut("msg", "success!")
                    .fluentPut("novelList", novelList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject().fluentPut("status", 1)
                .fluentPut("msg", "fail!");
    }


    /**
     * 创建新卷
     *
     * @return
     */
    @PostMapping(path = "/createVolume")
    public @ResponseBody
    JSONObject createVolume(@RequestParam Map<String, String> params) {

        Integer novelId = Integer.valueOf(params.get("novel_id"));
        String volumeTitle = params.get("volume_title");

        Volume volume = new Volume();
        volume.setNovelId(novelId);
        volume.setVolumeTitle(volumeTitle);

        Integer orderNum = volumeRepository.countAllByNovelId(novelId) + 1;
        volume.setOrderNum(orderNum);

        try {
            volumeRepository.save(volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回所有卷信息
     *
     * @return
     */
    public @ResponseBody
    JSONObject listVolumesByNovelId(@RequestParam("novel_id") Integer novelId) {
        Iterable<Volume> volumeIterable = null;
        try {
            volumeIterable = volumeRepository.findAllByNovelId(novelId);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().fluentPut("msg", "fail!")
                    .fluentPut("status", 1);
        }
        List<Volume> volumeList = new ArrayList<>();
        for (Volume volume : volumeIterable) {
            volumeList.add(volume);
        }
        return new JSONObject().fluentPut("msg", "success!")
                .fluentPut("status", 0)
                .fluentPut("volumes", volumeList);
    }


    /**
     * 创建新的章节
     *
     * @return
     */
    @PostMapping(path = "/createChapter")

    public @ResponseBody
    JSONObject addNewChapter(@RequestParam Map<String, String> params) {

        return null;
    }


    /**
     * 返回所有章节信息
     */
    public @ResponseBody
    JSONObject listAllChapterByNovelId(@RequestParam Map<String, String> params) {

        return null;
    }


    /**
     * 申请小说发布
     */
    public @ResponseBody
    JSONObject applyForPublish() {

        return null;
    }


}
