package com.bwardweb.spring6_reactive_mongo.web.fn;

import com.bwardweb.spring6_reactive_mongo.model.BeerDTO;
import com.bwardweb.spring6_reactive_mongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;

    public Mono<ServerResponse> listBeers(ServerRequest request){
        return ServerResponse.ok()
                .body(beerService.listBeers(), BeerDTO.class);
    }

    public Mono<ServerResponse> getBeerById(ServerRequest request){
        return ServerResponse.ok()
                .body(beerService.getBeerById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))), BeerDTO.class);
    }

    public Mono<ServerResponse> createBeer(ServerRequest request){
        return beerService.saveNewBeer(request.bodyToMono(BeerDTO.class))
                .flatMap(beerDTO -> ServerResponse
                        .created(UriComponentsBuilder.fromPath(
                                BeerRouterConfig.BEER_PATH_ID)
                                .build(beerDTO.getId()))
                .build());
    }

    public Mono<ServerResponse> updateBeer(ServerRequest request){
        return request.bodyToMono(BeerDTO.class).flatMap(beerDTO ->
            beerService.updateBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchBeer(ServerRequest request){
        return request.bodyToMono(BeerDTO.class).flatMap(beerDTO ->
                        beerService.patchBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteBeer(ServerRequest request){
        return beerService.getBeerById(request.pathVariable("beerId"))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> beerService.deleteBeer(beerDTO.getId()))
                .then(ServerResponse.noContent().build());
    }
}
