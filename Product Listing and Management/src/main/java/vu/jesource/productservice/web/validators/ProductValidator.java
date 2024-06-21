package vu.jesource.productservice.web.validators;

import vu.jesource.productservice.web.models.Product;
import vu.jesource.productservice.web.models.Product.OptionalParameter;
import vu.jesource.productservice.web.models.Product.UserDetails;

import java.util.Set;

public class ProductValidator {
    public static int MAX_OPTIONAL_PARAMS_COUNT = 25;

    public static void validate(Product product) throws IllegalArgumentException {
        validateTitle(product.getTitle());
        validateDescription(product.getDescription());
        validateCondition(product.getCondition());
        validateOptionalParams(product.getOptionalParameters());
        validateCreator(product.getCreator());
    }

    private static void validateCreator(UserDetails creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Product cannot be created by anonymous user");
        }

        if (!creator.isValid()) {
            throw new IllegalArgumentException("Creator is invalid");
        }
    }

    private static void validateOptionalParams(Set<OptionalParameter> optionalParameters) {
        if (optionalParameters == null) {
            return;
        }

        if (optionalParameters.size() > MAX_OPTIONAL_PARAMS_COUNT) {
            throw new IllegalArgumentException(String.format(
                    "Item cannot have more than %d parameters",
                    MAX_OPTIONAL_PARAMS_COUNT));
        }
    }

    private static void validateCondition(Product.Conditions condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be empty");
        }
    }

    private static void validateDescription(String description) {
        // No requirements for description, can be empty
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (title.replaceAll(" ", "").length() < 3) {
            throw new IllegalArgumentException("Title cannot be shorter than 3 characters");
        }
    }

    private static void validateId(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
    }
}
