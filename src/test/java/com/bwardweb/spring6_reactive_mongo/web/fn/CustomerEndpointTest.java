package com.bwardweb.spring6_reactive_mongo.web.fn;

import com.bwardweb.spring6_reactive_mongo.domain.Customer;
import com.bwardweb.spring6_reactive_mongo.mappers.BeerMapperImpl;
import com.bwardweb.spring6_reactive_mongo.mappers.CustomerMapperImpl;
import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import com.bwardweb.spring6_reactive_mongo.model.CustomerDTO;
import com.bwardweb.spring6_reactive_mongo.services.CustomerService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerService customerService;

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Test
    @Order(1)
    void testListCustomers(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouteConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(2)
    void testGetCustomerById(){
        CustomerDTO customerDTO = getSavedCustomerDto();

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouteConfig.CUSTOMER_PATH_ID,customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    void testGetCustomerByIdNotFound(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouteConfig.CUSTOMER_PATH_ID,999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateCustomer(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouteConfig.CUSTOMER_PATH)
                .body(Mono.just(getCustomer()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testCreateCustomerBadData(){
        Customer customer = getCustomer();
        customer.setCustomerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouteConfig.CUSTOMER_PATH)
                .body(Mono.just(customer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testUpdateCustomer(){
        CustomerDTO customerDTO = getSavedCustomerDto();
        customerDTO.setCustomerName("Changed Name");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateCustomerNotFound(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(getCustomer()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testPatchCustomer(){
        CustomerDTO customerDTO = getSavedCustomerDto();
        customerDTO.setCustomerName("Changed Name");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .patch().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testPatchCustomerNotFound(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .patch().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, "999")
                .body(Mono.just(getCustomer()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteCustomer(){
        CustomerDTO customerDTO = getSavedCustomerDto();
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteCustomerNotFound(){
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouteConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    public CustomerDTO getSavedCustomerDto(){
        return customerService.saveCustomer(Mono.just(getTestCustomerDto())).block();
    }

    public static CustomerDTO getTestCustomerDto(){
        return new CustomerMapperImpl().customerToCustomerDto(getCustomer());
    }

    static Customer getCustomer(){
        return Customer.builder()
                .customerName("John")
                .build();
    }
}
