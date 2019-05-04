package com.hk.controller;

import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.entity.Novel;
import com.hk.entity.Volume;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelIndex;
import com.hk.po.VolumeInfo;
import com.hk.repository.CreatorRepository;
import com.hk.service.NovelService;
import com.hk.util.EntityStatus;
import com.hk.util.ResultUtil;
import com.hk.util.SessionProperty;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.hk.util.ResultUtil.success;

/**
 * smallHK
 * 2019/3/26 16:55
 * <p>
 * 为作者提供业务服务
 */
@Controller
@RequestMapping(path = "/creator")
public class CreatorController {

    private NovelService novelService;

    private CreatorRepository creatorRepository;


    public CreatorController(CreatorRepository creatorRepository,
                             NovelService novelService) {
        this.creatorRepository = creatorRepository;
        this.novelService = novelService;
    }


    /**
     * 注册作者
     * 注册成功，返回注册成功页面
     * 注册失败，返回注册失败页面
     *
     * @return 注册是否成功，返回新的页面
     */
    @PostMapping(path = "/register")
    public @ResponseBody
    JSONObject registerCreator(@RequestParam Map<String, String> params) {

        String penName = params.get("penname");
        String password = params.get("password");
        Creator creator = new Creator();
        creator.setPenName(penName);
        creator.setPassword(password);
        try {
            creatorRepository.save(creator);
        } catch (Exception e) {
            return new JSONObject().fluentPut("msg", "failure!").fluentPut("status", "1");
        }
        return new JSONObject().fluentPut("msg", "success!").fluentPut("status", "0");
    }

