package com.hk.repository;

import com.hk.entity.Volume;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * smallHK
 * 2019/3/27 20:56
 */
public interface VolumeRepository extends CrudRepository<Volume, Integer> {

    List<Volume> findAllByNovelId(Integer novelId);

    Integer countAllByNovelId(Integer novelId);

    void deleteAllByNovelId(Integer novelId);

}
