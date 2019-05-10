package com.hk.repository;

import com.hk.entity.NovelComment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/4/12 12:36
 */
public interface NovelCommentRepo extends CrudRepository<NovelComment, Integer> {

    List<NovelComment> findAllByNovelId(Integer novelId);

    Integer countAllByNovelId(Integer novelId);

    void deleteAllByNovelId(Integer novelId);

    List<NovelComment> findAllByReaderId(Integer readerId);
}
