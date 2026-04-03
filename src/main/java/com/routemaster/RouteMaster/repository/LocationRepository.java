package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationCode(String locationCode);
    Boolean existsByLocationCode(String locationCode);

}
