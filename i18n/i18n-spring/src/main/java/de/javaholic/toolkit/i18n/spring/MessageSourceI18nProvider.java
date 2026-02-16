package de.javaholic.toolkit.i18n.spring;

import de.javaholic.toolkit.i18n.I18nProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;
import java.util.Optional;
import java.util.Objects;

public final class MessageSourceI18nProvider implements I18nProvider {

    private final MessageSource messageSource;
    private final Locale fallbackLocale;

    public MessageSourceI18nProvider(MessageSource messageSource, Locale fallbackLocale) {
        this.messageSource = Objects.requireNonNull(messageSource, "messageSource");
        this.fallbackLocale = Objects.requireNonNull(fallbackLocale, "fallbackLocale");
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        try {
            Locale effectiveLocale = locale != null ? locale : fallbackLocale;
            return Optional.of(messageSource.getMessage(key, null, effectiveLocale));
        } catch (NoSuchMessageException e) {
            return Optional.empty();
        }
    }
}
