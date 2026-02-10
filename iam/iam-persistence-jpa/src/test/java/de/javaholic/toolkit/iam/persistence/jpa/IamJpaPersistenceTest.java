package de.javaholic.toolkit.iam.persistence.jpa;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.persistence.jpa.config.IamJpaPersistenceConfig;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = IamJpaPersistenceTest.TestApp.class)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class IamJpaPersistenceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Autowired
    private JpaPermissionRepository permissionRepository;

    @Autowired
    private UserStore userStore;

    @Test
    void flywayMigrationCreatesTables() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from iam_user", Integer.class);
        assertNotNull(count);
    }

    @Test
    void loadsUserWithRolesAndPermissions() {
        JpaPermissionEntity manageUsers = new JpaPermissionEntity();
        manageUsers.setId(UUID.randomUUID());
        manageUsers.setCode("user.manage");
        permissionRepository.save(manageUsers);

        JpaRoleEntity adminRole = new JpaRoleEntity();
        adminRole.setId(UUID.randomUUID());
        adminRole.setName("ADMIN");
        adminRole.setPermissions(Set.of(manageUsers));
        roleRepository.save(adminRole);

        JpaUserEntity user = new JpaUserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("alice");
        user.setStatus(de.javaholic.toolkit.iam.core.domain.UserStatus.ACTIVE);
        user.setRoles(Set.of(adminRole));
        userRepository.save(user);

        User loaded = userStore.findByUsername("alice").orElseThrow();
        assertEquals("alice", loaded.getUsername());
        assertEquals(Set.of("ADMIN"),
            loaded.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()));
        Role role = loaded.getRoles().iterator().next();
        assertEquals(Set.of("user.manage"), role.getPermissions().stream().map(Permission::getCode)
            .collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void userStoreFindByUsernameWorks() {
        JpaUserEntity user = new JpaUserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("bob");
        user.setStatus(de.javaholic.toolkit.iam.core.domain.UserStatus.ACTIVE);
        userRepository.save(user);

        assertTrue(userStore.findByUsername("bob").isPresent());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(IamJpaPersistenceConfig.class)
    @EntityScan(basePackageClasses = JpaUserEntity.class)
    static class TestApp {
    }
}
