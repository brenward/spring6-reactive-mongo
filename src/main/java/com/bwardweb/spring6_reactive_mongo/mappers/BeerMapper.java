package com.bwardweb.spring6_reactive_mongo.mappers;

import com.bwardweb.spring6_reactive_mongo.domain.Beer;
import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
