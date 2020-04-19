package com.sidrsp.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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

    @HystrixCommand(fallbackMethod = "getFallbackMovieInfo",
            threadPoolKey = "movieInfoPool",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"), // maximum thread pool size, num of concurrent threads
                    @HystrixProperty(name = "maxQueueSize", value = "10") // num of request queued which waits for the threads
            },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")
            }
    )
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
