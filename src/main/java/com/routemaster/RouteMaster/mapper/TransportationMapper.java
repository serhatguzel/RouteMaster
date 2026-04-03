package com.routemaster.RouteMaster.mapper;

import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Transportation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransportationMapper {

    TransportationDto toDto(Transportation transportation);
    Transportation toEntity(TransportationDto transportationDto);
    void updateEntityFromDto(TransportationDto dto, @MappingTarget Transportation entity);
}
