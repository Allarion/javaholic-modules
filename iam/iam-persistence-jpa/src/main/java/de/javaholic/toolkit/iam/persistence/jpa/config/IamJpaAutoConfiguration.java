package de.javaholic.toolkit.iam.persistence.jpa.config;

import de.javaholic.toolkit.iam.core.spi.PermissionFormStore;
import de.javaholic.toolkit.iam.core.spi.RoleFormStore;
import de.javaholic.toolkit.iam.core.spi.UserFormStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainPermissionFormStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainRoleFormStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainUserFormStore;
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
@ConditionalOnClass(UserFormStore.class)
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
    public JpaDomainUserFormStore jpaDomainUserStore(JpaUserRepository userRepository, JpaUserMapper userMapper) {
        return new JpaDomainUserFormStore(userRepository, userMapper);
    }

    @Bean
    public JpaDomainRoleFormStore jpaDomainRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        return new JpaDomainRoleFormStore(roleRepository, roleMapper);
    }

    @Bean
    public JpaDomainPermissionFormStore jpaDomainPermissionStore(JpaPermissionRepository permissionRepository, JpaPermissionMapper permissionMapper) {
        return new JpaDomainPermissionFormStore(permissionRepository, permissionMapper);
    }

    @Bean
    @ConditionalOnMissingBean(UserFormStore.class)
    public UserFormStore userStore(JpaDomainUserFormStore store) {
        return store;
    }

    @Bean
    @ConditionalOnMissingBean(RoleFormStore.class)
    public RoleFormStore roleStore(JpaDomainRoleFormStore store) {
        return store;
    }

    @Bean
    @ConditionalOnMissingBean(PermissionFormStore.class)
    public PermissionFormStore permissionStore(JpaDomainPermissionFormStore store) {
        return store;
    }



}
