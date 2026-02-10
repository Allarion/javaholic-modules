package de.javaholic.toolkit.config.ui;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties("fluent.fields")
public class FluentFieldProperties {

    private Map<String, String> mappings = new LinkedHashMap<>();

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
}
