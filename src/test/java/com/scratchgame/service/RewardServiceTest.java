package com.scratchgame.service;

import com.scratchgame.domain.config.Config;
import com.scratchgame.domain.config.Symbol;
import com.scratchgame.domain.config.WinCombination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RewardServiceTest {

    private Config config;
    private MatrixService matrixService;
    private RewardService rewardService;

    @BeforeEach
    public void setUp() {
        config = mock(Config.class);
        matrixService = mock(MatrixService.class);
        rewardService = new RewardService(config, matrixService);
    }

    @Test
    public void testCalculateRewardWithWinningCombination() {
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "B", "B"},
                {"C", "C", "C"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.0);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);

        when(config.getSymbols()).thenReturn(symbols);

        WinCombination winCombinationA = new WinCombination();
        winCombinationA.setWhen("same_symbols");
        winCombinationA.setCount(3);
        winCombinationA.setRewardMultiplier(1.0);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombinationA);

        when(config.getWinCombinations()).thenReturn(winCombinations);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward =
                (bettingAmount * symbolA.getRewardMultiplier()) +
                        (bettingAmount * symbolB.getRewardMultiplier()) +
                        (bettingAmount * symbolC.getRewardMultiplier());

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Collections.singletonList("triple"));
        expectedCombinations.put("B", Collections.singletonList("triple"));
        expectedCombinations.put("C", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }

    @Test
    public void testCalculateRewardWithWinningCombinationByArea() {
        String[][] matrix = {
                {"A", "B", "A"},
                {"B", "A", "B"},
                {"C", "C", "A"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.1);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);

        when(config.getSymbols()).thenReturn(symbols);

        WinCombination winCombinationB = new WinCombination();
        winCombinationB.setWhen("same_symbols");
        winCombinationB.setCount(3);
        winCombinationB.setRewardMultiplier(1.2);

        WinCombination winCombinationA = new WinCombination();
        winCombinationA.setWhen("same_symbols");
        winCombinationA.setCount(4);
        winCombinationA.setRewardMultiplier(1.5);

        WinCombination winCombinationArea = new WinCombination();
        winCombinationArea.setWhen("linear_symbols");
        winCombinationArea.setCount(3);
        winCombinationArea.setRewardMultiplier(5.0);
        String[][] coveredArea = {
                {"0:0"},
                {"1:1"},
                {"2:2"}
        };
        winCombinationArea.setCoveredAreas(coveredArea);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombinationB);
        winCombinations.put("four", winCombinationA);
        winCombinations.put("diagonal", winCombinationArea);

        when(config.getWinCombinations()).thenReturn(winCombinations);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward =
                (bettingAmount * symbolA.getRewardMultiplier() * winCombinationA.getRewardMultiplier() * winCombinationArea.getRewardMultiplier()) +
                        (bettingAmount * symbolB.getRewardMultiplier() * winCombinationB.getRewardMultiplier());

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Arrays.asList("four", "diagonal"));
        expectedCombinations.put("B", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }

    @Test
    public void testCalculateRewardWithWinningCombinationByAreaAndAddBonus() {
        String[][] matrix = {
                {"A", "B", "A"},
                {"B", "A", "B"},
                {"C", "+1000", "A"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.1);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Symbol bonusSymbol = new Symbol();
        bonusSymbol.setExtra(1000);
        bonusSymbol.setImpact("extra_bonus");
        bonusSymbol.setType("bonus");

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);
        symbols.put("+1000", bonusSymbol);

        when(config.getSymbols()).thenReturn(symbols);

        WinCombination winCombinationB = new WinCombination();
        winCombinationB.setWhen("same_symbols");
        winCombinationB.setCount(3);
        winCombinationB.setRewardMultiplier(1.2);

        WinCombination winCombinationA = new WinCombination();
        winCombinationA.setWhen("same_symbols");
        winCombinationA.setCount(4);
        winCombinationA.setRewardMultiplier(1.5);

        WinCombination winCombinationArea = new WinCombination();
        winCombinationArea.setWhen("linear_symbols");
        winCombinationArea.setCount(3);
        winCombinationArea.setRewardMultiplier(5.0);
        String[][] coveredArea = {
                {"0:0"},
                {"1:1"},
                {"2:2"}
        };
        winCombinationArea.setCoveredAreas(coveredArea);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombinationB);
        winCombinations.put("four", winCombinationA);
        winCombinations.put("diagonal", winCombinationArea);

        when(config.getWinCombinations()).thenReturn(winCombinations);
        when(config.getRows()).thenReturn(3);
        when(config.getColumns()).thenReturn(3);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward =
                (bettingAmount * symbolA.getRewardMultiplier() * winCombinationA.getRewardMultiplier() * winCombinationArea.getRewardMultiplier()) +
                        (bettingAmount * symbolB.getRewardMultiplier() * winCombinationB.getRewardMultiplier()) +
                        bonusSymbol.getExtra();

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Arrays.asList("four", "diagonal"));
        expectedCombinations.put("B", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }

    @Test
    public void testCalculateRewardWithWinningCombinationByAreaAndMissBonus() {
        String[][] matrix = {
                {"A", "B", "A"},
                {"B", "A", "B"},
                {"C", "MISS", "C"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.1);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Symbol bonusSymbol = new Symbol();
        bonusSymbol.setImpact("miss");
        bonusSymbol.setType("bonus");

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);
        symbols.put("MISS", bonusSymbol);

        when(config.getSymbols()).thenReturn(symbols);

        WinCombination winCombination = new WinCombination();
        winCombination.setWhen("same_symbols");
        winCombination.setCount(3);
        winCombination.setRewardMultiplier(1.2);

        WinCombination winCombinationArea = new WinCombination();
        winCombinationArea.setWhen("linear_symbols");
        winCombinationArea.setCount(3);
        winCombinationArea.setRewardMultiplier(5.0);
        String[][] coveredArea = {
                {"0:0"},
                {"1:1"},
                {"2:2"}
        };
        winCombinationArea.setCoveredAreas(coveredArea);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombination);
        winCombinations.put("diagonal", winCombinationArea);

        when(config.getWinCombinations()).thenReturn(winCombinations);
        when(config.getRows()).thenReturn(3);
        when(config.getColumns()).thenReturn(3);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward =
                (bettingAmount * symbolA.getRewardMultiplier() * winCombination.getRewardMultiplier()) +
                        (bettingAmount * symbolB.getRewardMultiplier() * winCombination.getRewardMultiplier());

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Collections.singletonList("triple"));
        expectedCombinations.put("B", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }

    @Test
    public void testCalculateRewardWithWinningCombinationByAreaAndMultiplyBonus() {
        String[][] matrix = {
                {"A", "B", "A"},
                {"B", "A", "B"},
                {"C", "5x", "C"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.1);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Symbol bonusSymbol = new Symbol();
        bonusSymbol.setImpact("multiply_reward");
        bonusSymbol.setRewardMultiplier(5);
        bonusSymbol.setType("bonus");

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);
        symbols.put("5x", bonusSymbol);

        when(config.getSymbols()).thenReturn(symbols);

        WinCombination winCombination = new WinCombination();
        winCombination.setWhen("same_symbols");
        winCombination.setCount(3);
        winCombination.setRewardMultiplier(1.2);

        WinCombination winCombinationArea = new WinCombination();
        winCombinationArea.setWhen("linear_symbols");
        winCombinationArea.setCount(3);
        winCombinationArea.setRewardMultiplier(5.0);
        String[][] coveredArea = {
                {"0:0"},
                {"1:1"},
                {"2:2"}
        };
        winCombinationArea.setCoveredAreas(coveredArea);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombination);
        winCombinations.put("diagonal", winCombinationArea);

        when(config.getWinCombinations()).thenReturn(winCombinations);
        when(config.getRows()).thenReturn(3);
        when(config.getColumns()).thenReturn(3);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward = bonusSymbol.getRewardMultiplier() * (
                (bettingAmount * symbolA.getRewardMultiplier() * winCombination.getRewardMultiplier()) +
                        (bettingAmount * symbolB.getRewardMultiplier() * winCombination.getRewardMultiplier())
        );

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Collections.singletonList("triple"));
        expectedCombinations.put("B", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }

    @Test
    public void testCalculateRewardWithBonus() {
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "+1000", "B"},
                {"C", "C", "C"}
        };

        when(matrixService.getMatrix()).thenReturn(matrix);

        Symbol symbolA = new Symbol();
        symbolA.setRewardMultiplier(1.0);

        Symbol symbolB = new Symbol();
        symbolB.setRewardMultiplier(2.0);

        Symbol symbolC = new Symbol();
        symbolC.setRewardMultiplier(3.0);

        Symbol bonusSymbol = new Symbol();
        bonusSymbol.setType("bonus");
        bonusSymbol.setImpact("extra_bonus");
        bonusSymbol.setExtra(1000);

        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", symbolA);
        symbols.put("B", symbolB);
        symbols.put("C", symbolC);
        symbols.put("+1000", bonusSymbol);

        when(config.getSymbols()).thenReturn(symbols);
        when(config.getColumns()).thenReturn(3);
        when(config.getRows()).thenReturn(3);

        WinCombination winCombinationA = new WinCombination();
        winCombinationA.setWhen("same_symbols");
        winCombinationA.setCount(3);
        winCombinationA.setRewardMultiplier(1.0);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("triple", winCombinationA);

        when(config.getWinCombinations()).thenReturn(winCombinations);

        int bettingAmount = 100;

        rewardService.calculateReward(bettingAmount);

        double expectedReward =
                (bettingAmount * symbolA.getRewardMultiplier()) +
                        (bettingAmount * symbolC.getRewardMultiplier()) +
                        bonusSymbol.getExtra();

        assertEquals(expectedReward, rewardService.getReward());

        Map<String, List<String>> expectedCombinations = new HashMap<>();
        expectedCombinations.put("A", Collections.singletonList("triple"));
        expectedCombinations.put("C", Collections.singletonList("triple"));

        assertEquals(expectedCombinations, rewardService.getAppliedWinningCombinations());
    }
}
