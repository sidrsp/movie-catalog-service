package com.sidrsp.moviecatalogservice.resources;

import com.sidrsp.moviecatalogservice.resources.models.CatalogItem;
import com.sidrsp.moviecatalogservice.resources.models.Movie;
import com.sidrsp.moviecatalogservice.resources.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("WebClient Bean")
    private WebClient.Builder webClientbuilder;


    /*
    We can use this below object to do customized load balancing
    @Autowired
    private DiscoveryClient discoveryClient;
    */
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {

        /* using RestTemplate */
        UserRating userRating = restTemplate.getForObject(
                "http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class
        );

        /* using WebClient */
        return userRating.getUserRating().stream().map(rating -> {
            Movie movie = webClientbuilder.build()
                    .get()
                    .uri("http://movie-info-service/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();

            return new CatalogItem(movie.getName(), "desc-test", rating.getRating());
        }).collect(Collectors.toList());
    }

}
