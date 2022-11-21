package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    @Value("${ead.api.url.course}")
    String REQUEST_URL_COURSE;

//    @Retry(name = "retryInstance", fallbackMethod = "retryFallback")
    @CircuitBreaker(name ="circuitBreakerInstance")
    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable) {
        List<CourseDto> searchResult = null;

        ResponseEntity<ResponsePageDto<CourseDto>> result = null;

        String url = REQUEST_URL_COURSE + utilsService.createUrlGetAllCoursesByUser(userId, pageable);

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try {
            ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {
            };
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Number of elements: {}", searchResult.size());

        } catch (HttpStatusCodeException e) {
            log.error("Error Request /courses {}", e);
        }

        log.info("End of Request /course userId: {}", userId);

        return result.getBody();
    }

    public ResponseEntity<CourseDto> getOneCourseById(UUID courseId) {
        String url = REQUEST_URL_COURSE + "/courses/" + courseId;
        return restTemplate.exchange(url, HttpMethod.GET, null, CourseDto.class);
    }

    public Page<CourseDto> retryFallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Inside RetryFallback, cause: {} ", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

    public Page<CourseDto> circuitBreakerFallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Inside CircuitBreakerFallback, cause: {} ", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }
}
