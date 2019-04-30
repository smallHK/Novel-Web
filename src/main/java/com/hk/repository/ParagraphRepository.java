package com.hk.repository;

import com.hk.entity.Paragraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * smallHK
 * 2019/3/27 20:54
 */
public interface ParagraphRepository extends CrudRepository<Paragraph, Integer> {

    List<Paragraph> findAllByChapterId(Integer chapterId);

    void deleteAllByNovelId(Integer novelId);

}
