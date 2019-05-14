package com.hk.service;

import com.hk.constant.EntityStatus;
import com.hk.constant.EventStatus;
import com.hk.entity.*;
import com.hk.repository.*;
import com.hk.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * 增删改小说相关数据
 * @author smallHK
 * 2019/5/10 15:15
 */
@Service
@EnableTransactionManagement
public class NovelAlterService {

    private NovelRepository novelRepository;

    private NovelCommentRepo novelCommentRepo;

    private NovelPublishRepo novelPublishRepo;

    private VolumeRepository volumeRepository;

    private ChapterRepository chapterRepository;

    private ParagraphRepository paragraphRepository;



    private TagRepo tagRepo;

    private TagNovelRelationRepo tagNovelRelationRepo;

    private EditorRecommendRepo editorRecommendRepo;

    public NovelAlterService(NovelRepository novelRepository,
                             NovelCommentRepo novelCommentRepo,
                             NovelPublishRepo novelPublishRepo,
                             VolumeRepository volumeRepository,
                             ChapterRepository chapterRepository,
                             ParagraphRepository paragraphRepository,
                             TagRepo tagRepo,
                             TagNovelRelationRepo tagNovelRelationRepo,
                             EditorRecommendRepo editorRecommendRepo) {
        this.novelRepository = novelRepository;
        this.novelCommentRepo = novelCommentRepo;
        this.novelPublishRepo = novelPublishRepo;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
        this.tagRepo = tagRepo;
        this.tagNovelRelationRepo = tagNovelRelationRepo;
        this.editorRecommendRepo = editorRecommendRepo;
    }

    /**
     * 为小说添加标签
     */
    public void addNovelTag(String content, Integer novelId) {
        if(content.equals("")) return;
        Tag tag = tagRepo.findAllByContent(content);
        if(Objects.isNull(tag)) {
            Tag newTag = new Tag();
            newTag.setContent(content);
            tag = tagRepo.save(newTag);
        }else {
            TagNovelRelation oldRelation = tagNovelRelationRepo.findAllByNovelIdAndTagId(novelId, tag.getId());
            if(Objects.nonNull(oldRelation)) {
                return; //不添加重复标签
            }
        }
        TagNovelRelation relation = new TagNovelRelation();
        relation.setNovelId(novelId);
        relation.setTagId(tag.getId());
        tagNovelRelationRepo.save(relation);
    }

    /**
     * 为小说移除标签
     */
    public void removeNovelTag(Integer tagId, Integer novelId) {
        TagNovelRelation relation = tagNovelRelationRepo.findAllByNovelIdAndTagId(novelId, tagId);
        tagNovelRelationRepo.deleteById(relation.getId());
    }

