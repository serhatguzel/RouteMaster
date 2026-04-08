package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Boolean existsByLocationCode(String locationCode);
    Boolean existsByLocationCodeAndIdNot(String locationCode, Long id);
    
    List<Location> findAllByOrderByCityAscNameAsc();

}
