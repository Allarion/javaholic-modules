package de.javaholic.toolkit.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * File-based {@link I18n} implementation.
 *
 * <p>Reads a single locale-specific file. Missing keys return {@code ??key??}.</p>
 *
 * <pre>{@code
 * I18n i18n = new FileI18n(Locale.ENGLISH, Path.of("i18n/messages_en.properties"));
 * i18n.text("user.form.name.field.label");
 * }</pre>
 */
public final class FileI18n implements I18n {

    private final Locale locale;
    private final Map<String, String> entries;

    public FileI18n(Locale locale, Path file) {
        this.locale = Objects.requireNonNull(locale, "locale");
        this.entries = load(Objects.requireNonNull(file, "file"));
    }

    public Locale locale() {
        return locale;
    }

    @Override
    public String text(String key) {
        String value = entries.get(key);
        return value != null ? value : "??" + key + "??";
    }

    private static Map<String, String> load(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        try {
            if (name.endsWith(".properties")) {
                return loadProperties(file);
            }
            if (name.endsWith(".yaml") || name.endsWith(".yml")) {
                return loadYaml(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Unsupported i18n file type: " + file);
    }

    private static Map<String, String> loadProperties(Path file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String name : props.stringPropertyNames()) {
            map.put(name, props.getProperty(name));
        }
        return map;
    }

    private static Map<String, String> loadYaml(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return parseFlatYaml(reader);
        }
    }

    private static Map<String, String> parseFlatYaml(BufferedReader reader) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int idx = trimmed.indexOf(':');
            if (idx <= 0) {
                continue;
            }
            String key = trimmed.substring(0, idx).trim();
            String value = trimmed.substring(idx + 1).trim();
            value = stripQuotes(value);
            map.put(key, value);
        }
        return map;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
