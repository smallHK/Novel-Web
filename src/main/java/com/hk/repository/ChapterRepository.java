package com.hk.repository;

import com.hk.entity.Chapter;
import com.hk.entity.Novel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * smallHK
 * 2019/3/27 20:55
 */
public interface ChapterRepository extends CrudRepository<Chapter, Integer> {

    List<Chapter> findAllByNovelId(Integer id);

    Integer countAllByNovelIdAndVolumeId(Integer novelId, Integer voulmeId);

    void deleteAllByNovelId(Integer novelId);

    Integer countAllByNovelId(Integer novelId);

//    Chapter findNovelByOrderNumAndNovelId(Integer orderNum, Integer novelId);

    List<Chapter> findAllByVolumeId(Integer volumeId);

    Chapter findAllByVolumeIdAndNovelIdAndOrderNum(Integer volumeId, Integer novelId, Integer orderNum);

}
