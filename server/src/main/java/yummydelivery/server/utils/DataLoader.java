package yummydelivery.server.utils;

import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.*;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;


    public DataLoader(RoleRepository roleRepository, ProductRepository productRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() < 1) {
            RoleEntity customerRole = roleRepository.save(RoleEntity.builder().name(RoleEnum.CUSTOMER).build());
            RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name(RoleEnum.ADMIN).build());

            AddressEntity adminAddress = AddressEntity
                    .builder()
                    .city("Sofia")
                    .phoneNumber("08888881234")
                    .streetNumber("23")
                    .streetName("Vitosha street")
                    .build();

            AddressEntity customerAddress = AddressEntity
                    .builder()
                    .city("Sofia")
                    .phoneNumber("09998881234")
                    .streetNumber("33 B")
                    .streetName("Aleksandrovska street")
                    .build();

            AddressEntity customerSecondAddress = AddressEntity
                    .builder()
                    .city("Ahtopol")
                    .phoneNumber("09998881234")
                    .streetNumber("32 B")
                    .streetName("Saint Yani street")
                    .build();

            UserEntity customer = UserEntity
                    .builder()
                    .email("customer@abv.bg")
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .roles(Set.of(customerRole))
                    .password("$2a$10$d5wSM4U6MOUuYN0YvlJ3reAbYBBnrw5f6qvhf3RMwiqzUc8ffYWci")
                    .addresses(List.of(customerAddress, customerSecondAddress))
                    .cart(new ShoppingCartEntity())
                    .build();

            UserEntity admin = UserEntity
                    .builder()
                    .email("admin@abv.bg")
                    .firstName("Pesho")
                    .lastName("Dimitrov")
                    .roles(Set.of(adminRole))
                    .password("$2a$10$2sX1HlYhfnr0rcyYpMCJoucYKJXLG8n6VhIyk74SC0867lXznJkVO")
                    .addresses(List.of(adminAddress))
                    .build();

            addressRepository.saveAll(List.of(adminAddress, customerAddress));
            userRepository.saveAll(List.of(admin, customer));
        }
        loadNewFoodsAndBeverages();
    }
    public void loadNewFoodsAndBeverages() {
        if (productRepository.count() < 1) {
            BeverageEntity water = new BeverageEntity();
            water.setName("Devin");
            water.setMilliliters(500);
            water.setPrice(2.50);
            water.setImageURL("blabla");
            water.setProductType(ProductTypeEnum.BEVERAGE);

            BeverageEntity vodka = new BeverageEntity();
            vodka.setName("Flirt");
            vodka.setMilliliters(100);
            vodka.setPrice(4.50);
            vodka.setImageURL("blabla2");
            vodka.setProductType(ProductTypeEnum.BEVERAGE);

            FoodEntity marga = new FoodEntity();
            marga.setName("Margaritka");
            marga.setIngredients(List.of("Tomato Sauce", "Mozzarella", "Basil"));
            marga.setFoodTypeEnum(FoodTypeEnum.PIZZA);
            marga.setPrice(8.00);
            marga.setGrams(500);
            marga.setProductType(ProductTypeEnum.FOOD);
            marga.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

            FoodEntity cappri = new FoodEntity();
            cappri.setName("Kaprichozka");
            cappri.setIngredients(List.of("Tomato Sauce", "Mozzarella", "Cheese", "Baked Ham", "Mushroom", "Artichoke"));
            cappri.setFoodTypeEnum(FoodTypeEnum.PIZZA);
            cappri.setPrice(14.00);
            cappri.setGrams(700);
            cappri.setProductType(ProductTypeEnum.FOOD);
            cappri.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");
            productRepository.saveAll(List.of(marga, cappri, vodka, water));
        }
    }
}
