package com.codehistorian.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppDataLocator {

    public Path settingsDirectory() {
        String os = System.getProperty("os.name", "").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            Path base = appData != null
                    ? Paths.get(appData)
                    : Paths.get(System.getProperty("user.home"), "AppData", "Roaming");
            return base.resolve("GitLens");
        }

        if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "GitLens");
        }

        String xdgConfig = System.getenv("XDG_CONFIG_HOME");
        Path base = xdgConfig != null
                ? Paths.get(xdgConfig)
                : Paths.get(System.getProperty("user.home"), ".config");
        return base.resolve("GitLens");
    }

    public Path settingsFile() {
        return settingsDirectory().resolve("settings.json");
    }
}
