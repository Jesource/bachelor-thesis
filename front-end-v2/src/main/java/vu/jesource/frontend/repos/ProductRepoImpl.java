package vu.jesource.frontend.repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vu.jesource.frontend.exception.CrudOperationException;
import vu.jesource.frontend.microservices.ProductAuctionMicroservice;
import vu.jesource.frontend.models.Product;

@Service
public class ProductRepoImpl implements ProductRepo {
//    private ProductAuctionMicroservice productAuctionMicroservice;
//
//    @Autowired
//    public ProductRepoImpl(ProductAuctionMicroservice productAuctionMicroservice) {
//        this.productAuctionMicroservice = productAuctionMicroservice;
//    }

    @Override
    public void save(Product product) {
//        productAuctionMicroservice.saveProduct(product);
    }

    @Override
    public void edit(Product product) {

    }

    @Override
    public Product get(String id) throws CrudOperationException {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
