package com.codehistorian.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonCache {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public <T> void write(Path file, T value) throws IOException {
        Files.createDirectories(file.getParent());
        objectMapper.writeValue(file.toFile(), value);
    }

    public <T> T read(Path file, Class<T> type) throws IOException {
        return objectMapper.readValue(file.toFile(), type);
    }

    public boolean exists(Path file) {
        return Files.exists(file);
    }
}
