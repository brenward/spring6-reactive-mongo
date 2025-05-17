package com.bwardweb.spring6_reactive_mongo.web.fn;

import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import com.bwardweb.spring6_reactive_mongo.model.CustomerDTO;
import com.bwardweb.spring6_reactive_mongo.services.BeerService;
import com.bwardweb.spring6_reactive_mongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final CustomerService customerService;
    private final Validator validator;

    private void  validate(CustomerDTO customerDTO){
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);

        if(errors.hasErrors()){
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request){
        return ServerResponse.ok().body(customerService.listCustomers(), CustomerDTO.class);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request){
        return ServerResponse.ok()
                .body(customerService.getCustomerById(request.pathVariable("customerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),CustomerDTO.class);
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request){
        return customerService.saveCustomer(request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate))
                .flatMap(savedDTO ->
                   ServerResponse.created(UriComponentsBuilder.fromPath(BeerRouterConfig.BEER_PATH_ID).build(savedDTO.getId()))
        .build());

    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(updateDTO -> customerService.updateCustomer(request.pathVariable("customerId"),updateDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(updatedDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(updateDTO -> customerService.patchCustomer(request.pathVariable("customerId"),updateDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(updatedDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        return customerService.getCustomerById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDTO -> customerService.deleteCustomerById(customerDTO.getId()))
                .then(ServerResponse.noContent().build());
    }
}
