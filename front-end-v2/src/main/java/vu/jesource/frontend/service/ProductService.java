package vu.jesource.frontend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.jesource.frontend.exception.AuthServiceException;
import vu.jesource.frontend.microservices.AuthMicroservice;
import vu.jesource.frontend.microservices.ProductAuctionMicroservice;
import vu.jesource.frontend.models.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    private final AuthMicroservice authMicroservice;
    private final ProductAuctionMicroservice productAuctionMicroservice;

    @Autowired
    public ProductService(AuthMicroservice authMicroservice, ProductAuctionMicroservice productAuctionMicroservice) {
        this.authMicroservice = authMicroservice;
        this.productAuctionMicroservice = productAuctionMicroservice;
    }



    public void save(Product product, Map<String, String> optionalParams, String jwtToken) {
        log.info("Checking Auth service availability");
        authMicroservice.checkConnection();

        if (!authMicroservice.isTokenValid(jwtToken)) {
            log.warn("Invalid token");
            // return Authorisation error
            throw new AuthServiceException("Invalid token");
        } else {
            log.info("Valid token");
        }

        product.setOptionalParameters(extractOptionalParams(optionalParams));
        product.setCreator(authMicroservice.getUserFromJwt(jwtToken));

        // save in product service
        productAuctionMicroservice.saveProduct(product, jwtToken);
    }

    private Set<Product.OptionalParameter> extractOptionalParams(Map<String, String> allParams) {
        log.info("Handling all params");
        Set<Product.OptionalParameter> optionalParameters = new HashSet<>();

        Set<String> keys = allParams.keySet();

        Set<String> filteredKeySet = keys.stream()
                .filter(item -> item.contains("optionalParameters.key."))
                .map(item -> item.replace("optionalParameters.key.", ""))
                .collect(Collectors.toSet());

        filteredKeySet.forEach(
                key -> {
                    String optionalParamKey = "optionalParameters.key." + key;
                    String optionalParamValue = "optionalParameters.value." + key;

                    optionalParameters.add(
                            new Product.OptionalParameter(
                                    allParams.get(optionalParamKey),
                                    allParams.get(optionalParamValue)
                            )
                    );
                }
        );

        log.info("Finished handling all params");
        return optionalParameters;
    }


    public ArrayList<Product> getAllProducts() {
        return productAuctionMicroservice.getAllProducts();
    }

    public Product findProductById(String productId) {
        return productAuctionMicroservice.getProductById(productId);
    }
}
