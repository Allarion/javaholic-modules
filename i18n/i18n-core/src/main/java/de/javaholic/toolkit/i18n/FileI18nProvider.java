package de.javaholic.toolkit.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


public final class FileI18nProvider implements I18nProvider {

    private final Map<String, String> entries;

    public FileI18nProvider(Path file) {
        this.entries = load(file);
    }

    @Override
    public boolean contains(String key) {
        return entries.containsKey(key);
    }

    @Override
    public I18n get(){
        return entries::get;
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
