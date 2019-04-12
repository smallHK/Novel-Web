package com.hk.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Editor;
import com.hk.entity.Novel;
import com.hk.entity.NovelPublish;
import com.hk.entity.Profile;
import com.hk.repository.EditorRepository;
import com.hk.repository.NovelPublishRepo;
import com.hk.repository.NovelRepository;
import com.hk.repository.ProfileRepository;
import com.hk.util.EntityStatus;
import com.hk.util.EntityUtil;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * smallHK
 * 2019/4/3 20:34
 */
@Controller
@RequestMapping(path = "/editor")
@EnableTransactionManagement
public class EditorService {

    private ProfileRepository profileRepository;

    private EditorRepository editorRepository;

    private NovelRepository novelRepository;

    private NovelPublishRepo novelPublishRepo;

    public EditorService(ProfileRepository profileRepository,
                         EditorRepository editorRepository, NovelRepository novelRepository, NovelPublishRepo novelPublishRepo) {
        this.profileRepository = profileRepository;
        this.editorRepository = editorRepository;
        this.novelRepository = novelRepository;
        this.novelPublishRepo = novelPublishRepo;

    }


    /**
     * 申请称为编辑，投简历
     */
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
    @PostMapping("/login")
    public ModelAndView loginEditor(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/editor/userCenter");

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
     *
     * 状态为提交审核（1）
     */
    @GetMapping("/listAllNoPublishedNovel")
    public @ResponseBody JSONObject listAllNoPublishedNovel() {

        JSONObject result;
        try {
            List<Novel> novelList = novelRepository.findAllByStatus(EntityStatus.NOVEL_NO_PUBLISH);
            JSONArray novels = new JSONArray();
            for (Novel novel : novelList) {
                JSONObject novelJson = EntityUtil.objectToJsonObject(novel, Novel.class);
                novels.add(novelJson);
            }
            JSONObject novelInfo = new JSONObject();
            novelInfo.put("novels", novels);

            result = ResultUtil.success("success!").toJSONObject().fluentPut("novelInfo", novelInfo);

        } catch (Exception e) {
            e.printStackTrace();
            result = ResultUtil.failure("failure!").toJSONObject();
        }

        return result;
    }


    /**
     * 审批小说
     */
    @GetMapping("/publishNovel/{novelId}")
    @Transactional
    public ModelAndView publishNovel(@PathVariable Integer novelId, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        Integer editorId = (Integer) session.getAttribute("login_editor_id");

        try {
            NovelPublish novelPublish = new NovelPublish();
            novelPublish.setEditorId(editorId);
            novelPublish.setNovelId(novelId);
            novelPublishRepo.save(novelPublish);
            Novel novel = novelRepository.findNovelById(novelId);
            novel.setStatus(EntityStatus.NOVEL_PASSED);
            novelRepository.save(novel);
            modelAndView.setViewName("/editor/userCenter");
            modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("resultInfo", ResultUtil.failure("failure!").toJSONObject());
            modelAndView.setViewName("/result");
            return modelAndView;
        }
    }

    /**
     * 查看所有申请小说
     *
     * 状态为提交审核（1）
     */
    @GetMapping("/listAllPublishedNovel")
    public @ResponseBody JSONObject listAllPublishedNovel() {

        JSONObject result;
        try {
            List<Novel> novelList = novelRepository.findAllByStatus(EntityStatus.NOVEL_PASSED);
            JSONArray novels = new JSONArray();
            for (Novel novel : novelList) {
                JSONObject novelJson = EntityUtil.objectToJsonObject(novel, Novel.class);
                novels.add(novelJson);
            }
            JSONObject novelInfo = new JSONObject();
            novelInfo.put("novels", novels);
            result = ResultUtil.success("success!").toJSONObject().fluentPut("novelInfo", novelInfo);

        } catch (Exception e) {
            e.printStackTrace();
            result = ResultUtil.failure("failure!").toJSONObject();
        }

        return result;
    }


    /**
     * 审批章节
     */
    @GetMapping("/publishChapter")
    @Transactional
    public @ResponseBody
    JSONObject publishChapter() {
        return null;
    }
}
