package vu.jesource.frontend.microservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vu.jesource.frontend.exception.AuthServiceException;
import vu.jesource.frontend.exception.UserAuthorisationException;
import vu.jesource.frontend.models.Product.UserDetails;
import vu.jesource.frontend.models.RegistrationDetails;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class AuthMicroservice {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${service.authentication.url}")
    private String SERVICE_URL;
    @Value("${service.authentication.endpoint.tokenValidation}")
    private String TOKEN_VALIDATION_ENDPOINT;
    @Value("${service.authentication.endpoint.getUserDetails}")
    private String USER_DETAILS_ENDPOINT;
    @Value("${service.authentication.endpoint.health}")
    private String HEALTH_ENDPOINT;
    @Value("${service.authentication.endpoint.register}")
    private String REGISTRATION_ENDPOINT;
    @Value("${service.authentication.endpoint.login}")
    private String LOGIN_ENDPOINT;

    public boolean isTokenValid(String jwt) throws UserAuthorisationException {
        log.debug("Token validation endpoint is: {}", SERVICE_URL + TOKEN_VALIDATION_ENDPOINT);
        log.debug("Validating JWT: {}", jwt);

        URI requestUrl = null;
        try {
            requestUrl = new URI(SERVICE_URL + TOKEN_VALIDATION_ENDPOINT + "?jwt=" + jwt);
        } catch (URISyntaxException e) {
            log.error("Failed to form authentication service TOKEN VALIDATION endpoint URI");
        }

        ResponseEntity<Void> response = null;
        try {
            response = restTemplate.getForEntity(requestUrl, Void.class);
        } catch (RestClientException e) {
            log.info("Something went wrong: {}", e.getMessage());
            return false;
        }

        log.info("Response from service: {}", response);

        return response.getStatusCode() == HttpStatus.ACCEPTED;
    }

    public void save(RegistrationDetails registrationDetails) {
        URI requestUri = null;

        try {
            requestUri = new URI(SERVICE_URL + REGISTRATION_ENDPOINT);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegistrationDetails> request = new HttpEntity<>(registrationDetails, headers);


            // Send the POST request and receive the ResponseEntity without parsing the response body
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(requestUri, request, Void.class);

            // Process the response
            log.info("Received response: {}", responseEntity);
        } catch (URISyntaxException e) {
            log.error("Failed to form authentication service REGISTRATION endpoint URI");
            throw new AuthServiceException("Failed to form authentication service REGISTRATION endpoint URI");
        }
    }
    public UserDetails getUserFromJwt(String jwt) throws AuthServiceException {
        URI requestUri = null;

        try {
            requestUri = new URI(SERVICE_URL + USER_DETAILS_ENDPOINT + "?jwt=" + jwt);
        } catch (URISyntaxException e) {
            log.error("Failed to form authentication service TOKEN VALIDATION endpoint URI");
            throw new AuthServiceException("Failed to form authentication service TOKEN VALIDATION endpoint URI");
        }

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(requestUri, String.class);
        } catch (RestClientException e) {
            throw new AuthServiceException("Failed to process token" + e.getMessage());
        }
        Object responseBody = response.getBody();

        log.info("Response: {}", response);
        log.info("Response code: {}", response.getStatusCode());
        log.info("Response body: {}", responseBody.toString());

        if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
            try {
                return mapResponseBodyToUserDetails(responseBody.toString());
            } catch (JsonProcessingException e) {
                log.error("Failed to map user details to UserDetails.class");
                throw new AuthServiceException("Failed to map user details to UserDetails.class");
            }
        }

        throw new AuthServiceException("Failed to get user from JWT, reason: unknown");
    }

    private UserDetails mapResponseBodyToUserDetails(String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                responseBody,
                UserDetails.class
        );
    }

    public void checkConnection() {
        try {
            URI requestUri = new URI(SERVICE_URL + HEALTH_ENDPOINT);

            ResponseEntity<String> response = restTemplate.getForEntity(requestUri, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Auth service connection: OK");
            } else {
                log.warn("Auth service connection: PROBLEMATIC (response code {})", response.getStatusCode());
            }

        } catch (URISyntaxException e) {
            log.error("Failed to form authentication service health endpoint URI");
        }
    }

    public String getJwtForUser(LoginDetails loginDetails) {
        URI requestUri = null;

        try {
            requestUri = new URI(SERVICE_URL + LOGIN_ENDPOINT);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginDetails> request = new HttpEntity<>(loginDetails, headers);


            // Send the POST request and receive the ResponseEntity without parsing the response body
            ResponseEntity<String> responseEntity = null;
            try {
                responseEntity = restTemplate.postForEntity(requestUri, request, String.class);
            } catch (RestClientException e) {
                throw new AuthServiceException("Login failed, check your credentials");
            }

            // Process the response
            log.info("Received response: {}", responseEntity);
            return responseEntity.getBody();
        } catch (URISyntaxException e) {
            log.error("Failed to form authentication service REGISTRATION endpoint URI");
            throw new AuthServiceException("Failed to form authentication service REGISTRATION endpoint URI");
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginDetails {
        private String username;
        private String password;
    }
}
