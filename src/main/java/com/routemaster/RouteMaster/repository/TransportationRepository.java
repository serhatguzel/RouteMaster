package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    boolean existsByOriginIdOrDestinationId(Long originId, Long destinationId);

    @Query("SELECT t FROM Transportation t WHERE :day MEMBER OF t.operationDays")
    List<Transportation> findByOperationDaysContaining(@Param("day") Integer day);

    List<Transportation> findAllByOrderByOriginNameAsc();
}
