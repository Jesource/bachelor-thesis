package vu.jesource.authentication.web.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vu.jesource.authentication.web.exceptions.TokenProcessingException;
import vu.jesource.authentication.web.models.User;

@Slf4j
@Service
public class RequestProcessingService {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public RequestProcessingService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public ResponseEntity getUserDetailsResponse(String jwt) {
        ResponseEntity<String> validationResponse = jwtService.getJwtValidationResponse(jwt);

        try {
            if (!validationResponse.getStatusCode().is2xxSuccessful()) {
                return validationResponse;
            }

            UserDetails userDetails = mapUserToUserDetails(jwtService.getUserDataFromToken(jwt));
            log.info("Returning user ID '{}'", userDetails);

            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (TokenProcessingException | JsonProcessingException e) {
            log.error("Failed to extract user '{}'", e.getMessage());
            return new ResponseEntity<>("Failed to extract user ID: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private UserDetails mapUserToUserDetails(User userFromToken) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(
                userFromToken,
                UserDetails.class
        );
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserDetails {
        private long userId;
        private String username;
    }
}
