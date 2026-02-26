package de.javaholic.toolkit.iam.persistence.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.core.spi.UserFormStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class FileUserFormStore implements UserFormStore {

    private static final String DEFAULT_RESOURCE = "iam/users.yaml";

    private final List<User> users;
    private final Map<String, User> usersByUsername;

    public FileUserFormStore() {
        this(DEFAULT_RESOURCE, new ObjectMapper(new YAMLFactory()));
    }

    public FileUserFormStore(String resourcePath) {
        this(resourcePath, new ObjectMapper(new YAMLFactory()));
    }

    public FileUserFormStore(String resourcePath, ObjectMapper mapper) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        Objects.requireNonNull(mapper, "mapper");
        UsersFile data = load(resourcePath, mapper);
        Map<String, Role> rolesByName = buildRoles(data.roles);
        this.users = Collections.unmodifiableList(buildUsers(data.users, rolesByName));
        this.usersByUsername = indexUsers(this.users);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findById(UUID id) {
        throw new UnsupportedOperationException("unimplemented");
    }

    private UsersFile load(String resourcePath, ObjectMapper mapper) {
        try (InputStream inputStream = FileUserFormStore.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }
            UsersFile data = mapper.readValue(inputStream, UsersFile.class);
            return data != null ? data : new UsersFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load IAM users from " + resourcePath, e);
        }
    }

    private Map<String, Role> buildRoles(Map<String, RoleEntry> roleEntries) {
        Map<String, Role> rolesByName = new HashMap<>();
        Map<String, RoleEntry> entries = roleEntries != null ? roleEntries : Collections.emptyMap();
        for (Map.Entry<String, RoleEntry> entry : entries.entrySet()) {
            String roleName = entry.getKey();
            RoleEntry roleEntry = entry.getValue();
            Set<Permission> permissions = new HashSet<>();
            if (roleEntry != null && roleEntry.permissions != null) {
                for (String permission : roleEntry.permissions) {
                    permissions.add(new Permission(permission));
                }
            }
            rolesByName.put(roleName, new Role(roleName, permissions));
        }
        return rolesByName;
    }

    private List<User> buildUsers(List<UserEntry> userEntries, Map<String, Role> rolesByName) {
        List<UserEntry> entries = userEntries != null ? userEntries : Collections.emptyList();
        List<User> result = new ArrayList<>(entries.size());
        for (UserEntry entry : entries) {
            if (entry == null) {
                continue;
            }
            String username = Objects.requireNonNull(entry.username, "username");
            UUID id = entry.id != null ? entry.id : defaultIdFor(username);
            UserStatus status = entry.status != null ? UserStatus.valueOf(entry.status) : UserStatus.ACTIVE;
            Set<Role> roles = resolveRoles(entry.roles, rolesByName);
            result.add(new User(id, username, status, roles));
        }
        return result;
    }

    private Map<String, User> indexUsers(List<User> users) {
        Map<String, User> result = new HashMap<>();
        for (User user : users) {
            result.put(user.getUsername(), user);
        }
        return result;
    }

    private Set<Role> resolveRoles(Set<String> roleNames, Map<String, Role> rolesByName) {
        Set<String> names = roleNames != null ? roleNames : Collections.emptySet();
        Set<Role> roles = new HashSet<>();
        for (String roleName : names) {
            Role role = rolesByName.get(roleName);
            if (role == null) {
                throw new IllegalStateException("Role not defined: " + roleName);
            }
            roles.add(role);
        }
        return roles;
    }

    private UUID defaultIdFor(String username) {
        return UUID.nameUUIDFromBytes(("user:" + username).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public User save(User user) {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException("unimplemented");
    }

    private static final class UsersFile {
        public List<UserEntry> users = new ArrayList<>();
        public Map<String, RoleEntry> roles = new HashMap<>();
    }

    private static final class UserEntry {
        public UUID id;
        public String username;
        public String status;
        public Set<String> roles = new HashSet<>();
    }

    private static final class RoleEntry {
        public Set<String> permissions = new HashSet<>();
    }
}
