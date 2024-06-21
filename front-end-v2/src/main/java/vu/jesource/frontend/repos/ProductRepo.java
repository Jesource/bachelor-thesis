package vu.jesource.frontend.repos;

import vu.jesource.frontend.exception.CrudOperationException;
import vu.jesource.frontend.models.Product;

public interface ProductRepo {
    void save(Product product);
    void edit(Product product);
    Product get(String id) throws CrudOperationException;
    void delete(String id);
}
