package com.hk.repository;

import com.hk.entity.NovelComment;
import org.springframework.data.repository.CrudRepository;

/**
 * @author smallHK
 * 2019/4/12 12:36
 */
public interface NovelCommentRepo extends CrudRepository<NovelComment, Integer> {
}
