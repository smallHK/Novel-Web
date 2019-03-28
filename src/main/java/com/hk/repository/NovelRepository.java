package com.hk.repository;

import com.hk.entity.Novel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * smallHK
 * 2019/3/26 17:13
 */
public interface NovelRepository extends CrudRepository<Novel, Integer> {

    List<Novel> findAllByAuthorId(Integer authorId);

}
