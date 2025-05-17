package com.bwardweb.spring6_reactive_mongo.web.fn;

import com.bwardweb.spring6_reactive_mongo.domain.Beer;
import com.bwardweb.spring6_reactive_mongo.mappers.BeerMapperImpl;
import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import com.bwardweb.spring6_reactive_mongo.services.BeerService;
import com.bwardweb.spring6_reactive_mongo.services.BeerServiceImpl;
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

import java.math.BigDecimal;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class BeerEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BeerService beerService;

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Test
    @Order(1)
    void testListBeers(){
        webTestClient.get()
                .uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(2)
    void testGetBeerById(){
        BeerDTO savedBeerDto = getSavedBeerDto();

        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, savedBeerDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    void testGetBeerByIdNotFound(){
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateBeer(){
        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testCreateBeerBadData(){
        Beer beer = getTestBeer();
        beer.setBeerName("");

        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(beer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testUpdateBeer(){
        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateBeerBadRequest(){
        Beer beer = getTestBeer();
        beer.setBeerStyle("");

        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(beer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateBeerNotFound(){
        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(5)
    void testPatchBeer(){
        webTestClient.patch().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testPatchBeerNotFound(){
        webTestClient.patch().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteBeer(){
        webTestClient.delete().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteBeerNotFound(){
        webTestClient.delete().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    public BeerDTO getSavedBeerDto(){
        return beerService.saveNewBeer(Mono.just(getTestBeerDto())).block();
    }

    public static BeerDTO getTestBeerDto(){
        return new BeerMapperImpl().beerToBeerDto(getTestBeer());
    }

    static Beer getTestBeer(){
        return Beer.builder()
                .beerName("Test Beer")
                .beerStyle("Ake")
                .quantityOnHand(10)
                .upc("123456789012")
                .price(new BigDecimal("12.99"))
                .build();
    }
}
