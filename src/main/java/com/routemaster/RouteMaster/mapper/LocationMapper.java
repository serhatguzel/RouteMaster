package com.routemaster.RouteMaster.mapper;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    LocationDto toDto(Location location);
    Location toEntity(LocationDto locationDto);
    void updateEntityFromDto(LocationDto dto, @MappingTarget Location entity);
}
