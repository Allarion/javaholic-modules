package de.javaholic.toolkit.iam.dto.config;

import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.dto.dto.PermissionDto;
import de.javaholic.toolkit.iam.dto.dto.RoleDto;
import de.javaholic.toolkit.iam.dto.dto.UserDto;
import de.javaholic.toolkit.iam.dto.mapper.PermissionDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.RoleDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.UserDtoMapper;
import de.javaholic.toolkit.iam.dto.store.PermissionDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.RoleDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.UserDtoCrudStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@AutoConfiguration
@ConditionalOnBean({UserStore.class, RoleStore.class, PermissionStore.class})
public class IamDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserDtoMapper userDtoMapper() {
        return new UserDtoMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionDtoMapper permissionDtoMapper() {
        return new PermissionDtoMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleDtoMapper roleDtoMapper(PermissionDtoMapper permissionDtoMapper) {
        return new RoleDtoMapper(permissionDtoMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "userCrudStore")
    public CrudStore<UserDto, UUID> userCrudStore(UserStore domainStore, UserDtoMapper userDtoMapper) {
        return new UserDtoCrudStore(domainStore, userDtoMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "roleCrudStore")
    public CrudStore<RoleDto, UUID> roleCrudStore(RoleStore domainStore, RoleDtoMapper roleDtoMapper) {
        return new RoleDtoCrudStore(domainStore, roleDtoMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "permissionCrudStore")
    public CrudStore<PermissionDto, UUID> permissionCrudStore(PermissionStore domainStore, PermissionDtoMapper permissionDtoMapper) {
        return new PermissionDtoCrudStore(domainStore, permissionDtoMapper);
    }
}
