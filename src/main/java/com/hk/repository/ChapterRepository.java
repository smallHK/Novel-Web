package com.hk.repository;

import com.hk.entity.Chapter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * smallHK
 * 2019/3/27 20:55
 */
public interface ChapterRepository extends CrudRepository<Chapter, Integer> {

    List<Chapter> findAllByNovelId(Integer id);
}
