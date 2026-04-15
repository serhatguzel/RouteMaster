package com.routemaster.RouteMaster.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.routemaster.RouteMaster.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByLocationCode(String locationCode);

    boolean existsByLocationCodeAndIdNot(String locationCode, Long id);

    List<Location> findAllByOrderByCityAscNameAsc();

}
