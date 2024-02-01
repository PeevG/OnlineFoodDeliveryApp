package yummydelivery.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.enums.OrderStatusEnum;
import yummydelivery.server.model.OrderEntity;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusOrderByCreatedOnDesc(OrderStatusEnum orderStatusEnum);

    @Query("SELECT uo FROM UserEntity u JOIN u.orders uo WHERE u.id =:userId ORDER BY uo.createdOn DESC")
    Page<OrderEntity> findAllByUserId(@Param("userId")Long userId, Pageable pageable);
}
