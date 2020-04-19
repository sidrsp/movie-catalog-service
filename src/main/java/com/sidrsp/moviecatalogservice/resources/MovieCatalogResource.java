package com.sidrsp.moviecatalogservice.resources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sidrsp.moviecatalogservice.resources.models.CatalogItem;
import com.sidrsp.moviecatalogservice.resources.models.Movie;
import com.sidrsp.moviecatalogservice.resources.models.Rating;
import com.sidrsp.moviecatalogservice.resources.models.UserRating;
import com.sidrsp.moviecatalogservice.services.MovieInfoService;
import com.sidrsp.moviecatalogservice.services.UserRatingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private MovieInfoService movieInfoService;

    @Autowired
    private UserRatingInfoService userRatingInfoService;


    /*
    We can use this below object to do customized load balancing
    @Autowired
    private DiscoveryClient discoveryClient;
    */
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {

        UserRating userRating = userRatingInfoService.getUserRating(userId);
        return userRating.getUserRating().stream().map(rating -> {
            Movie movie = movieInfoService.getMovieInfo(rating);
            return new CatalogItem(movie.getName(), "desc-test", rating.getRating());
        }).collect(Collectors.toList());
    }

    /*
    * Note: When a fallback method is defined in the same class, hystrix has no way to get
    * the instance to call the fallback methods as it always holds on to instance of proxy class,
    * but if we define the fallback methods in an external class as service or beans and then
    * autowire, hystrix gets the proxy instance to call fallbacks
    *
    * */
    /*
    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    private UserRating getUserRating(String userId) {
        *//* using RestTemplate *//*
        return restTemplate.getForObject(
                "http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class
        );
    }

    private UserRating getFallbackUserRating(String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserRating(
                Arrays.asList(
                        new Rating("fallback movieId", 0)
                )
        );
        return userRating;
    }
    */

}
