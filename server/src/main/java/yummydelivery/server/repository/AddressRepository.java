package yummydelivery.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yummydelivery.server.dto.view.AddressView;
import yummydelivery.server.model.AddressEntity;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    @Query("SELECT NEW yummydelivery.server.dto.view.AddressView(ua.id, ua.city, ua.phoneNumber, ua.streetName, ua.streetNumber) FROM UserEntity u JOIN u.addresses ua WHERE u.id = :userId")
    Page<AddressView> findAllAddressesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT NEW yummydelivery.server.model.AddressEntity(ua.id, ua.city, ua.streetName, ua.streetNumber, ua.phoneNumber) FROM UserEntity u JOIN u.addresses ua WHERE u.email = :userName")
    List<AddressEntity> getAddressEntitiesByUsername(@Param("userName") String userName);
}
