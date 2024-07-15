package com.scratchgame.service;

import com.scratchgame.domain.config.BonusSymbolProbability;
import com.scratchgame.domain.config.Config;
import com.scratchgame.domain.config.Probabilities;
import com.scratchgame.domain.config.StandardSymbolProbability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

public class MatrixServiceTest {
    @Mock
    private Config config;

    @Mock
    private Random random;

    @InjectMocks
    private MatrixService matrixService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        matrixService = new MatrixService(config, random);
    }

    @Test
    public void testGenerateMatrix() {
        int rows = 3;
        int columns = 3;

        when(config.getRows()).thenReturn(rows);
        when(config.getColumns()).thenReturn(columns);

        Probabilities probabilities = getProbabilities("+1000");
        when(config.getProbabilities()).thenReturn(probabilities);
        when(random.nextInt(anyInt())).thenReturn(0);

        matrixService.generateMatrix();

        String[][] matrix = matrixService.getMatrix();
        assertNotNull(matrix);
        assertEquals(rows, matrix.length);
        assertEquals(columns, matrix[0].length);

        String[][] matrixExpected = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixExpected[i][j] = "A";
            }
        }
        matrixExpected[0][0] = "+1000";
        assertArrayEquals(matrixExpected, matrix);
    }

    @Test
    public void testInsertBonus() {
        int rows = 3;
        int columns = 3;

        when(config.getRows()).thenReturn(rows);
        when(config.getColumns()).thenReturn(columns);

        Probabilities probabilities = getProbabilities("+1000");
        when(config.getProbabilities()).thenReturn(probabilities);
        when(random.nextInt(anyInt())).thenReturn(0);

        matrixService.generateMatrix();

        String[][] matrix = matrixService.getMatrix();
        assertNotNull(matrix);
        boolean bonusPlaced = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ("+1000".equals(matrix[i][j])) {
                    bonusPlaced = true;
                    break;
                }
            }
        }

        assertTrue(bonusPlaced);
        assertEquals("+1000", matrixService.getAppliedBonusSymbol());
    }

    @Test
    public void testMissBonus() {
        int rows = 3;
        int columns = 3;

        when(config.getRows()).thenReturn(rows);
        when(config.getColumns()).thenReturn(columns);

        Probabilities probabilities = getProbabilities("miss");
        when(config.getProbabilities()).thenReturn(probabilities);
        when(random.nextInt(anyInt())).thenReturn(0);

        matrixService.generateMatrix();

        String[][] matrix = matrixService.getMatrix();
        assertNotNull(matrix);
        String[][] matrixExpected = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixExpected[i][j] = "A";
            }
        }
        matrixExpected[0][0] = "miss";
        assertArrayEquals(matrixExpected, matrix);
    }

    private Probabilities getProbabilities(String bonusKey) {
        Map<String, Integer> bonusSymbols = new HashMap<>();
        bonusSymbols.put(bonusKey, 1);
        BonusSymbolProbability bonusSymbolProbability = new BonusSymbolProbability();
        bonusSymbolProbability.setSymbols(bonusSymbols);
        Probabilities probabilities = new Probabilities();
        probabilities.setBonusSymbols(bonusSymbolProbability);

        Map<String, Integer> symbolMap = new HashMap<>();
        symbolMap.put("A", 1);
        StandardSymbolProbability symbolProbability = new StandardSymbolProbability();
        symbolProbability.setSymbols(symbolMap);
        List<StandardSymbolProbability> standardSymbols = new ArrayList<>();
        standardSymbols.add(symbolProbability);
        probabilities.setStandardSymbols(standardSymbols);
        return probabilities;
    }
}
