package com.bwardweb.spring6_reactive_mongo.services;

import com.bwardweb.spring6_reactive_mongo.domain.Beer;
import com.bwardweb.spring6_reactive_mongo.mappers.BeerMapper;
import com.bwardweb.spring6_reactive_mongo.mappers.BeerMapperImpl;
import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDTO;

    @BeforeEach
    void setUp(){
        beerDTO = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    @DisplayName("Test Save Beer Using Subscriber")
    void saveBeerUseSubscriber() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        Mono<BeerDTO> savedMono = beerService.saveNewBeer(Mono.just(beerDTO));

        savedMono.subscribe(savedDto -> {
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        BeerDTO persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }


    @Test
    void saveNewBeer(){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<BeerDTO> beerDTOMono = beerService.saveNewBeer(Mono.just(beerDTO));

        beerDTOMono.subscribe(savedDto -> {
           System.out.println(savedDto.getId());
           atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test Save Beer Using Block")
    void testSaveBeerUseBlock() {
        BeerDTO savedDto = beerService.saveNewBeer(Mono.just(getTestBeerDto())).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Beer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Beer Name";  // use final so cannot mutate
        BeerDTO savedBeerDto = getSavedBeerDto();
        savedBeerDto.setBeerName(newName);

        BeerDTO updatedDto = beerService.saveNewBeer(Mono.just(savedBeerDto)).block();

        //verify exists in db
        BeerDTO fetchedDto = beerService.getBeerById(updatedDto.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Beer Name";  // use final so cannot mutate

        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        beerService.saveNewBeer(Mono.just(getTestBeerDto()))
                .map(savedBeerDto -> {
                    savedBeerDto.setBeerName(newName);
                    return savedBeerDto;
                })
                .flatMap(beerService::saveNewBeer) // save updated beer
                .flatMap(savedUpdatedDto -> beerService.getBeerById(savedUpdatedDto.getId())) // get from db
                .subscribe(dtoFromDb -> {
                    atomicDto.set(dtoFromDb);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getBeerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteBeer() {
        BeerDTO beerToDelete = getSavedBeerDto();

        beerService.deleteBeer(beerToDelete.getId()).block();

        Mono<BeerDTO> expectedEmptyBeerMono = beerService.getBeerById(beerToDelete.getId());

        BeerDTO emptyBeer = expectedEmptyBeerMono.block();

        assertThat(emptyBeer).isNull();

    }

    @Test
    void findFirstByBeerName(){
        BeerDTO beerDto = getSavedBeerDto();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        Mono<BeerDTO> foundDTO = beerService.findFirstByBeerName(beerDto.getBeerName());

        foundDTO.subscribe(dto -> {
            System.out.println(dto.toString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void findByBeerStyle(){
        BeerDTO beerDto = getSavedBeerDto();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerService.findByBeerStyle(beerDto.getBeerStyle())
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    public BeerDTO getSavedBeerDto(){
        return beerService.saveNewBeer(Mono.just(getTestBeerDto())).block();
    }

    public static BeerDTO getTestBeerDto(){
        return new BeerMapperImpl().beerToBeerDto(getTestBeer());
    }

    public static Beer getTestBeer(){
        return Beer.builder()
                .beerName("Test Beer")
                .beerStyle("Ake")
                .quantityOnHand(10)
                .upc("123456789012")
                .price(new BigDecimal("12.99"))
                .build();
    }

}