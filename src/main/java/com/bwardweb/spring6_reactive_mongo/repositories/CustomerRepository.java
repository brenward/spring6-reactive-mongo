package com.bwardweb.spring6_reactive_mongo.repositories;

import com.bwardweb.spring6_reactive_mongo.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
