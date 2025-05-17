package com.bwardweb.spring6_reactive_mongo.mappers;

import com.bwardweb.spring6_reactive_mongo.domain.Customer;
import com.bwardweb.spring6_reactive_mongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDto(Customer customer);
}
