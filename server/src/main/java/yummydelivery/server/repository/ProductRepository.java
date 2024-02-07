package yummydelivery.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.Product;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT f FROM FoodEntity f WHERE f.foodTypeEnum = :foodType")
    Page<FoodEntity> findAllByProductTypePageable(@Param("foodType") FoodTypeEnum foodTypeEnum, Pageable pageable);

    @Query("SELECT b FROM BeverageEntity b WHERE b.productType = 'BEVERAGE'")
    Page<BeverageEntity> findAllBeveragesPageable(Pageable pageable);

    Optional<Product> findByName(String name);
}
