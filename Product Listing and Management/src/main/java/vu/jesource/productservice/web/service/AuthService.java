package vu.jesource.productservice.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vu.jesource.productservice.web.exceptions.UserAuthorisationException;
import vu.jesource.productservice.web.models.Product.UserDetails;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class AuthService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${service.authentication.url}")
    private String SERVICE_URL;
    @Value("${service.authentication.endpoint.tokenValidation}")
    private String TOKEN_VALIDATION_ENDPOINT;
    @Value("${service.authentication.endpoint.getUserDetails}")
    private String USER_DETAILS_SERVICE;

    public void validateJwt(String jwt) throws URISyntaxException, UserAuthorisationException {
        log.debug("Token validation endpoint is: {}", SERVICE_URL + TOKEN_VALIDATION_ENDPOINT);
        log.debug("Validating JWT: {}", jwt);

        URI requestUrl = new URI(SERVICE_URL + TOKEN_VALIDATION_ENDPOINT + "?jwt=" + jwt);

        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        log.info("Response from service: {}", response);

        if (response.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new UserAuthorisationException("Failed to verify user");
        }
    }

    public UserDetails getUserFromJwt(String jwt) throws URISyntaxException, JsonProcessingException {
        URI requestUri = new URI(SERVICE_URL + USER_DETAILS_SERVICE + "?jwt=" + jwt);

        ResponseEntity<String> response = restTemplate.getForEntity(requestUri, String.class);
        Object responseBody = response.getBody();

        log.info("Response: {}", response);
        log.info("Response code: {}", response.getStatusCode());
        log.info("Response body: {}", responseBody);

        if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
            return mapResponseBodyToUserDetails(responseBody.toString());
        }

        return null;
    }

    private UserDetails mapResponseBodyToUserDetails(String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                responseBody,
                UserDetails.class
        );
    }
}
