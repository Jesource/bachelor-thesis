package vu.jesource.productservice.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.jesource.productservice.web.exceptions.CrudOperationException;
import vu.jesource.productservice.web.exceptions.CrudOperationException.Operations;
import vu.jesource.productservice.web.exceptions.UserAuthorisationException;
import vu.jesource.productservice.web.models.Product;
import vu.jesource.productservice.web.models.Product.UserDetails;
import vu.jesource.productservice.web.repos.ProductRepository;
import vu.jesource.productservice.web.validators.ProductValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private static void checkIfProductExist(boolean dbProductCheck,
                                            String id, Operations crudOperation) {
        if (dbProductCheck) {
            log.info("Failed to {} product with id '{}': product does not exist", crudOperation, id);

            throw new CrudOperationException(
                    crudOperation,
                    String.format("Product with id '%s' was not found", id));
        }
    }

    private boolean isTheUserIsTheCreator(String id, UserDetails user) {
        Optional<Product> dbProduct = productRepository.findById(id);

        if (dbProduct.isPresent()) {
            UserDetails actualProductCreator = dbProduct.get().getCreator();

            boolean isTheUserIsTheCreator = actualProductCreator.equals(user);
            log.info("Is the user is the creator: {}", isTheUserIsTheCreator);

            return isTheUserIsTheCreator;
        }
        log.info("User {} trying to delete product with id '{}' is not the product's creator", user, id);
        return false;
    }

    public Product saveProduct(Product product) {
        ProductValidator.validate(product);

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteById(String procuctId, UserDetails user) throws CrudOperationException, UserAuthorisationException {
        log.info("Deleting product with id '{}'", procuctId);

        checkIfProductExist(!productRepository.existsById(procuctId), procuctId, Operations.DELETE);
        checkIfUserIsTheCreator(procuctId, user);

        productRepository.deleteById(procuctId);
    }

    private void checkIfUserIsTheCreator(String id, UserDetails user) throws CrudOperationException, UserAuthorisationException {
        if (!isTheUserIsTheCreator(id, user)) {
            throw new UserAuthorisationException("Looks like you are not the creator");
        }
    }

    public Product getProductById(String id) throws CrudOperationException {
        Optional<Product> dbProduct = productRepository.findById(id);

        checkIfProductExist(dbProduct.isEmpty(), id, Operations.READ);

        return dbProduct.get();
    }

    public Product update(Product product, String id, UserDetails user) throws CrudOperationException, UserAuthorisationException {
        Optional<Product> savedProduct = productRepository.findById(product.getId());

        checkIfProductExist(savedProduct.isEmpty(), id, Operations.UPDATE);
        checkIfUserIsTheCreator(id, user);

        return productRepository.save(product);
    }
}