    /**
     * 检验笔名是否已经存在
     *
     * @return 0表示不存在相同笔名，1表示存在相同笔名
     */
    @GetMapping(path = "checkPenNameExist")
    public @ResponseBody
    JSONObject existPenName(@RequestParam("penname") String penName) {

        Integer count = creatorRepository.countCreatorByPenName(penName);

        JSONObject result = new JSONObject();
        if (count > 0) {
            result.fluentPut("msg", "pen name exist!");
            result.fluentPut("status", "1");
        } else if (count == 0) {
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
    public ModelAndView login(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        try {
            Iterable<Creator> creatorList = creatorRepository.findAll();
            String penname = params.get("penname");
            String password = params.get("password");
            for (Creator creator : creatorList) {
                if (creator.getPenName().equals(penname) && creator.getPassword().equals(password)) {
                    session.setAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_NAME, penname);
                    session.setAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID, creator.getId());
                    modelAndView.setViewName("redirect:/creator/novelManagePage");
                    modelAndView.addObject("resultInfo", success("读者登陆成功！"));
                    return modelAndView;
                }
            }
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("读者登陆失败！"));
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();

            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure(e.getMessage()));
            return modelAndView;

        }

    }

    /**
     * 添加图书
     * 完成书籍添加，跳转到小说管理页面
     */
    @PostMapping(path = "/addNovel")
    public ModelAndView createNovel(@RequestParam Map<String, String> params, @RequestParam Map<String, MultipartFile> fileData, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        try {
            Integer creator_id = (Integer) session.getAttribute("login_creator_id");
            String novelName = params.get("novel_title");
            String briefIntro = params.get("brief_intro");

            String coverFileName = fileData.get("coverCoverImg").getOriginalFilename();
            byte[] coverData = fileData.get("coverCoverImg").getBytes();

            novelService.addNewNovel(creator_id, novelName, briefIntro, coverFileName, coverData);
            modelAndView.addObject("resultInfo", success("添加成功！"));
            modelAndView.setViewName("redirect:/creator/novelManagePage");
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("resultInfo", ResultUtil.failure(e.getMessage()));
            modelAndView.setViewName("/result");
            return modelAndView;
        }
    }


    /**
     * 显示登陆作者指定页的所有创作书籍
     *
     * @return 成功返回小说列表
     */
    @GetMapping("/createdNovelList/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listAllCreatedNovelByCreatorId(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        Integer offset = (pageNum - 1) * pageSize;
        List<Novel> novelList = novelService.findSpeicalCreatorLimitedCreatedNovelList(creatorId, offset, pageSize);
        Integer totalNum = novelService.countSpecialCreatorAllNovelList(creatorId);
        return ResultUtil.success("加载成功").toJSONObject()
                .fluentPut("novelList", novelList)
                .fluentPut("totalNum", totalNum);
    }

    /**
     * 获取作者指定页的所有未开放小说
     */
    @GetMapping("/unpublishedNovelList/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listAllUnpublishedNovelByCreatorId(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        Integer offset = (pageNum - 1) * pageSize;

        List<Novel> novelList = novelService.findSpeCreatorLimSpePubStaNovelList(creatorId, EntityStatus.NOVLE_CREATED, offset, pageSize);
        Integer totalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVLE_CREATED);
        return ResultUtil.success("加载成功").toJSONObject()
                .fluentPut("novelList", novelList)
                .fluentPut("totalNum", totalNum);
    }

    /**
     * 获取作者所有指定页的审核中小说
     */
    @GetMapping("/checkingNovelList/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listAllCheckingNovelByCreatorId(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        Integer offset = (pageNum - 1) * pageSize;

        List<Novel> novelList = novelService.findSpeCreatorLimSpePubStaNovelList(creatorId, EntityStatus.NOVEL_CHECKING, offset, pageSize);
        Integer totalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVEL_CHECKING);
        return ResultUtil.success("加载成功").toJSONObject()
                .fluentPut("novelList", novelList)
                .fluentPut("totalNum", totalNum);
    }

    /**
     * 获取作者所有指定页的已开放小说
     */
    @GetMapping("/publishedNovelList/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listAllPublishedNovelByCreatorId(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        Integer offset = (pageNum - 1) * pageSize;

        List<Novel> novelList = novelService.findSpeCreatorLimSpePubStaNovelList(creatorId, EntityStatus.NOVEL_PASSED, offset, pageSize);
        Integer totalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVEL_PASSED);
        return ResultUtil.success("加载成功").toJSONObject()
                .fluentPut("novelList", novelList)
                .fluentPut("totalNum", totalNum);
    }


    /**
     * 进入小说目录编辑管理页面
     */
    @GetMapping("/novelIndexManage/{novelId}")
    public ModelAndView novelIndexManage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        //页面处理加载
        modelAndView.setViewName("/creator/novelIndexManagePage");
        NovelIndex novelIndex = novelService.findNovelIndex(novelId);
        modelAndView.addObject("novelIndexInfo", novelIndex);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }


    /**
     * 小说目录
     * 返回一个小说所有的章节以及相关卷名
     * 在小说没有章节与卷的情况下，返回一个空list对象
     */
    @GetMapping("/findNovelIndex/{novel_id}")
    public ModelAndView findNovelIndex(@PathVariable(name = "novel_id") Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/findNovelIndex");

        NovelIndex novelIndex = novelService.findNovelIndex(novelId);
        modelAndView.addObject("novelIndexInfo", novelIndex);

        modelAndView.addObject("status", 0);
        modelAndView.addObject("msg", "success!");

        return modelAndView;
    }


    /**
     * 创建新卷
     **/
    @PostMapping(path = "/addNewVolume")
    public ModelAndView createVolume(@RequestParam(name = "novel-id") Integer novelId, @RequestParam(name = "volume-name") String volumeTitle) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/creator/chapterManage/" + novelId);
        novelService.addNewVolume(novelId, volumeTitle);
        return modelAndView;
    }


    /**
     * 创建新的章节
     */
    @PostMapping(path = "/addNewChapter")
    @Transactional
    public ModelAndView addNewChapter(@RequestParam Map<String, String> params) {

        ModelAndView modelAndView = new ModelAndView();

        Integer novelId = Integer.valueOf(params.get("novel_id"));
        Integer volumeId = Integer.valueOf(params.get("volume_id"));
        String chapterTitle = params.get("chapter_title");

        String originChapterContent = params.get("chapter_content");
        //文本处理
        novelService.addNewChapter(novelId, volumeId, chapterTitle, originChapterContent);

        modelAndView.setViewName("redirect:/creator/chapterManage/" + novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("创建成功！"));

        return modelAndView;
    }


    /**
     * 跳转添加新章节页面
     **/
    @GetMapping(path = "/enterAddChapterPage/{novelId}/{volumeId}")
    public ModelAndView addNewChapter(@PathVariable("novelId") Integer novelId, @PathVariable("volumeId") Integer volumeId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/addNewChapterPage");
        modelAndView.addObject("novelId", novelId);
        modelAndView.addObject("volumeId", volumeId);
        return modelAndView;
    }


    /**
     * 获取章节内容
     **/
    @GetMapping(path = "/findChapterContent/{chapterId}")
    public ModelAndView findChapterContent(@PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/contentPage");

        ChapterInfo chapterInfo = novelService.findNovelChapter(chapterId);
        modelAndView.addObject("chapterInfo", chapterInfo);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }


    /**
     * 申请小说发布
     */
    @GetMapping("/applyForPublish/{novelId}")
    public ModelAndView applyForPublish(@PathVariable Integer novelId) {

        ModelAndView modelAndView = new ModelAndView();
        novelService.updateNovelPublishedStatus(novelId, EntityStatus.NOVEL_CHECKING);
        modelAndView.setViewName("redirect:/creator/novelManagePage");
        modelAndView.addObject("resultInfo", success("success!").toJSONObject());

        return modelAndView;
    }

    /**
     * 申请章节开发
     */
    @GetMapping("/applyForChapterPublish/{chapterId}")
    public ModelAndView applyForChapterPublish(@PathVariable Integer chapterId, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        Integer authorId = (Integer)session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        novelService.publishChapterPublishEvent(chapterId, authorId);
        novelService.updateChapterPublishStatus(chapterId, EntityStatus.CHAPTER_CHECKING);

        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }


    /**
     * 章节管理页面填充
     */
    @GetMapping("/chapterManage/{novelId}")
    public ModelAndView chapterManagePage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        NovelIndex index = novelService.findNovelIndex(novelId);

        modelAndView.setViewName("/creator/chapterManagePage");

        modelAndView.addObject("index", index);

        return modelAndView;
    }

    /**
     * 获取指定章节的信息（ajax请求）
     */
    @GetMapping("/rest/chapterInfo/{chapterId}")
    public @ResponseBody JSONObject findChapterInfoByRest(@PathVariable Integer chapterId) {
        ChapterInfo info = novelService.findNovelChapter(chapterId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("chapterInfo", info);

    }

    /**
     * 获取指定卷信息（ajax请求）
     */
    @GetMapping("/rest/volumeInfo/{volumeId}")
    public @ResponseBody JSONObject findVolumeInfoByRest(@PathVariable Integer volumeId) {
        VolumeInfo info = novelService.findVolumeInfo(volumeId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("volumeInfo", info);

    }

    /**
     * 获取制定小说目录信息（ajax请求）
     */
    @GetMapping("/rest/novelIndex/{novelId}")
    public @ResponseBody JSONObject findNovelIndexByRest(@PathVariable Integer novelId) {
        NovelIndex index = novelService.findNovelIndex(novelId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("novelIndex", index);

    }


}
