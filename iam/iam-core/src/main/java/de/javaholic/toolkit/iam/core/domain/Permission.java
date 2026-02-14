package de.javaholic.toolkit.iam.core.domain;

import java.util.Objects;
/**
 * Domain representation of a permission.
 *
 * <p>Permissions are identified by stable string codes
 * (e.g. {@code "user.manage"}, {@code "config.read"}).</p>
 *
 * <p>They are domain data, not enums, to allow extensibility
 * without code changes.</p>
 */
public final class Permission {

    private String code;

    public Permission() {
    }

    public Permission(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    /**
     * Equality is based on permission code, which is the stable identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission that)) {
            return false;
        }
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
