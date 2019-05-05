package com.hk.repository;

import com.hk.entity.Favorite;
import org.springframework.data.repository.CrudRepository;

/**
 * @author smallHK
 * 2019/5/5 22:08
 */
public interface FavoriteRepo extends CrudRepository<Favorite, Integer> {
}
