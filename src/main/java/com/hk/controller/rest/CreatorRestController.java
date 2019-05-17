package com.hk.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.hk.constant.SessionProperty;
import com.hk.entity.Novel;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelIndex;
import com.hk.po.VolumeInfo;
import com.hk.service.CreatorService;
import com.hk.service.NovelService;
import com.hk.constant.EntityStatus;
import com.hk.util.ResultUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author smallHK
 * 2019/5/9 16:39
 */
@RestController
@RequestMapping(path = "/creator")
public class CreatorRestController {

    private CreatorService creatorService;

    private NovelService novelService;

    public CreatorRestController(NovelService novelService,
                                 CreatorService creatorService) {
        this.novelService = novelService;
        this.creatorService = creatorService;
    }

    /**
     * 获取指定章节的信息（ajax请求）
     */
    @GetMapping("/rest/chapterInfo/{chapterId}")
    public @ResponseBody
    JSONObject findChapterInfoByRest(@PathVariable Integer chapterId) {
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

    /**
     * 判断卷是否开放，已开放返回true
     */
    @GetMapping("/rest/jugdeVolumePublish/{volumeId}")
    public @ResponseBody JSONObject judegVolumePublishByRest(@PathVariable Integer volumeId) {
        boolean flag = novelService.judgeVolumePublished(volumeId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("flag", flag);
    }



    /**
     * 获取作者所有图书
     * 条件：页码
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
     * 获取所有图书总页数
     */
    @GetMapping("/createdNovelList/size")
    public @ResponseBody JSONObject countNovelPageAmount(HttpSession session) {
        Integer creatorId = (Integer) session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        Integer novelTotalNum = novelService.countSpecialCreatorAllNovelList(creatorId);
       return ResultUtil.success("success!").toJSONObject().fluentPut("allNovelPageSum", novelTotalNum % 12 == 0 ? novelTotalNum/12 : novelTotalNum/12 + 1);
    }

    /**
     * 获取所有未开放图书总数
     */
    @GetMapping("/unpublishedNovelList/size")
    public JSONObject countUnpublishedNovelPageAmount(HttpSession session) {
        Integer creatorId = (Integer) session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        Integer novelTotalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVLE_CREATED);
        return ResultUtil.success("success!").toJSONObject().fluentPut("novelPageSum", novelTotalNum % 12 == 0 ? novelTotalNum/12 : novelTotalNum/12 + 1);
    }

    /**
     * 获取所有审核中小说总数
     */
    @GetMapping("/checkingNovelList/size")
    public JSONObject countCheckingNovelPageAmount(HttpSession session) {
        Integer creatorId = (Integer) session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        Integer novelTotalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVEL_CHECKING);
        return ResultUtil.success("success!").toJSONObject().fluentPut("novelPageSum", novelTotalNum % 12 == 0 ? novelTotalNum/12 : novelTotalNum/12 + 1);
    }

    /**
     * 获取所有开放小说总数
     */
    @GetMapping("/publishedNovelList/size")
    public JSONObject countPublishedNovelPageAmount(HttpSession session) {
        Integer creatorId = (Integer) session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID);
        Integer novelTotalNum = novelService.countSpeCreSpePubStaNovel(creatorId, EntityStatus.NOVEL_PASSED);
        return ResultUtil.success("success!").toJSONObject().fluentPut("novelPageSum", novelTotalNum % 12 == 0 ? novelTotalNum/12 : novelTotalNum/12 + 1);
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
     * 检验笔名是否已经存在
     *
     * @return 0表示不存在相同笔名，1表示存在相同笔名
     */
    @GetMapping(path = "/checkPenNameExist")
    public @ResponseBody
    JSONObject existPenName(@RequestParam("penname") String penName) {
        boolean flag = creatorService.existPenName(penName);
        return ResultUtil.success("success!").toJSONObject().fluentPut("flag", flag);
    }


}
