package com.sidrsp.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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

    @HystrixCommand(fallbackMethod = "getFallbackUserRating",
            threadPoolKey = "userRatingInfoPool",
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
