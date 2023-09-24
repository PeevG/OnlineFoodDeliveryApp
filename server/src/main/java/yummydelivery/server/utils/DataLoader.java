package yummydelivery.server.utils;

import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.RoleEntity;
import yummydelivery.server.repository.FoodRepository;
import yummydelivery.server.repository.RoleRepository;

import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final FoodRepository foodRepository;

    public DataLoader(RoleRepository roleRepository, FoodRepository foodRepository) {
        this.roleRepository = roleRepository;
        this.foodRepository = foodRepository;
    }

    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() < 1) {
            roleRepository.save(RoleEntity.builder().name(RoleEnum.CUSTOMER).build());
            roleRepository.save(RoleEntity.builder().name(RoleEnum.ADMIN).build());
        }
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
