package vu.jesource.frontend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.jesource.frontend.exception.AuthServiceException;
import vu.jesource.frontend.microservices.AuthMicroservice;
import vu.jesource.frontend.models.RegistrationDetails;

@Slf4j
@Service
public class UserService {

    private final AuthMicroservice authMicroservice;

    @Autowired
    public UserService(AuthMicroservice authMicroservice) {
        this.authMicroservice = authMicroservice;
    }

    public void saveUser(RegistrationDetails user) {
        authMicroservice.save(user);
    }

    public String getJwtForUser(AuthMicroservice.LoginDetails loginDetails) throws AuthServiceException {
        return authMicroservice.getJwtForUser(loginDetails);
    }
}
