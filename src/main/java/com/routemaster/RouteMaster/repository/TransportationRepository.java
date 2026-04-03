package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    List<Transportation> findByOrigin(Location location);


}
