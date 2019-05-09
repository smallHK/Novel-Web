package com.hk.repository.event;

import com.hk.entity.event.ChapterPublishEvent;
import org.springframework.data.repository.CrudRepository;

/**
 * @author smallHK
 * 2019/5/3 16:59
 */
public interface ChapterPublishEventRepo extends CrudRepository<ChapterPublishEvent, Integer> {


}
