package com.hk.repository;

import com.hk.entity.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/4/3 21:54
 */
public interface ProfileRepository extends CrudRepository<Profile, Integer> {

    List<Profile> findAllByStatus(Integer status);

}
