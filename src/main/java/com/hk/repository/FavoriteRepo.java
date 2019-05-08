package com.hk.repository;

import com.hk.entity.Favorite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author smallHK
 * 2019/5/5 22:08
 */
public interface FavoriteRepo extends CrudRepository<Favorite, Integer> {

    Optional<Favorite> findByNovelIdAndReaderId(Integer novelId, Integer readerId);

    List<Favorite> findAllByReaderId(Integer readerId);
}
