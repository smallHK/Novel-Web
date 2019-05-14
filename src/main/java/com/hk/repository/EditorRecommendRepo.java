package com.hk.repository;

import com.hk.entity.EditorRecommend;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/13 21:58
 */
public interface EditorRecommendRepo extends CrudRepository<EditorRecommend, Integer> {

    List<EditorRecommend> findAllByStatus(Integer status);

    List<EditorRecommend> findAllByStatusOrderByRecommendTimeDesc(Integer status);

    List<EditorRecommend> findAllByNovelId(Integer novelId);


}
