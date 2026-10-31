package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Читає версію, автоматично підставлену Maven з pom.xml під час збірки. */
final class BuildVersion {

    private BuildVersion() {
    }

    static String current() {
        Properties properties = new Properties();
        try (InputStream input = BuildVersion.class.getResourceAsStream("/build.properties")) {
            if (input == null) {
                throw new IllegalStateException("Не знайдено build.properties; зберіть програму через Maven");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Не вдалося прочитати версію програми", exception);
        }

        String version = properties.getProperty("application.version");
        if (version == null || version.isBlank()) {
            throw new IllegalStateException("У build.properties не задано версію програми");
        }
        return version;
    }
}
