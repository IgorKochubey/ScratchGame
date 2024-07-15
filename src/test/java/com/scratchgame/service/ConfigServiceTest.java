package com.scratchgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.domain.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {
    private final String VALID_CONFIG_PATH = "src/test/resources/test_config.json";

    private ObjectMapper objectMapper;
    private ConfigService configService;

    @BeforeEach
    public void setUp() {
        objectMapper = Mockito.mock(ObjectMapper.class);
        configService = new ConfigService(objectMapper);
    }

    @Test
    public void testGetConfig_ReadValidConfigFile() throws IOException {
        Config expectedConfig = new Config();
        File file = new File(VALID_CONFIG_PATH);

        when(objectMapper.readValue(file, Config.class))
                .thenReturn(expectedConfig);

        Config actualConfig = configService.getConfig(VALID_CONFIG_PATH);

        assertNotNull(actualConfig);
        assertEquals(expectedConfig, actualConfig);
        verify(objectMapper, times(1)).readValue(file, Config.class);
    }

    @Test
    public void testGetConfig_FileNotFound() {
        String configFilePath = "src/test/resources/non_existing_config.json";
        File file = Mockito.mock(File.class);

        when(file.exists()).thenReturn(false);
        when(file.getPath()).thenReturn(configFilePath);

        Exception exception = assertThrows(IOException.class, () -> configService.getConfig(configFilePath));

        assertTrue(exception.getMessage().contains(configFilePath));
    }

    @Test
    public void testGetConfig_InvalidJson() throws IOException {
        String configFilePath = "src/test/resources/invalid_config.json";
        File file = new File(configFilePath);

        when(objectMapper.readValue(file, Config.class)).thenThrow(new IOException("Invalid JSON"));

        Exception exception = assertThrows(IOException.class, () -> configService.getConfig(configFilePath));

        assertTrue(exception.getMessage().contains("Invalid JSON"));
        verify(objectMapper, times(1)).readValue(file, Config.class);
    }

    @Test
    public void testConfigLoading() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Config config = mapper.readValue(new File(VALID_CONFIG_PATH), Config.class);

        assertNotNull(config);
        assertEquals(3, config.getColumns());
        assertEquals(3, config.getRows());

        assertTrue(config.getSymbols().containsKey("A"));
        assertTrue(config.getSymbols().containsKey("10x"));

        assertEquals(6, config.getProbabilities().getStandardSymbols().get(0).getSymbols().get("F"));
        assertEquals(1, config.getProbabilities().getBonusSymbols().getSymbols().get("10x"));

        assertTrue(config.getWinCombinations().containsKey("same_symbols_diagonally_left_to_right"));
    }
}
