package com.hk.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.*;
import com.hk.repository.*;
import com.hk.util.CommonUtil;
import com.hk.po.VolumeInfo;
import com.hk.util.EntityStatus;
import com.hk.util.EntityUtil;
import com.hk.util.ResultUtil;
import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * smallHK
 * 2019/3/26 16:55
 * <p>
 * 为作者提供业务服务
 */
@Controller
@RequestMapping(path = "/creator")
@EnableTransactionManagement
public class CreatorService {


    private CreatorRepository creatorRepository;

    private NovelRepository novelRepository;

    private VolumeRepository volumeRepository;

    private ChapterRepository chapterRepository;

    private ParagraphRepository paragraphRepository;

    private NovelPublishRepo novelPublishRepo;

    private EditorRepository editorRepository;

    public CreatorService(CreatorRepository creatorRepository, NovelRepository novelRepository,
                          VolumeRepository volumeRepository, ChapterRepository chapterRepository,
                          ParagraphRepository paragraphRepository,
                          NovelPublishRepo novelPublishRepo,
                          EditorRepository editorRepository) {
        this.creatorRepository = creatorRepository;
        this.novelRepository = novelRepository;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
        this.novelPublishRepo = novelPublishRepo;
        this.editorRepository = editorRepository;
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
                    session.setAttribute("login_penname", penname);
                    session.setAttribute("login_creator_id", creator.getId());
                    modelAndView.setViewName("/creator/novelManagePage");
                    modelAndView.addObject("resultInfo", ResultUtil.success("读者登陆成功！"));
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

            Path parent = Path.of("D:\\CurriculumDesign\\Novel-Web\\src\\main\\resources\\static\\data\\novelCover\\cover",creator_id.toString());
            Files.createDirectories(parent);
            Path target = Path.of(parent.toString(), coverFileName);

            if(!Files.exists(target)) {
                Files.createFile(target);
                Files.write(target, coverData);
            }

            Novel novel = new Novel();
            novel.setAuthorId(creator_id);
            novel.setBriefIntro(briefIntro);
            novel.setNovelName(novelName);
            novel.setCoverImg("/data/novelCover/cover/" + creator_id + "/" + coverFileName);
            novel.setStatus(0);

            novelRepository.save(novel);
            modelAndView.addObject("resultInfo", ResultUtil.success("添加成功！"));
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
     * 显示作者的所有创作的书籍
     *
     * @return 成功返回小说列表
     */
    public ModelAndView listAllNovelByCreatorId(HttpSession session) {

        Integer creator_id = (Integer) session.getAttribute("login_creator_id");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/listNovel");
        try {
            List<Novel> novelList = novelRepository.findAllByAuthorId(creator_id);
            if (novelList == null) novelList = new ArrayList<>();
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
     * 返回登陆作者的所有小说
     */
    @GetMapping(path = "/listAllNovel")
    public @ResponseBody JSONObject listAllNovelFromLoginCreator(HttpSession session) {

        Integer creator_id = (Integer) session.getAttribute("login_creator_id");
        try {
            List<Novel> novelList = novelRepository.findAllByAuthorId(creator_id);
            if (novelList == null) novelList = new ArrayList<>();
            return ResultUtil.success("数据成功返回！").toJSONObject().fluentPut("novelList", novelList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.failure(e.getMessage()).toJSONObject();
        }
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

        try {
            List<Volume> volumeList = volumeRepository.findAllByNovelId(novelId);
            List<Chapter> chapterList = chapterRepository.findAllByNovelId(novelId);
            Novel novel = novelRepository.findNovelById(novelId);


            List<VolumeInfo> volumeInfoList = new ArrayList<>();

            for (Volume volume : volumeList) {
                VolumeInfo volumeInfo = new VolumeInfo();
                volumeInfo.setVolumeTitle(volume.getVolumeTitle());
                volumeInfo.setOrderNum(volume.getOrderNum());
                volumeInfo.setVolumeId(volume.getId());
                List<Chapter> voChapterList = new ArrayList<>();
                for (Chapter chapter : chapterList) {
                    if (chapter.getVolumeId().equals(volume.getId())) {
                        voChapterList.add(chapter);
                    }
                }
                volumeInfo.setChapterList(voChapterList);
                volumeInfoList.add(volumeInfo);
            }
            modelAndView.addObject("novelId", novel.getId());
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
     * 创建新卷
     **/
    @PostMapping(path = "/addNewVolume")
    public ModelAndView createVolume(@RequestParam(value = "novel_id") Integer novelId, @RequestParam(value = "volume_title") String volumeTitle) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/result");
        Volume volume = new Volume();
        volume.setNovelId(novelId);
        volume.setVolumeTitle(volumeTitle);
        Integer orderNum = volumeRepository.countAllByNovelId(novelId) + 1;
        volume.setOrderNum(orderNum);
        try {
            volumeRepository.save(volume);
            modelAndView.addObject("msg", "success!");
            modelAndView.addObject("status", 0);
            return modelAndView;
        } catch (Exception e) {
            modelAndView.addObject("msg", "fail!");
            modelAndView.addObject("status", 1);
        }
        return modelAndView;
    }


    /**
     * 创建新的章节
     */
    @PostMapping(path = "/addNewChapter")
    @Transactional
    public ModelAndView addNewChapter(@RequestParam() Map<String, String> params) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/result");

        Integer novelId = Integer.valueOf(params.get("novel_id"));
        Integer volumeId = Integer.valueOf(params.get("volume_id"));
        String chapterTitle = params.get("chapter_title");

        String originChapterContent = params.get("chapter_content");
        //文本处理
        String chapterContent = CommonUtil.textProcessing(originChapterContent);

        Integer wordCount = chapterContent.length();

        Chapter chapter = new Chapter();
        chapter.setNovelId(novelId);
        chapter.setVolumeId(volumeId);
        chapter.setTitle(chapterTitle);
        chapter.setWordCount(wordCount);

        try {
            Integer count = chapterRepository.countAllByNovelIdAndVolumeId(novelId, volumeId);
            chapter.setOrderNum(count + 1);

            Chapter resultChapter = chapterRepository.save(chapter);

            List<Paragraph> paragraphList = new ArrayList<>();
            String[] contents = chapterContent.split("\n");
            for (int i = 0; i < contents.length; i++) {
                String line = contents[i];
                Paragraph paragraph = new Paragraph();
                paragraph.setChapterId(resultChapter.getId());
                paragraph.setContent(line);
                paragraph.setOrderNum(i + 1);
                paragraphList.add(paragraph);
            }
            paragraphRepository.saveAll(paragraphList);

            modelAndView.addObject("status", 0);
            modelAndView.addObject("msg", "success!");

        } catch (Exception e) {

            modelAndView.addObject("status", 1);
            modelAndView.addObject("msg", "fail!");
            e.printStackTrace();
        }

        return modelAndView;
    }


    /**
     * 跳转添加新章节页面
     **/
    @GetMapping(path = "/enterAddChapterPage/{novelId}/{volumeId}")
    public ModelAndView addNewChapter(@PathVariable("novelId") Integer novelId, @PathVariable("volumeId") Integer volumeId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/addChapter");
        modelAndView.addObject("novelId", novelId);
        modelAndView.addObject("volumeId", volumeId);
        return modelAndView;
    }


    /**
     * 获取章节内容
     *
     * @param chapterId
     */
    @GetMapping(path = "/findChapterContent/{chapterId}")
    public ModelAndView findChapterContent(@PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/Content");
        try {
            Optional<Chapter> chapterWrapper = chapterRepository.findById(chapterId);
            if (!chapterWrapper.isPresent()) {
                throw new RuntimeException("no chapter!");
            }
            Chapter chapter = chapterWrapper.get();
            List<Paragraph> paragraphList = paragraphRepository.findAllByChapterId(chapterId);
            paragraphList.sort((p1, p2) -> p1.getOrderNum().compareTo(p2.getOrderNum()));
            modelAndView.addObject("chapterTitle", chapter.getTitle());
            modelAndView.addObject("paragraphList", paragraphList);
            modelAndView.addObject("msg", "success!");
            modelAndView.addObject("status", 0);
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("status", 1);
            modelAndView.addObject("msg", "fail!");
        }
        return modelAndView;
    }

    /**
     * 获取所有未申请发布小说
     */
    @GetMapping("/listAllCreatedNovel")
    public @ResponseBody
    JSONObject listAllCreatedNovel(HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        try {
            List<Novel> novelList = novelRepository.findAllByStatusAndAuthorId(EntityStatus.NOVLE_CREATED, creatorId);
            JSONArray novels = EntityUtil.entityListToJSONArray(novelList, Novel.class);
            return ResultUtil.success("success!").toJSONObject().fluentPut("novelList", novels);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.success("failure!").toJSONObject();

        }
    }


    /**
     * 申请小说发布
     */
    @GetMapping("/applyForPublish/{novelId}")
    public ModelAndView applyForPublish(@PathVariable Integer novelId) {

        ModelAndView modelAndView = new ModelAndView();
        try {
            Novel novel = novelRepository.findNovelById(novelId);
            novel.setStatus(EntityStatus.NOVEL_NO_PUBLISH);
            novelRepository.save(novel);
            modelAndView.setViewName("redirect:/creator/listAllNovel");
            modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("failure!").toJSONObject());
        }
        return modelAndView;
    }


    /**
     * 获取所有申请审核的小说
     * <p>
     * 小说状态为正在申请
     */
    @GetMapping("/listAllNoPublishedNovel")
    public @ResponseBody
    JSONObject listAllNoPublishedNovel(HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        try {
            List<Novel> novelList = novelRepository.findAllByStatusAndAuthorId(EntityStatus.NOVEL_NO_PUBLISH, creatorId);
            JSONArray novels = EntityUtil.entityListToJSONArray(novelList, Novel.class);
            return ResultUtil.success("success!").toJSONObject().fluentPut("novelList", novels);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.success("failure!").toJSONObject();

        }
    }

    /**
     * 获取所有公开小说
     * <p>
     * 小说状态为通过审核
     */
    @GetMapping("/listAllPublishedNovel")
    public @ResponseBody
    JSONObject listAllPublishedNovel(HttpSession session) {

        Integer creatorId = (Integer) session.getAttribute("login_creator_id");
        try {
            List<Novel> novelList = novelRepository.findAllByStatusAndAuthorId(EntityStatus.NOVEL_PASSED, creatorId);
            List<Integer> novelIdList = new ArrayList<>();
            for (Novel novel : novelList) {
                novelIdList.add(novel.getId());
            }
            Iterable<NovelPublish> novelPublishList = novelPublishRepo.findAllById(novelIdList);
            List<Integer> editorIdList = new ArrayList<>();
            for (NovelPublish novelPublish : novelPublishList) {
                editorIdList.add(novelPublish.getEditorId());
            }
            Iterable<Editor> editorIterable = editorRepository.findAllById(editorIdList);


            JSONArray info = new JSONArray();
            for (NovelPublish novelPublish : novelPublishList) {
                JSONObject novelAndEditor = new JSONObject();
                for (Novel novel : novelList) {
                    if (novel.getId().equals(novelPublish.getNovelId())) {
                        novelAndEditor.put("novel", novel);
                        break;
                    }
                }
                for (Editor editor : editorIterable) {
                    if (editor.getId().equals(novelPublish.getEditorId())) {
                        novelAndEditor.put("editor", editor);
                        break;
                    }
                }
                info.add(novelAndEditor);
            }
            return ResultUtil.success("success!").toJSONObject().fluentPut("info", info);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.success("failure!").toJSONObject();

        }
    }


}
