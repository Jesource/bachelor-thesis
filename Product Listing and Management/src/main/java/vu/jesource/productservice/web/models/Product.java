package vu.jesource.productservice.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private String title;
    private String description;
    private Conditions condition;
    private double price;
    private Set<OptionalParameter> optionalParameters;
    private UserDetails creator;

    public Product(String id, String title, String description, Conditions condition, double price,
                   Set<OptionalParameter> optionalParameters, UserDetails creator) {
        setPrice(price);
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.optionalParameters = optionalParameters;
        this.creator = creator;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal to 0");
        }

        this.price = price;
    }

    public enum Conditions {
        NEW_WITH_TAGS,
        NEW_WITHOUT_TAGS,
        VERY_GOOD,
        GOOD,
        SATISFACTORY
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserDetails {
        private long userId;
        private String username;

        public boolean isValid() {
            return userId > 0 && (username != null && !username.isEmpty());
        }
    }

    @Data
    @AllArgsConstructor
    public static class OptionalParameter {
        private String name;
        private String value;

        @Override
        public String toString() {
            return String.format("(%s : %s)", name, value);
        }
    }
}

