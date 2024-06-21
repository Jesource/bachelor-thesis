package vu.jesource.frontend.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vu.jesource.frontend.exception.AuthServiceException;
import vu.jesource.frontend.microservices.AuthMicroservice.LoginDetails;
import vu.jesource.frontend.models.Product;
import vu.jesource.frontend.models.RegistrationDetails;
import vu.jesource.frontend.service.ProductService;
import vu.jesource.frontend.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller("/")
public class AuthServiceController {

    private ProductService productService;
    private UserService userService;

    private String SESSION_LOGIN_ERROR_MESSAGE_KEY = "errorMessage";
    private String SESSION_JWT_TOKEN_KEY = "jwtToken";

    @Autowired
    public AuthServiceController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }


    @GetMapping("hi")
    public String sayHello() {
        log.info("Tried to access welcoming endpoint");

        return "redirect:/registration";
    }

    @GetMapping("sup")
    public String welcome() {
        return "home";
    }

    @GetMapping("create-item")
    public String createItem(Model model, HttpSession session) {

        String token = (String) session.getAttribute(SESSION_JWT_TOKEN_KEY);

        if (token == null) {
            session.setAttribute(SESSION_LOGIN_ERROR_MESSAGE_KEY, "Missing token, please login");
            return "redirect:/login";
        }

        model.addAttribute("JwtToken", token);
        model.addAttribute(new Product());
        model.addAttribute("optionalParams", new HashMap<String, String>());

        return "item-form";
    }

    @PostMapping("/submitProduct")
    public String createProduct(Product product, @RequestParam String jwt_token,
                                                @RequestParam Map<String, String> allParams, Model model) {
        System.out.println(jwt_token);
        try {
            productService.save(product, allParams, jwt_token);
        } catch (AuthServiceException e) {
            log.warn("Auth service exception: {}", e.getMessage());
            return "redirect:/login";
        }

        return "redirect:/products";
    }

    @GetMapping("registration")
    public String getRegistrationForm(Model model) {
        model.addAttribute(new RegistrationDetails());

        return "registration_form";
    }

    @PostMapping("registration")
    public String register(RegistrationDetails registrationDetails) {
        log.info("Got registration details: {}", registrationDetails);

        userService.saveUser(registrationDetails);

        return "redirect:/login";
    }

    @GetMapping("login")
    public String getLoginPage(Model model, HttpSession session) {
        model.addAttribute(new LoginDetails());

        String errorMessage = (String) session.getAttribute(SESSION_LOGIN_ERROR_MESSAGE_KEY);
        if (errorMessage != null) {
            model.addAttribute(SESSION_LOGIN_ERROR_MESSAGE_KEY, errorMessage);
            session.removeAttribute(SESSION_LOGIN_ERROR_MESSAGE_KEY);
        }

        return "login";
    }

    @PostMapping("login")
    public String doLogin(LoginDetails loginDetails, HttpSession session) {
        log.info("Got login details: {}", loginDetails);

        String token = null;
        try {
            token = userService.getJwtForUser(loginDetails);
            session.setAttribute(SESSION_JWT_TOKEN_KEY, token);
            return "redirect:/create-item";
        } catch (AuthServiceException e) {
            log.info("Failed to login: {}", e.getMessage());
            session.setAttribute(SESSION_LOGIN_ERROR_MESSAGE_KEY, e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("products")
    public String getAllProducts(Model model) {

        model.addAttribute("products", productService.getAllProducts());

        return "products";
    }

    @GetMapping("/products/{productId}")
    public String showProductDetails(@PathVariable String productId, Model model) {
        // Find the product with the specified ID (you can replace this with your data retrieval logic)
        Product product = productService.findProductById(productId);

        // Create a Thymeleaf template for displaying a "Product not found" message
        if (product != null) {
            model.addAttribute("product", product);
        }

        return "product"; // This is the name of your Thymeleaf template
    }
}
