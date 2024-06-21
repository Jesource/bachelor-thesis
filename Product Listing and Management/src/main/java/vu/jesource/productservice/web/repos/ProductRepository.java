package vu.jesource.productservice.web.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import vu.jesource.productservice.web.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
