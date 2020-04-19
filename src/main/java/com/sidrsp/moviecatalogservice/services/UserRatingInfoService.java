package com.sidrsp.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sidrsp.moviecatalogservice.resources.models.Rating;
import com.sidrsp.moviecatalogservice.resources.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfoService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    public UserRating getUserRating(String userId) {
        /* using RestTemplate */
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
}
