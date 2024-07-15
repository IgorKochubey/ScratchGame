package com.scratchgame.service;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineServiceTest {

    @Test
    public void testParse_LongValidArguments() throws ParseException {
        CommandLineService commandLineService = new CommandLineService();
        String[] args = {"-config", "config.json", "-betting-amount", "100"};

        commandLineService.parse(args);

        assertEquals("config.json", commandLineService.getConfigFilePath());
        assertEquals(100, commandLineService.getBettingAmount());
    }

    @Test
    public void testParse_ShortValidArguments() throws ParseException {
        CommandLineService commandLineService = new CommandLineService();
        String[] args = {"-c", "config.json", "-b", "100"};

        commandLineService.parse(args);

        assertEquals("config.json", commandLineService.getConfigFilePath());
        assertEquals(100, commandLineService.getBettingAmount());
    }

    @Test
    public void testParse_MissingConfigArgument() {
        CommandLineService commandLineService = new CommandLineService();
        String[] args = {"-b", "100"};

        Exception exception = assertThrows(ParseException.class, () -> commandLineService.parse(args));

        String expectedMessage = "Missing required option: c";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testParse_MissingBettingAmountArgument() {
        CommandLineService commandLineService = new CommandLineService();
        String[] args = {"-c", "config.json"};

        Exception exception = assertThrows(ParseException.class, () -> commandLineService.parse(args));

        String expectedMessage = "Missing required option: b";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testParse_InvalidBettingAmountArgument() {
        CommandLineService commandLineService = new CommandLineService();
        String[] args = {"-c", "config.json", "-b", "invalid"};

        Exception exception = assertThrows(NumberFormatException.class, () -> commandLineService.parse(args));

        String expectedMessage = "For input string: \"invalid\"";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
