package com.sidrsp.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sidrsp.moviecatalogservice.resources.models.Movie;
import com.sidrsp.moviecatalogservice.resources.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MovieInfoService {

    @Autowired
    @Qualifier("WebClient Bean")
    private WebClient.Builder webClientbuilder;

    @HystrixCommand(fallbackMethod = "getFallbackMovieInfo")
    public Movie getMovieInfo(Rating rating) {

        /* using WebClient */
        return webClientbuilder.build()
                .get()
                .uri("http://movie-info-service/movies/" + rating.getMovieId())
                .retrieve()
                .bodyToMono(Movie.class)
                .block();
    }

    private Movie getFallbackMovieInfo(Rating rating) {
        return new Movie(rating.getMovieId(), "fallback movie name");
    }
}
