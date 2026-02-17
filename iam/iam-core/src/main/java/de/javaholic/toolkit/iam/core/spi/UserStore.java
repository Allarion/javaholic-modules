package de.javaholic.toolkit.iam.core.spi;

/**
 * Combined read/write SPI for users.
 */
public interface UserStore extends UserQuery, UserCommand {
}
