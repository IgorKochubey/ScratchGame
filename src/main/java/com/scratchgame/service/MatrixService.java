package com.scratchgame.service;

import com.scratchgame.domain.config.Config;
import com.scratchgame.domain.config.Probabilities;
import com.scratchgame.domain.config.StandardSymbolProbability;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatrixService {
    private final Config config;
    private final Random random;

    @Getter
    private String[][] matrix;
    @Getter
    private String appliedBonusSymbol;

    public MatrixService(Config config, Random random) {
        this.config = config;
        this.random = random;
    }

    public void generateMatrix() {
        int rows = config.getRows();
        int columns = config.getColumns();
        this.matrix = new String[rows][columns];

        Probabilities probabilities = config.getProbabilities();
        insertStandard(probabilities, rows, columns);
        insertBonus(probabilities, rows, columns);
    }

    private void insertStandard(Probabilities probabilities, int rows, int columns) {
        List<String> standardSymbols = getStandardSymbols(probabilities);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int randomIndex = random.nextInt(standardSymbols.size());
                this.matrix[i][j] = standardSymbols.get(randomIndex);
            }
        }
    }

    private void insertBonus(Probabilities probabilities, int rows, int columns) {
        List<String> bonusSymbols = getBonusSymbols(probabilities);
        int randomBonusIndex = random.nextInt(bonusSymbols.size());
        String bonusSymbol = bonusSymbols.get(randomBonusIndex);

        int randomIndexI = random.nextInt(rows);
        int randomIndexJ = random.nextInt(columns);
        this.matrix[randomIndexI][randomIndexJ] = bonusSymbol;
        if (!"miss".equalsIgnoreCase(bonusSymbol)) {
            this.appliedBonusSymbol = bonusSymbol;
        }
    }

    private List<String> getBonusSymbols(Probabilities probabilities) {
        List<String> bonusSymbols = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : probabilities.getBonusSymbols().getSymbols().entrySet()) {
            String symbol = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                bonusSymbols.add(symbol);
            }
        }
        return bonusSymbols;
    }

    private List<String> getStandardSymbols(Probabilities probabilities) {
        List<String> standardSymbols = new ArrayList<>();
        for (StandardSymbolProbability probability : probabilities.getStandardSymbols()) {
            for (Map.Entry<String, Integer> entry : probability.getSymbols().entrySet()) {
                String symbol = entry.getKey();
                int count = entry.getValue();
                for (int i = 0; i < count; i++) {
                    standardSymbols.add(symbol);
                }
            }
        }
        return standardSymbols;
    }
}
