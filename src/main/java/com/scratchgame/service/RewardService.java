package com.scratchgame.service;

import com.scratchgame.domain.config.Config;
import com.scratchgame.domain.config.Symbol;
import com.scratchgame.domain.config.WinCombination;
import lombok.Getter;

import java.util.*;


public class RewardService {
    private final Config config;
    private final MatrixService matrixService;

    @Getter
    private final Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
    @Getter
    private double reward = 0;

    public RewardService(Config config, MatrixService matrixService) {
        this.config = config;
        this.matrixService = matrixService;
    }

    public void calculateReward(int bettingAmount) {
        String[][] matrix = matrixService.getMatrix();
        boolean hasWinningCombination = false;

        for (Map.Entry<String, WinCombination> entry : config.getWinCombinations().entrySet()) {
            WinCombination winCombination = entry.getValue();
            String combinationName = entry.getKey();

            if (winCombination.getCoveredAreas() != null) {
                Set<String> symbols = new HashSet<>();
                for (String[] area : winCombination.getCoveredAreas()) {
                    String symbol = matchesPatternSymbol(matrix, area);
                    if (symbol != null) {
                        symbols.add(symbol);
                    }
                }

                if (symbols.size() == 1) {
                    String symbol = symbols.iterator().next();
                    hasWinningCombination = true;
                    if (!appliedWinningCombinations.containsKey(symbol)) {
                        appliedWinningCombinations.put(symbol, new ArrayList<>());
                    }
                    appliedWinningCombinations.get(symbol).add(combinationName);
                }
            } else if ("same_symbols".equals(winCombination.getWhen())) {
                Map<Integer, List<String>> countSymbols = countSymbols(matrix);
                if (countSymbols.containsKey(winCombination.getCount())) {
                    hasWinningCombination = true;
                    for (String symbol : countSymbols.get(winCombination.getCount())) {
                        if (!appliedWinningCombinations.containsKey(symbol)) {
                            appliedWinningCombinations.put(symbol, new ArrayList<>());
                        }
                        appliedWinningCombinations.get(symbol).add(combinationName);
                    }
                }
            }
        }

        if (hasWinningCombination) {
            for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
                List<String> values = entry.getValue();
                double rewardBySymbol = bettingAmount * config.getSymbols().get(entry.getKey()).getRewardMultiplier();
                for (String val : values) {
                    rewardBySymbol *= config.getWinCombinations().get(val).getRewardMultiplier();
                }
                reward += rewardBySymbol;
            }


            for (int i = 0; i < config.getRows(); i++) {
                for (int j = 0; j < config.getColumns(); j++) {
                    String cell = matrix[i][j];
                    if (config.getSymbols().containsKey(cell) && "bonus".equals(config.getSymbols().get(cell).getType())) {
                        reward = applyBonus(reward, cell);
                    }
                }
            }
        }
    }

    private String matchesPatternSymbol(String[][] matrix, String[] pattern) {
        String[] splitFirstPattern = pattern[0].split(":");
        String patternSymbol = matrix[Integer.parseInt(splitFirstPattern[0])][Integer.parseInt(splitFirstPattern[1])];

        for (String position : pattern) {
            String[] coords = position.split(":");
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);

            if (!matrix[row][col].equals(patternSymbol)) {
                return null;
            }
        }
        return patternSymbol;
    }

    private Map<Integer, List<String>> countSymbols(String[][] matrix) {
        Map<String, Integer> symbolCounts = new HashMap<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String symbol = matrix[i][j];
                symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
            }
        }

        Map<Integer, List<String>> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            if (!result.containsKey(entry.getValue())) {
                result.put(entry.getValue(), new ArrayList<>());
            }
            result.get(entry.getValue()).add(entry.getKey());
        }
        return result;
    }

    private double applyBonus(double reward, String bonus) {
        Symbol bonusSymbol = config.getSymbols().get(bonus);

        switch (bonusSymbol.getImpact()) {
            case "multiply_reward":
                return reward * bonusSymbol.getRewardMultiplier();
            case "extra_bonus":
                return reward + bonusSymbol.getExtra();
            case "miss":
                return reward;
            default:
                return reward;
        }
    }
}
