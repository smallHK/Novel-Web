package com.hk.service;

import com.hk.entity.Tag;
import com.hk.entity.TagNovelRelation;
import com.hk.repository.TagNovelRelationRepo;
import com.hk.repository.TagRepo;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 *
 * 增删改小说相关数据
 * @author smallHK
 * 2019/5/10 15:15
 */
@Service
public class NovelAlterService {

    private TagRepo tagRepo;

    private TagNovelRelationRepo tagNovelRelationRepo;

    public NovelAlterService(TagRepo tagRepo,
                             TagNovelRelationRepo tagNovelRelationRepo){
        this.tagRepo = tagRepo;
        this.tagNovelRelationRepo = tagNovelRelationRepo;
    }

    /**
     * 为小说添加标签
     */
    public void addNovelTag(String content, Integer novelId) {
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


}
