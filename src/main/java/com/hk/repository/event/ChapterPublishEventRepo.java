package com.hk.repository.event;

import com.hk.entity.event.ChapterPublishEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/3 16:59
 */
public interface ChapterPublishEventRepo extends CrudRepository<ChapterPublishEvent, Integer> {

    List<ChapterPublishEvent> findAllByEditorIdAndStatus(Integer editorId, Integer status);

}
