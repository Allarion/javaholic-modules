package de.javaholic.toolkit.iam.application.spring.config;

import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainPermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainRoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.store.JpaDomainUserStore;
import de.javaholic.toolkit.iam.ui.dto.PermissionDto;
import de.javaholic.toolkit.iam.ui.dto.RoleDto;
import de.javaholic.toolkit.iam.ui.dto.UserDto;
import de.javaholic.toolkit.iam.ui.mapper.PermissionDtoMapper;
import de.javaholic.toolkit.iam.ui.mapper.RoleDtoMapper;
import de.javaholic.toolkit.iam.ui.mapper.UserDtoMapper;
import de.javaholic.toolkit.iam.ui.store.PermissionDtoCrudStore;
import de.javaholic.toolkit.iam.ui.store.RoleDtoCrudStore;
import de.javaholic.toolkit.iam.ui.store.UserDtoCrudStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class IamApplicationSpringConfig {

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
    public CrudStore<UserDto, UUID> userStore(JpaDomainUserStore domainStore, UserDtoMapper userDtoMapper) {
        return new UserDtoCrudStore(domainStore, userDtoMapper);
    }

    @Bean
    public CrudStore<RoleDto, UUID> roleStore(JpaDomainRoleStore domainStore, RoleDtoMapper roleDtoMapper) {
        return new RoleDtoCrudStore(domainStore, roleDtoMapper);
    }

    @Bean
    public CrudStore<PermissionDto, UUID> permissionStore(JpaDomainPermissionStore domainStore, PermissionDtoMapper permissionDtoMapper) {
        return new PermissionDtoCrudStore(domainStore, permissionDtoMapper);
    }
}
