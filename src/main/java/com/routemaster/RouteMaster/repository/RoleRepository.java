package com.routemaster.RouteMaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.routemaster.RouteMaster.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
