package com.hk.repository;

import com.hk.entity.NovelPublish;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/4/10 19:53
 */
public interface NovelPublishRepo extends CrudRepository<NovelPublish, Integer> {

    void deleteAllByNovelId(Integer novelId);

    List<NovelPublish> findAllByEditorId(Integer editorId);
}
