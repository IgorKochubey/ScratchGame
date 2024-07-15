package com.scratchgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.domain.Result;
import com.scratchgame.domain.config.Config;
import com.scratchgame.service.*;

import java.io.IOException;
import java.util.Random;

public class Main {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            CommandLineService cmd = new CommandLineService();
            cmd.parse(args);
            int bettingAmount = cmd.getBettingAmount();
            String configFilePath = cmd.getConfigFilePath();

            Result result = getResult(configFilePath, bettingAmount);
            printResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Result getResult(String configFilePath, int bettingAmount) throws IOException {
        ConfigService configService = new ConfigService(objectMapper);
        Config config = configService.getConfig(configFilePath);

        Random random = new Random();
        MatrixService matrixService = new MatrixService(config, random);
        RewardService rewardService = new RewardService(config, matrixService);

        GameService gameService = new GameService(matrixService, rewardService);
        gameService.play(bettingAmount);
        return gameService.getResult();
    }

    private static void printResult(Result result) throws JsonProcessingException {
        String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        System.out.println(jsonResult);
    }
}
