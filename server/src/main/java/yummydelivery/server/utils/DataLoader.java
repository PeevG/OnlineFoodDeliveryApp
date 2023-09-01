package yummydelivery.server.utils;

import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.model.RoleEntity;
import yummydelivery.server.repository.RoleRepository;

@Component
public class DataLoader implements ApplicationRunner {
    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() < 1) {
            RoleEntity customerRole = roleRepository.save(RoleEntity.builder().name(RoleEnum.CUSTOMER).build());
            RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name(RoleEnum.ADMIN).build());
        }
    }
}
