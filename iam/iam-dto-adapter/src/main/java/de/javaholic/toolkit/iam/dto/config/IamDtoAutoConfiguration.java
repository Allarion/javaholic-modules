package de.javaholic.toolkit.iam.dto.config;

import de.javaholic.toolkit.iam.core.spi.PermissionFormStore;
import de.javaholic.toolkit.iam.core.spi.RoleFormStore;
import de.javaholic.toolkit.iam.core.spi.UserFormStore;
import de.javaholic.toolkit.iam.dto.mapper.PermissionFormDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.RoleFormDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.UserFormDtoMapper;
import de.javaholic.toolkit.iam.dto.spi.PermissionFormDtoStore;
import de.javaholic.toolkit.iam.dto.spi.RoleDtoStore;
import de.javaholic.toolkit.iam.dto.spi.UserFormDtoStore;
import de.javaholic.toolkit.iam.dto.store.PermissionFormDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.RoleFormDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.UserFormDtoCrudStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class IamDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserFormDtoMapper userDtoMapper(RoleFormDtoMapper roleFormDtoMapper) {
        return new UserFormDtoMapper(roleFormDtoMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionFormDtoMapper permissionDtoMapper() {
        return new PermissionFormDtoMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleFormDtoMapper roleDtoMapper(PermissionFormDtoMapper permissionFormDtoMapper) {
        return new RoleFormDtoMapper(permissionFormDtoMapper);
    }

    @Bean
    public UserFormDtoStore userDtoStore(UserFormStore domainStore, UserFormDtoMapper userFormDtoMapper) {
        return new UserFormDtoCrudStore(domainStore, userFormDtoMapper);
    }

    @Bean
    public RoleDtoStore roleDtoStore(RoleFormStore domainStore, RoleFormDtoMapper roleFormDtoMapper) {
        return new RoleFormDtoCrudStore(domainStore, roleFormDtoMapper);
    }

    @Bean
    public PermissionFormDtoStore permissionDtoStore(PermissionFormStore domainStore, PermissionFormDtoMapper permissionFormDtoMapper) {
        return new PermissionFormDtoCrudStore(domainStore, permissionFormDtoMapper);
    }
}
