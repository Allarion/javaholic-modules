package de.javaholic.toolkit.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFileI18nProviderTest {

    @Test
    void resolvesFromClasspathWithLocaleFallback() {
        LocalFileI18nProvider provider = new LocalFileI18nProvider("i18n/messages");

        assertThat(provider.resolve("greeting", Locale.GERMANY)).hasValue("Guten Tag");
        assertThat(provider.resolve("languageOnly", Locale.GERMANY)).hasValue("Deutsch");
        assertThat(provider.resolve("defaultOnly", Locale.GERMANY)).hasValue("Default");
        assertThat(provider.resolve("missing", Locale.GERMANY)).isEmpty();
    }

    @Test
    void resolvesFromFileSystemWithLocaleFallback(@TempDir Path tempDir) throws IOException {
        write(tempDir.resolve("bundle.properties"), "greeting=Default");
        write(tempDir.resolve("bundle_de.properties"), "greeting=Deutsch");
        write(tempDir.resolve("bundle_de_DE.properties"), "greeting=Bundesrepublik");

        LocalFileI18nProvider provider = new LocalFileI18nProvider(tempDir.resolve("bundle").toString());

        assertThat(provider.resolve("greeting", Locale.GERMANY)).hasValue("Bundesrepublik");
        assertThat(provider.resolve("greeting", Locale.GERMAN)).hasValue("Deutsch");
        assertThat(provider.resolve("greeting", Locale.US)).hasValue("Default");
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }
}
