package com.hk.repository;

import com.hk.entity.AdminOperationLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/11 10:32
 */
public interface AdminOperationLogRepo extends CrudRepository<AdminOperationLog, Integer> {

    List<AdminOperationLog> findAllByType(Integer type);
}
