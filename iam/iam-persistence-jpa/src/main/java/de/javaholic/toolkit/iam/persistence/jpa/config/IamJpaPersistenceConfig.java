package de.javaholic.toolkit.iam.persistence.jpa.config;

import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.core.dto.PermissionDto;
import de.javaholic.toolkit.iam.core.dto.RoleDto;
import de.javaholic.toolkit.iam.core.dto.UserDto;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.PermissionDtoMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.RoleDtoMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.UserDtoMapper;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainPermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainRoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainUserStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.PermissionDtoCrudStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.RoleDtoCrudStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.UserDtoCrudStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.UUID;

@Configuration
@EnableJpaRepositories(basePackageClasses = {
    JpaUserRepository.class,
    JpaRoleRepository.class,
    JpaPermissionRepository.class
})
@EntityScan(basePackageClasses = {
    JpaUserEntity.class,
    JpaRoleEntity.class,
    JpaPermissionEntity.class
})
public class IamJpaPersistenceConfig {

    @Bean
    public JpaPermissionMapper jpaPermissionMapper() {
        return new JpaPermissionMapper();
    }

    @Bean
    public JpaRoleMapper jpaRoleMapper(JpaPermissionMapper permissionMapper) {
        return new JpaRoleMapper(permissionMapper);
    }

    @Bean
    public JpaUserMapper jpaUserMapper(JpaRoleMapper roleMapper) {
        return new JpaUserMapper(roleMapper);
    }

    @Bean
    public UserDtoMapper userDtoMapper() {
        return new UserDtoMapper();
    }

    @Bean
    public PermissionDtoMapper permissionDtoMapper() {
        return new PermissionDtoMapper();
    }

    @Bean
    public RoleDtoMapper roleDtoMapper(PermissionDtoMapper permissionDtoMapper) {
        return new RoleDtoMapper(permissionDtoMapper);
    }

    @Bean
    public JpaDomainUserStore jpaDomainUserStore(JpaUserRepository userRepository, JpaUserMapper userMapper) {
        return new JpaDomainUserStore(userRepository, userMapper);
    }

    @Bean
    public JpaDomainRoleStore jpaDomainRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        return new JpaDomainRoleStore(roleRepository, roleMapper);
    }

    @Bean
    public JpaDomainPermissionStore jpaDomainPermissionStore(JpaPermissionRepository permissionRepository, JpaPermissionMapper permissionMapper) {
        return new JpaDomainPermissionStore(permissionRepository, permissionMapper);
    }

    @Bean
    public UserStore userStore(JpaDomainUserStore userStore) {
        return userStore;
    }

    @Bean
    public CrudStore<UserDto, UUID> userDtoCrudStore(JpaDomainUserStore domainStore, UserDtoMapper mapper) {
        return new UserDtoCrudStore(domainStore, mapper);
    }

    @Bean
    public CrudStore<RoleDto, UUID> roleDtoCrudStore(JpaDomainRoleStore domainStore, RoleDtoMapper mapper) {
        return new RoleDtoCrudStore(domainStore, mapper);
    }

    @Bean
    public CrudStore<PermissionDto, UUID> permissionDtoCrudStore(JpaDomainPermissionStore domainStore, PermissionDtoMapper mapper) {
        return new PermissionDtoCrudStore(domainStore, mapper);
    }

    @Bean
    public RoleStore roleStore(JpaDomainRoleStore roleStore) {
        return roleStore;
    }

    @Bean
    public PermissionStore permissionStore(JpaDomainPermissionStore permissionStore) {
        return permissionStore;
    }
}
