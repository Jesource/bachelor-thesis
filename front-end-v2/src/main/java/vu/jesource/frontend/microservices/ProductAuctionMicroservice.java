package vu.jesource.frontend.microservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vu.jesource.frontend.models.Product;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Component // Used to be @Service
public class ProductAuctionMicroservice {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${service.productAndAuction.products.url}")
    private String PRODUCT_SERVICE_URL;
    @Value("${service.productAndAuction.endpoint.products}")
    private String SAVE_ENDPOINT;
    @Value("${service.authentication.endpoint.health}")
    private String HEALTH_ENDPOINT;

    public boolean isServiceAlive() {
        URI requestUri = null;
        try {
            requestUri = new URI(PRODUCT_SERVICE_URL + HEALTH_ENDPOINT);
        } catch (URISyntaxException e) {
            log.error("Failed to form product service health endpoint URI");
            return false;
        }

        ResponseEntity<String> response = restTemplate.getForEntity(requestUri, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }

    public void saveProduct(Product product, String jwt) {
        URI requestUri = null;
        ResponseEntity<String> response = null;

        try {
            requestUri = new URI(PRODUCT_SERVICE_URL + SAVE_ENDPOINT + "?jwt=" + jwt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Product> request = new HttpEntity<>(product, headers);


            // Send the POST request and receive the ResponseEntity without parsing the response body
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(requestUri, request, Void.class);

            // Process the response
            log.info("Received response: {}", responseEntity);

        } catch (URISyntaxException e) {
            log.error("Failed to form product service save endpoint URI");
        }
    }

    public ArrayList<Product> getAllProducts() {
        String apiUrl = "http://127.0.0.1:54397/api/product-service/products"; // Replace with your actual REST API endpoint
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);
        Product[] productsArray = response.getBody();

        if (productsArray != null) {
            return new ArrayList<>(Arrays.asList(productsArray));
        } else {
            return new ArrayList<>();
        }

    }

    public Product getProductById(String productId) {
        String apiUrl = "http://127.0.0.1:54397/api/product-service/product/" + productId; // Replace with your actual REST API endpoint
        Product product = null;
        try {
            ResponseEntity<Product> response = restTemplate.getForEntity(apiUrl, Product.class);
            product = response.getBody();
        } catch (RestClientException e) {
            log.info("Failed to get product details for product with ID '{}':", productId, e.getMessage());
            return null;
        }

        return product;
    }
}
