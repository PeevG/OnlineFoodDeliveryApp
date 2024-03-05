package yummydelivery.server.utils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

            loadCustomerAndAdminWithAddresses(customerRole, adminRole);
            loadFoodsAndBeverages();

            log.info("Test user with role Customer -> username: customer@abv.bg, password: slabaParola");
            log.info("Test user with role Admin -> username: admin@abv.bg, password: TestovaParola");
        }
    }

    private void loadFoodsAndBeverages() {
        if (productRepository.count() < 1) {
            loadPizzas();
            loadBeverages();
            loadSalads();
        }
    }

    private void loadSalads() {
        FoodEntity kobSalad = new FoodEntity();
        kobSalad.setName("Kob Salad");
        kobSalad.setIngredients(List.of("Shrimp, Fresh tomato, Onion, Egg"));
        kobSalad.setFoodTypeEnum(FoodTypeEnum.SALAD);
        kobSalad.setPrice(15.50);
        kobSalad.setGrams(350);
        kobSalad.setProductType(ProductTypeEnum.FOOD);
        kobSalad.setImageURL("SomeImageURL");

        FoodEntity saladWithGrilledVegetables = new FoodEntity();
        saladWithGrilledVegetables.setName("Salad with Grilled vegetables");
        saladWithGrilledVegetables.setIngredients(List.of("Mix of green salads, grilled eggplant, zucchini and mushrooms, roasted peppers, marinated cheese"));
        saladWithGrilledVegetables.setFoodTypeEnum(FoodTypeEnum.SALAD);
        saladWithGrilledVegetables.setPrice(14.50);
        saladWithGrilledVegetables.setGrams(450);
        saladWithGrilledVegetables.setProductType(ProductTypeEnum.FOOD);
        saladWithGrilledVegetables.setImageURL("SomeImageURL");

        FoodEntity burrata = new FoodEntity();
        burrata.setName("Burrata");
        burrata.setIngredients(List.of("Burrata cheese, prosciutto, cherry tomatoes, mixed salads, arugula, grilled zucchini and eggplant, capers, pesto sauce seasoned with soy mustard dressing"));
        burrata.setFoodTypeEnum(FoodTypeEnum.SALAD);
        burrata.setPrice(16.50);
        burrata.setGrams(450);
        burrata.setProductType(ProductTypeEnum.FOOD);
        burrata.setImageURL("SomeImageURL");

        FoodEntity caprese = new FoodEntity();
        caprese.setName("Caprese");
        caprese.setIngredients(List.of("Green Zebra tomatoes, mozzarella, pesto sauce"));
        caprese.setFoodTypeEnum(FoodTypeEnum.SALAD);
        caprese.setPrice(13.50);
        caprese.setGrams(450);
        caprese.setProductType(ProductTypeEnum.FOOD);
        caprese.setImageURL("SomeImageURL");

        productRepository.saveAll(List.of(caprese, burrata, saladWithGrilledVegetables, kobSalad));
        log.info("Salads populated successfully");
    }

    private void loadCustomerAndAdminWithAddresses(RoleEntity customerRole, RoleEntity adminRole) {
        AddressEntity adminAddress = AddressEntity
                .builder()
                .city("Sofia")
                .phoneNumber("08888881234")
                .streetNumber("23")
                .streetName("Vitosha street")
                .build();

        AddressEntity customerAddress = AddressEntity
                .builder()
                .city("Petrich")
                .phoneNumber("09998881234")
                .streetNumber("33 B")
                .streetName("Aleksandrovska street")
                .build();

        AddressEntity customerSecondAddress = AddressEntity
                .builder()
                .city("Burgas")
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
        addressRepository.saveAll(List.of(adminAddress, customerAddress, customerSecondAddress));
        log.info("Addresses populated successfully");
        userRepository.saveAll(List.of(admin, customer));
    }

    private void loadPizzas() {
        FoodEntity cappricciosa = new FoodEntity();
        cappricciosa.setName("Cappricciosa");
        cappricciosa.setIngredients(List.of("Homemade tomato sauce, mozzarella, ham, sausage, olives, mushrooms and artichoke"));
        cappricciosa.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        cappricciosa.setPrice(12.00);
        cappricciosa.setGrams(500);
        cappricciosa.setProductType(ProductTypeEnum.FOOD);
        cappricciosa.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

        FoodEntity romma = new FoodEntity();
        romma.setName("Romma");
        romma.setIngredients(List.of("Homemade tomato sauce, mozzarella, ham, fresh tomatoes, melted cheese and parmesan"));
        romma.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        romma.setPrice(12.50);
        romma.setGrams(550);
        romma.setProductType(ProductTypeEnum.FOOD);
        romma.setImageURL("SomeImageURL");

        FoodEntity hawai = new FoodEntity();
        hawai.setName("Hawai");
        hawai.setIngredients(List.of("Homemade tomato sauce, mozzarella, ham, pineapple"));
        hawai.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        hawai.setPrice(13.00);
        hawai.setGrams(600);
        hawai.setProductType(ProductTypeEnum.FOOD);
        hawai.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

        FoodEntity pulledBeef = new FoodEntity();
        pulledBeef.setName("Pulled Beef");
        pulledBeef.setIngredients(List.of("BBQ Sauce, Mozzarella, Cheddar Cheese, Egg, Caramelized onions"));
        pulledBeef.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        pulledBeef.setPrice(14.00);
        pulledBeef.setGrams(550);
        pulledBeef.setProductType(ProductTypeEnum.FOOD);
        pulledBeef.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

        FoodEntity freshGarden = new FoodEntity();
        freshGarden.setName("Fresh Garden");
        freshGarden.setIngredients(List.of("Tomato Sauce, Mozzarella, Fresh tomato, Fresh green peppers, Fresh mushrooms, Onion, Black Olives"));
        freshGarden.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        freshGarden.setPrice(14.00);
        freshGarden.setGrams(450);
        freshGarden.setProductType(ProductTypeEnum.FOOD);
        freshGarden.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

        FoodEntity extravaganza = new FoodEntity();
        extravaganza.setName("Extravaganza");
        extravaganza.setIngredients(List.of("Tomato Sauce, Mozzarella, Smoked Ham, Pepperoni, Fresh green peppers, Fresh mushrooms, Onion, Black Olives"));
        extravaganza.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        extravaganza.setPrice(14.00);
        extravaganza.setGrams(545);
        extravaganza.setProductType(ProductTypeEnum.FOOD);
        extravaganza.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");

        FoodEntity hamClassic = new FoodEntity();
        hamClassic.setName("Ham Classic");
        hamClassic.setIngredients(List.of("Tomato Sauce,Fresh green peppers, Mozzarella, Smoked Ham, Fresh mushrooms"));
        hamClassic.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        hamClassic.setPrice(9.00);
        hamClassic.setGrams(500);
        hamClassic.setProductType(ProductTypeEnum.FOOD);
        hamClassic.setImageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg");
        productRepository.saveAll(List.of(cappricciosa, romma, hawai, pulledBeef, freshGarden, extravaganza, hamClassic));
        log.info("Pizzas populated successfully");
    }

    private void loadBeverages() {
        BeverageEntity water = new BeverageEntity();
        water.setName("Devin");
        water.setMilliliters(500);
        water.setPrice(2.50);
        water.setImageURL("SomeImageURL");
        water.setProductType(ProductTypeEnum.BEVERAGE);

        BeverageEntity cocaCola = new BeverageEntity();
        cocaCola.setName("Coca COla");
        cocaCola.setMilliliters(200);
        cocaCola.setPrice(4.50);
        cocaCola.setImageURL("SomeImageURL");
        cocaCola.setProductType(ProductTypeEnum.BEVERAGE);

        BeverageEntity orangeJuice = new BeverageEntity();
        orangeJuice.setName("Orange Juice");
        orangeJuice.setMilliliters(300);
        orangeJuice.setPrice(7.50);
        orangeJuice.setImageURL("SomeImageURL");
        orangeJuice.setProductType(ProductTypeEnum.BEVERAGE);

        BeverageEntity appleJuice = new BeverageEntity();
        appleJuice.setName("Apple Juice");
        appleJuice.setMilliliters(300);
        appleJuice.setPrice(8.50);
        appleJuice.setImageURL("SomeImageURL");
        appleJuice.setProductType(ProductTypeEnum.BEVERAGE);

        productRepository.saveAll(List.of(appleJuice, orangeJuice, cocaCola, water));
        log.info("Beverages populated successfully");
    }
}
