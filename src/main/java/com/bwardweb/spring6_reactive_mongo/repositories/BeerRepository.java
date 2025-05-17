package com.bwardweb.spring6_reactive_mongo.repositories;

import com.bwardweb.spring6_reactive_mongo.domain.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {

}
