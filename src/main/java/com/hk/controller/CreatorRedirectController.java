package com.hk.controller;

import com.hk.po.ChapterInfo;
import com.hk.po.VolumeInfo;
import com.hk.service.NovelAlterService;
import com.hk.service.NovelService;
import com.hk.constant.EntityStatus;
import com.hk.util.ResultUtil;
import com.hk.constant.SessionProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.hk.util.ResultUtil.success;

/**
 * @author smallHK
 * 2019/5/10 14:20
 */
@Controller
@RequestMapping(path = "/creator")
public class CreatorRedirectController {

    private NovelService novelService;

    private NovelAlterService novelAlterService;

    public CreatorRedirectController(NovelService novelService,
                                     NovelAlterService novelAlterService) {
        this.novelService = novelService;
        this.novelAlterService = novelAlterService;
    }

    /**
     * 创建新的章节
     */
    @PostMapping(path = "/addNewChapter")
    public ModelAndView addNewChapter(@RequestParam Map<String, String> params) {

        ModelAndView modelAndView = new ModelAndView();

        Integer novelId = Integer.valueOf(params.get("novel_id"));
        Integer volumeId = Integer.valueOf(params.get("volume_id"));
        String chapterTitle = params.get("chapter_title");

        String originChapterContent = params.get("chapter_content");
        //文本处理
        novelAlterService.addNewChapter(novelId, volumeId, chapterTitle, originChapterContent);

        modelAndView.setViewName("redirect:/creator/chapterManage/" + novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("创建成功！"));

        return modelAndView;
    }


    /**
     * 更新章节
     */
    @PostMapping(path = "/updateChapter")
    public ModelAndView updateChapter(@RequestParam Map<String, String> params) {
        ModelAndView modelAndView = new ModelAndView();
        Integer chapterId = Integer.valueOf(params.get("chapter_id"));
        String chapterTitle = params.get("chapter_title");
        String originChapterContent = params.get("chapter_content");
        Integer novelId = novelService.findNovelIdByChapterId(chapterId);
        novelAlterService.updateChapter(chapterId, chapterTitle, originChapterContent);
        modelAndView.setViewName("redirect:/creator/chapterManage/" + novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("创建成功！"));
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
     * 申请章节开放
     */
    @GetMapping("/applyForChapterPublish/{chapterId}")
    public ModelAndView applyForChapterPublish(@PathVariable Integer chapterId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer authorId = (Integer)session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        novelService.publishChapterPublishEvent(chapterId, authorId);
        novelService.updateChapterPublishStatus(chapterId, EntityStatus.CHAPTER_CHECKING);
        ChapterInfo info = novelService.findNovelChapter(chapterId);
        modelAndView.setViewName("redirect:/creator/chapterManage/" + info.getNovelId());
        return modelAndView;
    }


    /**
     * 申请卷开放
     */
    @GetMapping("/applyForVolumePublish/{volumeId}")
    public ModelAndView applyForVolumePublish(@PathVariable Integer volumeId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer authorId = (Integer)session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        novelService.publishVolumePublishEvent(volumeId, authorId);
        novelService.updateVolumePublishStatus(volumeId, EntityStatus.VOLUME_CHECKING);
        VolumeInfo info = novelService.findVolumeInfo(volumeId);

        modelAndView.setViewName("redirect:/creator/chapterManage/" + info.getNovelId());
        return modelAndView;
    }



    /**
     * 创建新卷
     **/
    @PostMapping(path = "/addNewVolume")
    public ModelAndView createVolume(@RequestParam(name = "novel-id") Integer novelId, @RequestParam(name = "volume-name") String volumeTitle) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/creator/chapterManage/" + novelId);
        novelAlterService.addNewVolume(novelId, volumeTitle);
        return modelAndView;
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

            novelAlterService.addNewNovel(creator_id, novelName, briefIntro, coverFileName, coverData);
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


    //添加标签
    @PostMapping(path = "/addTag")
    public ModelAndView addTag(@RequestParam Map<String, String> params) {
        ModelAndView modelAndView = new ModelAndView();
        String tagContent = params.get("content");
        Integer novelId = Integer.valueOf(params.get("novelId"));
        novelAlterService.addNovelTag(tagContent, novelId);
        modelAndView.setViewName("redirect:/creator/infoManage/" + novelId);
        modelAndView.addObject("/resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }


    //移除标签
    @GetMapping(path = "/removeTag/{novelId}/{tagId}")
    public ModelAndView removeTag(@PathVariable Integer novelId, @PathVariable Integer tagId) {
        ModelAndView modelAndView = new ModelAndView();
        novelAlterService.removeNovelTag(tagId, novelId);
        modelAndView.setViewName("redirect:/creator/infoManage/" + novelId);
        modelAndView.addObject("/resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }


    //推荐小说
    @GetMapping(path = "/recommendNovel/{novelId}")
    public ModelAndView recommendNovel(@PathVariable Integer novelId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer editorId = (Integer) session.getAttribute(SessionProperty.EDITOR_LOGIN_EDITOR_ID);
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        return modelAndView;
    }

}
