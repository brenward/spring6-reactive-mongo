package com.bwardweb.spring6_reactive_mongo.services;

import com.bwardweb.spring6_reactive_mongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> listCustomers();

    Mono<CustomerDTO> getCustomerById(String id);

    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDTO);

    Mono<Void> deleteCustomerById(String id);

    Mono<CustomerDTO> patchCustomer(String id, CustomerDTO customerDTO);

    Mono<CustomerDTO> updateCustomer(String id, CustomerDTO customerDTO);
}
