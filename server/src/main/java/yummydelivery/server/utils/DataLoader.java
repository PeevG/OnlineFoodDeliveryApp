package yummydelivery.server.utils;

import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.*;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BeverageRepository beverageRepository;

    public DataLoader(RoleRepository roleRepository, FoodRepository foodRepository, UserRepository userRepository, AddressRepository addressRepository, BeverageRepository beverageRepository) {
        this.roleRepository = roleRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.beverageRepository = beverageRepository;
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
                    .streetNumber(23)
                    .street("Vitosha street")
                    .build();

            AddressEntity customerAddress = AddressEntity
                    .builder()
                    .city("Sofia")
                    .phoneNumber("09998881234")
                    .streetNumber(33)
                    .street("Aleksandrovska street")
                    .build();

            UserEntity customer = UserEntity
                    .builder()
                    .email("customer@abv.bg")
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .roles(Set.of(customerRole))
                    .password("$2a$10$d5wSM4U6MOUuYN0YvlJ3reAbYBBnrw5f6qvhf3RMwiqzUc8ffYWci")
                    .addresses(List.of(customerAddress))
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
        loadFoods();
        loadBeverages();

    }

    private void loadBeverages() {
        if (beverageRepository.count() < 1) {
            BeverageEntity coke = BeverageEntity.builder()
                    .milliliters(500)
                    .name("Coke")
                    .price(3.0)
                    .build();
            BeverageEntity water = BeverageEntity.builder()
                    .milliliters(500)
                    .name("Mineral Water")
                    .price(3.0)
                    .build();
            beverageRepository.saveAll(List.of(coke, water));
        }
    }

    private void loadFoods() {
        if (foodRepository.count() < 1) {
            FoodEntity margaritta = FoodEntity.builder()
                    .price(8.00)
                    .foodTypeEnum(FoodTypeEnum.PIZZA)
                    .weight(500)
                    .name("Margherita")
                    .imageURL("https://maisons.bg/burgas/wp-content/uploads/2020/03/Food-Burgas_90.jpg")
                    .ingredients(List.of("Tomato Sauce", "Mozzarella", "Basil"))
                    .build();

            FoodEntity capricciosa = FoodEntity.builder()
                    .price(14.50)
                    .foodTypeEnum(FoodTypeEnum.PIZZA)
                    .weight(600)
                    .name("Capricciosa")
                    .imageURL("https://wips.plug.it/cips/buonissimo.org/cms/2019/04/pizza-capricciosa.jpg?w=713&a=c&h=407")
                    .ingredients(List.of("Tomato Sauce", "Mozzarella", "Cheese", "Baked Ham", "Mushroom", "Artichoke"))
                    .build();
            foodRepository.saveAll(List.of(margaritta, capricciosa));
        }
    }
}
