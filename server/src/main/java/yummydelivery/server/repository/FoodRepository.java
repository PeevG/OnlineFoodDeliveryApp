package yummydelivery.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.model.FoodEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    Optional<List<FoodEntity>> findAllByFoodTypeEnum(FoodTypeEnum foodType);
}
