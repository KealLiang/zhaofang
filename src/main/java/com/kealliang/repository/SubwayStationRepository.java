package com.kealliang.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.kealliang.entity.SubwayStation;

/**
 * Created by 瓦力.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {
    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
