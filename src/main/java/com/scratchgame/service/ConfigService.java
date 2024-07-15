package com.scratchgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.domain.config.Config;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@AllArgsConstructor
public class ConfigService {
    private final ObjectMapper objectMapper;

    public Config getConfig(String configFilePath) throws IOException {
        File file = new File(configFilePath);
        if (file.exists()) {
            return objectMapper.readValue(file, Config.class);
        }
        throw new FileNotFoundException(configFilePath);
    }
}
