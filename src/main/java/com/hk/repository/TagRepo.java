package com.hk.repository;

import com.hk.entity.Tag;
import org.springframework.data.repository.CrudRepository;

/**
 * @author smallHK
 * 2019/5/7 16:49
 */
public interface TagRepo extends CrudRepository<Tag, Integer> {
}
