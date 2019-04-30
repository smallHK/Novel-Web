package com.hk.repository;

import com.hk.entity.Creator;
import org.springframework.data.repository.CrudRepository;

/**
 * smallHK
 * 2019/3/21 16:11
 */
public interface CreatorRepository extends CrudRepository<Creator, Integer> {

    Integer countCreatorByPenName(String penName);
}
