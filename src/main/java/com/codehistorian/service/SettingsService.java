package com.codehistorian.service;

import com.codehistorian.model.AppSettings;
import com.codehistorian.storage.AppDataLocator;
import com.codehistorian.storage.JsonCache;

import java.io.IOException;
import java.nio.file.Path;

public class SettingsService {

    private final AppDataLocator locator = new AppDataLocator();
    private final JsonCache jsonCache = new JsonCache();

    public Path settingsFile() {
        return locator.settingsFile();
    }

    public AppSettings load() {
        Path file = locator.settingsFile();
        if (jsonCache.exists(file)) {
            try {
                return jsonCache.read(file, AppSettings.class);
            } catch (IOException e) {
                return new AppSettings();
            }
        }
        return new AppSettings();
    }

    public void save(AppSettings settings) throws IOException {
        jsonCache.write(locator.settingsFile(), settings);
    }

    public String resolveAnthropicApiKey() {
        String saved = load().getAnthropicApiKey();
        if (saved != null && !saved.isBlank()) {
            return saved;
        }
        return System.getenv("ANTHROPIC_API_KEY");
    }
}
