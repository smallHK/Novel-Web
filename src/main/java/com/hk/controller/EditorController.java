package com.hk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Editor;
import com.hk.entity.Novel;
import com.hk.entity.NovelPublish;
import com.hk.entity.Profile;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelIndex;
import com.hk.repository.EditorRepository;
import com.hk.repository.NovelPublishRepo;
import com.hk.repository.NovelRepository;
import com.hk.repository.ProfileRepository;
import com.hk.service.NovelService;
import com.hk.util.EntityStatus;
import com.hk.util.EntityUtil;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hk.util.ResultUtil.success;

/**
 * smallHK
 * 2019/4/3 20:34
 */
@Controller
@RequestMapping(path = "/editor")
@EnableTransactionManagement
public class EditorController {

    private NovelService novelService;

    private ProfileRepository profileRepository;

    private EditorRepository editorRepository;

    private NovelRepository novelRepository;

    private NovelPublishRepo novelPublishRepo;

    public EditorController(NovelService novelService, ProfileRepository profileRepository,
                            EditorRepository editorRepository, NovelRepository novelRepository, NovelPublishRepo novelPublishRepo) {
        this.profileRepository = profileRepository;
        this.editorRepository = editorRepository;
        this.novelRepository = novelRepository;
        this.novelPublishRepo = novelPublishRepo;

        this.novelService = novelService;

    }


    /**
     * 申请称为编辑，投简历
     */
    @Deprecated
    @PostMapping("/postProfile")
    public ModelAndView postProfile(@RequestParam Map<String, String> params) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/result");
        String realName = params.get("real_name");
        String phoneNum = params.get("phone_number");
        String idNumber = params.get("id_number");
        String eMail = params.get("e_mail");
        String briefIntro = params.get("brief_intro");


        Profile profile = new Profile();
        profile.setRealName(realName);
        profile.setIdNumber(idNumber);
        profile.setEMail(eMail);
        profile.setPhoneNumber(phoneNum);
        profile.setBriefIntro(briefIntro);

        try {
            profileRepository.save(profile);
            modelAndView.addObject("resultInfo", ResultUtil.success("申请已发出").toJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("resultInfo", ResultUtil.failure("申请发送失败").toJSONObject());
        }

        return modelAndView;
    }


    /**
     * 编辑登陆
     */
    @Deprecated
    @PostMapping("/login")
    public ModelAndView loginEditor(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/editor/workSpacePage");

        String editorName = params.get("editor_name");
        String password = params.get("password");

        try {
            Iterable<Editor> editorIterable = editorRepository.findAll();

            for (Editor editor : editorIterable) {
                if (editor.getEditorName().equals(editorName) && editor.getPassword().equals(password)) {
                    session.setAttribute("login_editor_name", editorName);
                    session.setAttribute("login_editor_id", editor.getId());
                    modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
                    return modelAndView;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("resultInfo", ResultUtil.failure("failure!").toJSONObject());
            modelAndView.setViewName("/result");

        }
        return modelAndView;
    }


    /**
     * 修改个人信息
     */
    @Deprecated
    @PostMapping("/updatePersonalInfo")
    public ModelAndView updatePersonalInfo(@RequestParam Map<String, String> params, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer loginId = (Integer) session.getAttribute("login_editor_id");


        String editorName = params.get("editor_name");
        String password = params.get("password");

        try {
            Optional<Editor> editorWrapper = editorRepository.findById(loginId);
            if (!editorWrapper.isPresent()) {
                throw new RuntimeException("用户不存在！");
            }
            Editor editor = editorWrapper.get();
            editor.setPassword(password);
            editor.setEditorName(editorName);
            editorRepository.save(editor);

            session.setAttribute("login_editor_name", editorName);
            modelAndView.setViewName("/editor/userCenter");
            modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("failure!").toJSONObject());
        }

        return modelAndView;
    }

    /**
     * 查看所有申请小说
     */
    @GetMapping("/checkingNovelList/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listAllNoPublishedNovel(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {


        Integer offset = (pageNum - 1) * pageSize;

        List<Novel> novelList = novelService.findLimSpePubStaNovelList(EntityStatus.NOVEL_CHECKING, offset, pageSize);
        Integer totalNum = novelService.countSpePubStaNovel(EntityStatus.NOVEL_CHECKING);

        return ResultUtil.success("加载成功").toJSONObject()
                .fluentPut("novelList", novelList)
                .fluentPut("totalNum", totalNum);
    }


    /**
     * 审批小说
     */
    @GetMapping("/publishNovel/{novelId}")
    public ModelAndView publishNovel(@PathVariable Integer novelId, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        Integer editorId = (Integer) session.getAttribute("login_editor_id");
        novelService.publishNovel(novelId, editorId);
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }


    /**
     * 查看登陆编辑审核管理的书籍
     */
    @GetMapping("/findAllManagedNovel")
    public @ResponseBody JSONObject
    findAllPublishedNovelByEditor(HttpSession session) {
        Integer editorId = (Integer) session.getAttribute("login_editor_id");
        List<Novel> novelList = novelService.listAllNovelByEditorId(editorId);
        return ResultUtil.success("success!").toJSONObject()
                .fluentPut("novelList", novelList);
    }


    /**
     * 查看指定书籍目录
     */
    @GetMapping("/showSpecialNovelIndex/{novelId}")
    public ModelAndView findSpecialNovelIndex(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        NovelIndex index = novelService.findNovelIndex(novelId);
        modelAndView.setViewName("/editor/novelIndexPage");
        modelAndView.addObject("indexInfo", index);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }

    /**
     * 查看书籍指定章节内容
     */
    @GetMapping("/showSpecialChapterContent/{chapterId}")
    public ModelAndView findSpecialNovelContent(@PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/editor/contentPage");
        ChapterInfo chapterInfo = novelService.findNovelChapter(chapterId);
        modelAndView.addObject("chapterInfo", chapterInfo);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }


}
