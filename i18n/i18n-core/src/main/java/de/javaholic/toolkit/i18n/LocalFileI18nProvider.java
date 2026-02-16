package de.javaholic.toolkit.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LocalFileI18nProvider implements I18nProvider {

    private final String baseName;
    private final ClassLoader classLoader;
    private final ConcurrentMap<String, Optional<Properties>> cache = new ConcurrentHashMap<>();

    public LocalFileI18nProvider(String baseName) {
        this(baseName, Thread.currentThread().getContextClassLoader());
    }

    public LocalFileI18nProvider(String baseName, ClassLoader classLoader) {
        this.baseName = normalizeBaseName(Objects.requireNonNull(baseName, "baseName"));
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        for (String candidate : candidateBundles(locale)) {
            Optional<Properties> properties = cache.computeIfAbsent(candidate, this::loadProperties);
            if (properties.isPresent() && properties.get().containsKey(key)) {
                return Optional.ofNullable(properties.get().getProperty(key));
            }
        }
        return Optional.empty();
    }

    private Optional<Properties> loadProperties(String fileName) {
        Optional<InputStream> maybeInput = openFileSystem(fileName);
        if (maybeInput.isEmpty()) {
            maybeInput = openClasspath(fileName);
        }
        if (maybeInput.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream input = maybeInput.get()) {
            Properties properties = new Properties();
            properties.load(input);
            return Optional.of(properties);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<InputStream> openFileSystem(String fileName) {
        Path path = Path.of(fileName);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.newInputStream(path));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<InputStream> openClasspath(String fileName) {
        return Optional.ofNullable(classLoader.getResourceAsStream(fileName));
    }

    private Set<String> candidateBundles(Locale locale) {
        Set<String> result = new LinkedHashSet<>();
        if (!locale.toString().isBlank()) {
            result.add(baseName + "_" + locale + ".properties");
        }
        if (!locale.getLanguage().isBlank()) {
            result.add(baseName + "_" + locale.getLanguage() + ".properties");
        }
        result.add(baseName + ".properties");
        return result;
    }

    private static String normalizeBaseName(String baseName) {
        if (baseName.endsWith(".properties")) {
            return baseName.substring(0, baseName.length() - ".properties".length());
        }
        return baseName;
    }
}
