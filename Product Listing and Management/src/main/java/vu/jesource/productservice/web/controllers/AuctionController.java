package vu.jesource.productservice.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.*;
import vu.jesource.productservice.web.exceptions.CrudOperationException;
import vu.jesource.productservice.web.models.Auction;
import vu.jesource.productservice.web.models.Product.UserDetails;
import vu.jesource.productservice.web.service.AuctionService;
import vu.jesource.productservice.web.service.AuthService;

import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("/api/auction-service/")
public class AuctionController {
    private final AuctionService auctionService;
    private final AuthService authService;

    @Autowired
    public AuctionController(AuctionService auctionService, AuthService authService) {
        this.auctionService = auctionService;
        this.authService = authService;
    }


    @GetMapping("hi")
    public ResponseEntity<String> sayHello() {
        log.info("Tried to access welcoming endpoint");

        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @PostMapping("auctions")
    public ResponseEntity<Object> createAuction(@RequestBody Auction auction, @RequestParam String jwt) {
        try {
            auction.setCreator(validateTokenAndGetUserFromJwt(jwt));
            Auction savedAuction = auctionService.save(auction);

            if (savedAuction.getId() == null) {
                return new ResponseEntity<>("Failed to save auction", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            log.info("Saved {}", savedAuction);
            return new ResponseEntity<>(savedAuction, HttpStatus.OK);
        } catch (HttpMessageNotWritableException | IllegalArgumentException | NullPointerException e) {
            log.error("Failed to save auction: {}", e.getMessage());
            return new ResponseEntity<>(
                    String.format("Failed to save %s auction: %s", auction, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException | JsonProcessingException e) {
            return new ResponseEntity<>("Failed to authenticate user", HttpStatus.FORBIDDEN);
        }
    }

    private UserDetails validateTokenAndGetUserFromJwt(String jwt) throws URISyntaxException, JsonProcessingException {
        authService.validateJwt(jwt);

        return authService.getUserFromJwt(jwt);
    }

    @GetMapping("auctions")
    public ResponseEntity<Object> listAllAuctions() {
        try {
            return new ResponseEntity<>(auctionService.getAllAuctions(), HttpStatus.OK);
        } catch (MongoSocketException | MongoTimeoutException e) {
            log.error("DB error occurred: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Something went wrong: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("auctions/{id}")
    public ResponseEntity<Object> getAuctionById(@PathVariable String id) {
        try {
            return new ResponseEntity<>(auctionService.getAuctionById(id), HttpStatus.OK);
        } catch (CrudOperationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("auctions/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable String id, @RequestBody Auction auction, @RequestParam String jwt) {
        try {
            return new ResponseEntity<>(auctionService.update(auction, id, validateTokenAndGetUserFromJwt(jwt)), HttpStatus.OK);
        } catch (CrudOperationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (URISyntaxException | JsonProcessingException e) {
            return new ResponseEntity<>("Failed to authenticate user: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("auctions/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable String id, @RequestParam String jwt) {
        try {
            auctionService.deleteById(id, validateTokenAndGetUserFromJwt(jwt));
            return new ResponseEntity<>(
                    String.format("Product with id '%s' was successfully deleted.", id),
                    HttpStatus.NO_CONTENT
            );
        } catch (CrudOperationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (URISyntaxException | JsonProcessingException e) {
            return new ResponseEntity<>("Failed to authenticate user: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
