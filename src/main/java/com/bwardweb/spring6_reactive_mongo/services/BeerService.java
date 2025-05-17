package com.bwardweb.spring6_reactive_mongo.services;

import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import reactor.core.publisher.Mono;

public interface BeerService {

    Mono<BeerDTO> getBeerById(String beerId);

    Mono<BeerDTO> saveNewBeer(Mono<BeerDTO> beerDTO);
}
