package com.hk.controller;

import com.hk.constant.EventStatus;
import com.hk.entity.Admin;
import com.hk.entity.Editor;
import com.hk.entity.EditorRecommend;
import com.hk.entity.Profile;
import com.hk.po.EditorRecommendInfo;
import com.hk.po.NovelInfo;
import com.hk.repository.AdminRepository;
import com.hk.repository.EditorRepository;
import com.hk.repository.ProfileRepository;
import com.hk.service.AdminService;
import com.hk.service.NovelAlterService;
import com.hk.service.NovelService;
import com.hk.constant.EntityStatus;
import com.hk.util.ResultUtil;
import com.hk.constant.SessionProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author smallHK
 * 2019/3/19 21:03
 * <p>
 * 提供管理员服务
 */
@Controller
@EnableTransactionManagement
@RequestMapping(path = "/admin")
public class AdminController {



    private ProfileRepository profileRepository;

    private EditorRepository editorRepository;

    private AdminService adminService;

    private JavaMailSender mailSender;

    private NovelService novelService;

    private NovelAlterService novelAlterService;

    public AdminController(ProfileRepository profileRepository,
                           EditorRepository editorRepository,
                           JavaMailSender mailSender,
                           AdminService adminService,
                           NovelService novelService,
                           NovelAlterService novelAlterService
    ) {
        this.profileRepository = profileRepository;
        this.editorRepository = editorRepository;
        this.mailSender = mailSender;
        this.adminService = adminService;
        this.novelService = novelService;
        this.novelAlterService = novelAlterService;
    }

    /**
     * 根据输入的帐户信息
     * 返回判断结果
     *
     * @param username 用户名
     * @param pwd      密码
     * @return status=0登陆成功，status=1登陆失败
     */
    @PostMapping(path = "/login")
    public ModelAndView login(@RequestParam("username") String username, @RequestParam("password") String pwd, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Admin admin = adminService.login(username, pwd);
        if (Objects.nonNull(admin)) {
            session.setAttribute(SessionProperty.ROOT_LOGIN_NAME, username);
            session.setAttribute(SessionProperty.ROOT_LOGIN_ID, admin.getId());
            modelAndView.addObject("loginName", username);
            modelAndView.addObject("resultInfo", ResultUtil.success("Success!").toJSONObject());
            modelAndView.setViewName("redirect:/admin/enterAdminCenter");
            return modelAndView;
        }

        modelAndView.addObject("resultInfo", ResultUtil.failure("fail!").toJSONObject());
        modelAndView.setViewName("redirect:/admin/loginPage");
        return modelAndView;
    }

    /**
     * 管理中心
     */
    @GetMapping("/enterAdminCenter")
    public ModelAndView enterAdminCenter() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/adminCenterPage");

        List<Profile> unreadProfiles = adminService.findAllProfileByStatus(EntityStatus.PROFILE_UNREAD);
        List<Profile> passedProfiles = adminService.findAllProfileByStatus(EntityStatus.PROFILE_PASSED);
        List<NovelInfo> infos = novelService.findAllNovelInfo();
        List<EditorRecommendInfo> recommendInfos = adminService.gainAllSubmittedEditorRecommend();

        String lastCalTime = adminService.gainLastCalConsineTime();

        modelAndView.addObject("resultInfo", ResultUtil.success("success").toJSONObject());
        modelAndView.addObject("unreadProfiles", unreadProfiles);
        modelAndView.addObject("passedProfiles", passedProfiles);
        modelAndView.addObject("allNovelInfos", infos);
        modelAndView.addObject("recommendInfos", recommendInfos);
        modelAndView.addObject("lastCalTime", lastCalTime);

        return modelAndView;
    }


    /**
     * 通过审核，创建编辑用户
     * <p>
     * 指定简历审核通过
     * 创建Editor用户
     * 发送邮件进行通知
     */
    @GetMapping("/allowNewEditor/{profileId}")
    @Transactional
    public ModelAndView allowNewEditor(@PathVariable Integer profileId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/enterAdminCenter");
        try {
            //设置简历状态
            Optional<Profile> profileWrapper = profileRepository.findById(profileId);
            if (profileWrapper.isEmpty()) {
                throw new RuntimeException("找不到简历!");
            }
            Profile profile = profileWrapper.get();
            profile.setStatus(EntityStatus.PROFILE_PASSED);
            profileRepository.save(profile);

            //随机生成
            String editorName = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);
            String password = "123456";

            Editor editor = new Editor();
            editor.setEditorName(editorName);
            editor.setPassword(password);
            editor.setProfileId(profileId);
            editorRepository.save(editor);

            //发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("xiaoheiwanghk@qq.com");
            message.setTo(profile.getEMail());
            message.setSubject("恭喜录用！");
            message.setText("用户名:<" + editorName + ">" + System.lineSeparator() + "密码:<" + password + ">" + System.lineSeparator());
            mailSender.send(message);

            modelAndView.addObject("resultInfo", ResultUtil.success("Success!").toJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("resultInfo", ResultUtil.failure("fail!").toJSONObject());
            modelAndView.setViewName("/result");
        }

        return modelAndView;
    }

    /**
     * 计算余弦值
     */
    @GetMapping("/calculateCosineSim")
    public ModelAndView recommendByCosineSim() {
        ModelAndView modelAndView = new ModelAndView();
        adminService.calculateCosineSim();
        modelAndView.setViewName("redirect:/admin/enterAdminCenter");
        return modelAndView;
    }


    /**
     * 同意推荐
     */
    @GetMapping("/agreeEditorRecommend/{novelId}")
    public ModelAndView agreeEditorRecommend(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        adminService.agreeEditorRecommend(novelId);
        modelAndView.setViewName("redirect:/admin/enterAdminCenter");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }

    /**
     * 拒绝推荐
     */
    @GetMapping("/rejectEditorRecommend/{novelId}")
    public ModelAndView rejectEditorRecommend(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        adminService.rejectEditorRecommend(novelId);
        modelAndView.setViewName("redirect:/admin/enterAdminCenter");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }

    /**
     * 删除小说
     */
    @GetMapping("/deleteNovel/{novelId}")
    public ModelAndView deleteAllNovelInfo(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/enterAdminCenter");
        novelAlterService.deleteNovel(novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }

}
