package com.hk.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.*;
import com.hk.repository.*;
import com.hk.vo.VolumeInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    private ParagraphRepository paragraphRepository;

    public CreatorService(CreatorRepository creatorRepository, NovelRepository novelRepository,
                          VolumeRepository volumeRepository, ChapterRepository chapterRepository,
                          ParagraphRepository paragraphRepository) {
        this.creatorRepository = creatorRepository;
        this.novelRepository = novelRepository;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
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
     * 显示作者的所有创作的书籍
     * @return 成功返回小说列表
     */
    @GetMapping(path = "/listAllNovel")
    public ModelAndView listAllNovelByCreatorId(HttpSession session) {

        Integer creator_id = (Integer) session.getAttribute("login_creator_id");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/listNovel");
        try {
            List<Novel> novelList = novelRepository.findAllByAuthorId(creator_id);
            if(novelList == null) novelList = new ArrayList<>();
            JSONArray novels = new JSONArray();
            novels.addAll(novelList);
            modelAndView.addObject("novelList", novelList);
            modelAndView.addObject("status", 0);
            modelAndView.addObject("msg", "success!");
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.addObject("status", 1);
        modelAndView.addObject("msg", "fail!");
        return modelAndView;
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
     * 小说目录
     * 返回一个小说所有的章节以及相关卷名
     * 在小说没有章节与卷的情况下，返回一个空list对象
     * @param novelId
     * @return
     */
    @GetMapping("/findNovelIndex/{novel_id}")
    public ModelAndView findNovelIndex(@PathVariable(name = "novel_id") Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/findNovelIndex");

        try {
            List<Volume> volumeList = volumeRepository.findAllByNovelId(novelId);
            List<Chapter>  chapterList = chapterRepository.findAllByNovelId(novelId);
            Novel novel = novelRepository.findNovelById(novelId);


            List<VolumeInfo> volumeInfoList = new ArrayList<>();

            for(Volume volume: volumeList) {
                VolumeInfo volumeInfo = new VolumeInfo();
                volumeInfo.setVolumeTitle(volume.getVolumeTitle());
                volumeInfo.setOrderNum(volume.getOrderNum());
                List<Chapter> voChapterList = new ArrayList<>();
                for(Chapter chapter: chapterList) {
                    if(chapter.getVolumeId().equals(volume.getId())) {
                        voChapterList.add(chapter);
                    }
                }
                volumeInfo.setChapterList(voChapterList);
                volumeInfoList.add(volumeInfo);
            }

            modelAndView.addObject("novelName", novel.getNovelName());
            modelAndView.addObject("volumeInfoList", volumeInfoList);
            modelAndView.addObject("status", 0);
            modelAndView.addObject("msg", "success!");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("status", 1);
            modelAndView.addObject("msg", "fail!");
        }

        return modelAndView;
    }


    /**
     * 获取章节内容
     * @param volumeId
     * @param chapterId
     */
    @GetMapping(path = "/findChapterContent/{novelId}/{volumeId}/{chapterId}")
    public ModelAndView findChapterContent(@PathVariable Integer volumeId, @PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("Content");
        try {
            List<Paragraph> paragraphList = paragraphRepository.findAllByChapterId(chapterId);
            modelAndView.addObject("paragraphList", paragraphList);
            modelAndView.addObject("msg", "success!");
            modelAndView.addObject("status", 0);
            return  modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("status", 1);
            modelAndView.addObject("msg", "fail!");
        }
        return modelAndView;
    }


    /**
     * 申请小说发布
     */
    public @ResponseBody
    JSONObject applyForPublish() {

        return null;
    }


}
