package vu.jesource.productservice.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vu.jesource.productservice.web.exceptions.CrudOperationException;
import vu.jesource.productservice.web.exceptions.UserAuthorisationException;
import vu.jesource.productservice.web.models.Product;
import vu.jesource.productservice.web.models.Product.UserDetails;
import vu.jesource.productservice.web.service.AuthService;
import vu.jesource.productservice.web.service.ProductService;

import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("/api/product-service/")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    @Autowired
    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @GetMapping("hi")
    public ResponseEntity<String> sayHello() {
        log.info("Tried to access welcoming endpoint");

        return new ResponseEntity<>("Hello World!", HttpStatus.ACCEPTED);
    }

    @PostMapping("products")
    public ResponseEntity<Object> createProduct(@RequestBody Product product, @RequestParam String jwt) {
        try {
            product.setCreator(validateTokenAndGetUserFromJwt(jwt));
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Failed to authenticate user", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Successfully verified user, but failed to format response: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Product savedProduct = productService.saveProduct(product);

        if (savedProduct.getId() == null) {
            return new ResponseEntity<>("Failed to save product", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Saved product: {}", savedProduct);

        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    private UserDetails validateTokenAndGetUserFromJwt(String jwt) throws URISyntaxException, JsonProcessingException {
        authService.validateJwt(jwt);

        return authService.getUserFromJwt(jwt);
    }

    @GetMapping("products")
    public ResponseEntity<Object> listAllProducts() {
        try {
            return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
        } catch (MongoSocketException | MongoTimeoutException e) {
            log.error("DB error occurred: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Something went wrong: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("product/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable String id, @RequestParam String jwt) {
        try {
            productService.deleteById(id, validateTokenAndGetUserFromJwt(jwt));
        } catch (CrudOperationException e) {
            return new ResponseEntity<>("Failed to delete: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (URISyntaxException | JsonProcessingException | UserAuthorisationException e) {
            return new ResponseEntity<>("Failed to verify user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                String.format("Product with id '%s' was successfully deleted.", id),
                HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable String id) {
        try {
            return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
        } catch (CrudOperationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("product/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable String id, @RequestBody Product product, @RequestParam String jwt) {
        try {
            return new ResponseEntity<>(productService.update(product, id, validateTokenAndGetUserFromJwt(jwt)), HttpStatus.OK);
        } catch (CrudOperationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (URISyntaxException | JsonProcessingException | UserAuthorisationException e) {
            return new ResponseEntity<>("Failed to verify user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
