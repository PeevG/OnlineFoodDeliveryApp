package yummydelivery.server.utils;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import yummydelivery.server.model.Product;
import yummydelivery.server.repository.ProductRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommonUtils {
    private final ProductRepository productRepository;

    public CommonUtils(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String collectErrorMessagesToString(BindingResult bindingResult) {
        return bindingResult
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }

    public boolean productWithThisNameExist(String name) {
        Optional<Product> product = productRepository.findByName(name);
        return product.isPresent();
    }
}
