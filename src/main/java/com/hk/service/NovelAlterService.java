package com.hk.service;

import com.hk.entity.Tag;
import com.hk.entity.TagNovelRelation;
import com.hk.repository.*;
import com.hk.repository.event.ChapterPublishEventRepo;
import com.hk.repository.event.VolumePublishEventRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

/**
 *
 * 增删改小说相关数据
 * @author smallHK
 * 2019/5/10 15:15
 */
@Service
public class NovelAlterService {

    private NovelRepository novelRepository;

    private NovelCommentRepo novelCommentRepo;

    private NovelPublishRepo novelPublishRepo;

    private VolumeRepository volumeRepository;

    private ChapterRepository chapterRepository;

    private ParagraphRepository paragraphRepository;



    private TagRepo tagRepo;

    private TagNovelRelationRepo tagNovelRelationRepo;

    public NovelAlterService(NovelRepository novelRepository,
                             NovelCommentRepo novelCommentRepo,
                             NovelPublishRepo novelPublishRepo,
                             VolumeRepository volumeRepository,
                             ChapterRepository chapterRepository,
                             ParagraphRepository paragraphRepository,
                             TagRepo tagRepo,
                             TagNovelRelationRepo tagNovelRelationRepo) {
        this.novelRepository = novelRepository;
        this.novelCommentRepo = novelCommentRepo;
        this.novelPublishRepo = novelPublishRepo;
        this.volumeRepository = volumeRepository;
        this.chapterRepository = chapterRepository;
        this.paragraphRepository = paragraphRepository;
        this.tagRepo = tagRepo;
        this.tagNovelRelationRepo = tagNovelRelationRepo;
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
