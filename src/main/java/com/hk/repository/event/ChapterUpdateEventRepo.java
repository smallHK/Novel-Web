package com.hk.repository.event;

import com.hk.entity.event.ChapterUpdateEvent;
import org.springframework.data.repository.CrudRepository;

/**
 * @author smallHK
 * 2019/5/9 10:43
 */
public interface ChapterUpdateEventRepo extends CrudRepository<ChapterUpdateEvent, Integer> {
}
