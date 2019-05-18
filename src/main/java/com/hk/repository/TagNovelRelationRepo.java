package com.hk.repository;

import com.hk.entity.TagNovelRelation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/7 16:59
 */
public interface TagNovelRelationRepo extends CrudRepository<TagNovelRelation, Integer> {

    List<TagNovelRelation> findAllByNovelId(Integer novelId);

    TagNovelRelation findAllByNovelIdAndTagId(Integer novelId, Integer tagId);

    void deleteAllByNovelId(Integer novelId);

}
