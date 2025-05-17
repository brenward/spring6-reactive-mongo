package com.bwardweb.spring6_reactive_mongo.bootstrap;

import com.bwardweb.spring6_reactive_mongo.domain.Beer;
import com.bwardweb.spring6_reactive_mongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    @Autowired
    BeerRepository beerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll().doOnSuccess(success -> {
            loadBeerData();
        }).subscribe();

        beerRepository.count().subscribe(count -> {
            System.out.println("Beer Count: " + count);
        });
    }

    private void loadBeerData(){
        beerRepository.count().subscribe(count -> {
            if(count == 0){
                Beer beer1 = Beer.builder()
                        .beerName("Coors")
                        .beerStyle("Lager")
                        .upc("123456")
                        .price(new BigDecimal("6.10"))
                        .quantityOnHand(122)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer2 = Beer.builder()
                        .beerName("Guiness")
                        .beerStyle("Stout")
                        .upc("486151")
                        .price(new BigDecimal("6.50"))
                        .quantityOnHand(56)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer3 = Beer.builder()
                        .beerName("Harp")
                        .beerStyle("Lager")
                        .upc("5673493")
                        .price(new BigDecimal("4.50"))
                        .quantityOnHand(556)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                beerRepository.save(beer1).subscribe();
                beerRepository.save(beer2).subscribe();
                beerRepository.save(beer3).subscribe();
            }
        });
    }
}
