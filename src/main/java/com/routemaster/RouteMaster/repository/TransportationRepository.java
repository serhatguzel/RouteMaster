package com.routemaster.RouteMaster.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.routemaster.RouteMaster.entity.Transportation;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    boolean existsByOriginIdOrDestinationId(Long originId, Long destinationId);

    @Query("SELECT t FROM Transportation t WHERE :day MEMBER OF t.operationDays")
    List<Transportation> findByOperationDaysContaining(@Param("day") Integer day);

    List<Transportation> findAllByOrderByOriginNameAsc();

}
