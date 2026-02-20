package de.javaholic.toolkit.iam.dto.config;

import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.dto.mapper.PermissionDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.RoleDtoMapper;
import de.javaholic.toolkit.iam.dto.mapper.UserDtoMapper;
import de.javaholic.toolkit.iam.dto.spi.PermissionDtoStore;
import de.javaholic.toolkit.iam.dto.spi.RoleDtoStore;
import de.javaholic.toolkit.iam.dto.spi.UserDtoStore;
import de.javaholic.toolkit.iam.dto.store.PermissionDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.RoleDtoCrudStore;
import de.javaholic.toolkit.iam.dto.store.UserDtoCrudStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class IamDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserDtoMapper userDtoMapper(RoleDtoMapper roleDtoMapper) {
        return new UserDtoMapper(roleDtoMapper);
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
    public UserDtoStore userDtoStore(UserStore domainStore, UserDtoMapper userDtoMapper) {
        return new UserDtoCrudStore(domainStore, userDtoMapper);
    }

    @Bean
    public RoleDtoStore roleDtoStore(RoleStore domainStore, RoleDtoMapper roleDtoMapper) {
        return new RoleDtoCrudStore(domainStore, roleDtoMapper);
    }

    @Bean
    public PermissionDtoStore permissionDtoStore(PermissionStore domainStore, PermissionDtoMapper permissionDtoMapper) {
        return new PermissionDtoCrudStore(domainStore, permissionDtoMapper);
    }
}
