package de.javaholic.toolkit.i18n.spring;

import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.I18nProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public final class MessageSourceI18nProvider implements I18nProvider {

    private final MessageSource messageSource;
    private final Locale locale;

    public MessageSourceI18nProvider(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    public boolean contains(String key) {
        try {
            messageSource.getMessage(key, null, locale);
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }

    @Override
    public I18n get(){
        return new I18n() {
            @Override
            public String text(String key) {
                return messageSource.getMessage(key, null, locale);
            }
        };
    }
}
