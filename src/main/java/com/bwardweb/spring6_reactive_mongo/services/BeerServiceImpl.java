package com.bwardweb.spring6_reactive_mongo.services;

import com.bwardweb.spring6_reactive_mongo.mappers.BeerMapper;
import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import com.bwardweb.spring6_reactive_mongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Mono<BeerDTO> getBeerById(String beerId) {
        return null;
    }

    @Override
    public Mono<BeerDTO> saveNewBeer(Mono<BeerDTO> beerDTO) {
        return beerDTO.map(beerMapper::beerDtoToBeer)
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDto);

    }
}
