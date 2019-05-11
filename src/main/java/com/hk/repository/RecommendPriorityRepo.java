package com.hk.repository;

import com.hk.entity.RecommendPriority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/8 10:24
 */
public interface RecommendPriorityRepo extends CrudRepository<RecommendPriority, Long> {

    void deleteAllByReaderIdIn(List<Integer> readerIdList);

    List<RecommendPriority> findAllByReaderIdOrderByPriorityDesc(Integer readerId);
}
