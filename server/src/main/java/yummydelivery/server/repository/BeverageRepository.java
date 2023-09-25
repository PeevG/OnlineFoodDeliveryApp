package yummydelivery.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yummydelivery.server.model.BeverageEntity;

@Repository
public interface BeverageRepository extends JpaRepository<BeverageEntity, Long> {
}
