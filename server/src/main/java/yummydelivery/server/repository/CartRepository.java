package yummydelivery.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yummydelivery.server.model.ShoppingCartEntity;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCartEntity, Long> {
   // Optional<CartEntity> findCartEntityByOwnerEmail(String email);
}
