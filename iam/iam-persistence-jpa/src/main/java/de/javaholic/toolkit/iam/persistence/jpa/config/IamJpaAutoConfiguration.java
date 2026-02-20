package de.javaholic.toolkit.iam.persistence.jpa.config;

import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainPermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainRoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainUserStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
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
@ConditionalOnClass(UserStore.class)
public class IamJpaAutoConfiguration {

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
    @ConditionalOnMissingBean(UserStore.class)
    public UserStore userStore(JpaDomainUserStore store) {
        return store;
    }

    @Bean
    @ConditionalOnMissingBean(RoleStore.class)
    public RoleStore roleStore(JpaDomainRoleStore store) {
        return store;
    }

    @Bean
    @ConditionalOnMissingBean(PermissionStore.class)
    public PermissionStore permissionStore(JpaDomainPermissionStore store) {
        return store;
    }



}
