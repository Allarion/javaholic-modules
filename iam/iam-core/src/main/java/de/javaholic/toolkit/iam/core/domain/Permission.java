package de.javaholic.toolkit.iam.core.domain;

import java.util.Objects;

public final class Permission {

    private final String code;

    public Permission(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    public String getCode() {
        return code;
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