    /**
     * 添加新小说
     */
    public void addNewNovel(Integer creatorId, String novelName, String briefIntro, String coverFileName, byte[] coverData) {

        try {
            Path parent = Path.of("D:\\CurriculumDesign\\Novel-Web\\src\\main\\resources\\static\\data\\novelCover\\cover", creatorId.toString());
            Files.createDirectories(parent);
            Path target = Path.of(parent.toString(), coverFileName);

            if (!Files.exists(target)) {
                Files.createFile(target);
                Files.write(target, coverData);
            }
            Novel novel = new Novel();
            novel.setAuthorId(creatorId);
            novel.setBriefIntro(briefIntro);
            novel.setNovelName(novelName);
            novel.setCreateTime(Timestamp.from(Instant.now()));
            novel.setUpdateTime(Timestamp.from(Instant.now()));
            novel.setCompleteStatus(EntityStatus.NOVEL_UNCOMPLETED);
            novel.setCoverImg("/data/novelCover/cover/" + creatorId + "/" + coverFileName);
            novel.setStatus(0);
            novelRepository.save(novel);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 添加新卷
     */
    public void addNewVolume(Integer novelId, String volumeTitle) {
        Volume volume = new Volume();
        volume.setNovelId(novelId);
        volume.setVolumeTitle(volumeTitle);
        Integer orderNum = volumeRepository.countAllByNovelId(novelId) + 1;
        volume.setOrderNum(orderNum);
        volumeRepository.save(volume);
    }

    /**
     * 添加新的章节
     */
    @Transactional
    public void addNewChapter(Integer novelId, Integer volumeId, String chapterTitle, String originText) {

        //文本处理
        String chapterContent = CommonUtil.textProcessing(originText);

        Integer wordCount = chapterContent.length();

        Chapter chapter = new Chapter();
        chapter.setNovelId(novelId);
        chapter.setVolumeId(volumeId);
        chapter.setTitle(chapterTitle);
        chapter.setWordCount(wordCount);

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
            paragraph.setNovelId(novelId);
            paragraphList.add(paragraph);
        }
        paragraphRepository.saveAll(paragraphList);

    }


    /**
     * 添加评论
     */
    public void publishNovelComment(Integer readerId, Integer novelId, String originContent) {

        NovelComment novelComment = new NovelComment();
        novelComment.setContent(originContent);
        novelComment.setReaderId(readerId);
        novelComment.setNovelId(novelId);
        Integer number = novelCommentRepo.countAllByNovelId(novelId);
        novelComment.setOrderNum(number + 1);
        novelComment.setCreateTime(Timestamp.from(Instant.now()));
        novelCommentRepo.save(novelComment);

    }

    /**
     * 添加编辑推荐
     */
    public void addEditorRecommend(Integer novelId, Integer editorId, String reason) {
        EditorRecommend editorRecommend = new EditorRecommend();
        editorRecommend.setEditorId(editorId);
        editorRecommend.setNovelId(novelId);
        editorRecommend.setRecommendTime(Timestamp.from(Instant.now()));
        editorRecommend.setStatus(EventStatus.EDITOR_RECOMMEND_SUBMITTED);
        editorRecommend.setReason(reason);
        editorRecommendRepo.save(editorRecommend);
    }

    /**
     * 更新章节
     */
    @Transactional
    public void updateChapter(Integer chapterId,  String chapterTitle, String originText) {

        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        String chapterContent = CommonUtil.textProcessing(originText);

        Instant now = Instant.now();

        //清楚所有段落
        paragraphRepository.deleteAllByChapterId(chapterId);

        //更新章节信息
        chapter.setClickCount(0);
        chapter.setTitle(chapterTitle);
        chapter.setWordCount(chapterContent.length());

        //添加新的段落
        addParagraphList(chapterContent, chapterId, chapter.getNovelId());

        //更新小说更新时间
        Novel novel = novelRepository.findById(chapter.getNovelId()).orElseThrow();
        novel.setUpdateTime(Timestamp.from(now));

        //如果小说已经被开放，而且章节也已经被开放，发布事件
//        if(novel.getStatus().equals(EntityStatus.NOVEL_PASSED)) {//如果小说已经被开放，发布更新时间给管理的编辑
//            NovelPublish novelPublish = novelPublishRepo.findByNovelId(novel.getId());
//
//            //发布章节更新事件
//            ChapterUpdateEvent event = new ChapterUpdateEvent();
//            event.setChapterId(chapterId);
//            event.setEditorId(novelPublish.getEditorId());
//            event.setUpdateTime(Timestamp.from(now));
//            event.setStatus(EventStatus.CHAPTER_UPDATE_SUBMITTED);
//
//        }

    }


    private void addParagraphList(String chapterContent, Integer chapterId, Integer novelId) {
        List<Paragraph> paragraphList = new ArrayList<>();
        String[] contents = chapterContent.split("\n");
        for (int i = 0; i < contents.length; i++) {
            String line = contents[i];
            Paragraph paragraph = new Paragraph();
            paragraph.setChapterId(chapterId);
            paragraph.setContent(line);
            paragraph.setOrderNum(i + 1);
            paragraph.setNovelId(novelId);
            paragraphList.add(paragraph);
        }
        paragraphRepository.saveAll(paragraphList);
    }


    /**
     * 删除执行小说的一切相关数据
     *
     * @param novelId 制定小说id
     */
    @Transactional
    public void deleteNovel(Integer novelId) {

        //删除小说
        novelRepository.deleteAllById(novelId);

        //删除小说评论
        novelCommentRepo.deleteAllByNovelId(novelId);

        //删除小说发布映射
        novelPublishRepo.deleteAllByNovelId(novelId);

        //删除小说卷
        volumeRepository.deleteAllByNovelId(novelId);

        //删除小说章节
        chapterRepository.deleteAllByNovelId(novelId);

        //删除小说段落
        paragraphRepository.deleteAllByNovelId(novelId);

        //删除对小说的收藏

        //删除对小说的标签

        //删除小说的相关事件，小说发布事件、卷发布事件、章节更新事件


    }
}
