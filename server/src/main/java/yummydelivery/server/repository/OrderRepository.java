package yummydelivery.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yummydelivery.server.enums.OrderStatusEnum;
import yummydelivery.server.model.OrderEntity;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusOrderByCreatedOnDesc(OrderStatusEnum orderStatusEnum);
}
