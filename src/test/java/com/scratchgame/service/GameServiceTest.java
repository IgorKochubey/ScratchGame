package com.scratchgame.service;

import com.scratchgame.domain.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @Mock
    private MatrixService matrixService;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(matrixService, rewardService);
    }

    @Test
    public void testPlay() {
        int bettingAmount = 100;

        doNothing().when(matrixService).generateMatrix();
        doNothing().when(rewardService).calculateReward(bettingAmount);

        gameService.play(bettingAmount);

        verify(matrixService, times(1)).generateMatrix();
        verify(rewardService, times(1)).calculateReward(bettingAmount);
    }

    @Test
    public void testGetResult() {
        String[][] expectedMatrix = {{"A", "B"}, {"C", "D"}};
        String expectedBonusSymbol = "+1000";
        double expectedReward = 5000;
        // Assuming a map of winning combinations, add accordingly
        Map<String, List<String>> expectedWinningCombinations = new HashMap<>();
        List<String> winningCombinations = new ArrayList<>();
        winningCombinations.add("same_symbol_3_times");
        expectedWinningCombinations.put("A", winningCombinations);

        when(matrixService.getMatrix()).thenReturn(expectedMatrix);
        when(matrixService.getAppliedBonusSymbol()).thenReturn(expectedBonusSymbol);
        when(rewardService.getReward()).thenReturn(expectedReward);
        when(rewardService.getAppliedWinningCombinations()).thenReturn(expectedWinningCombinations);

        Result result = gameService.getResult();

        assertNotNull(result);
        assertArrayEquals(expectedMatrix, result.getMatrix());
        assertEquals(expectedBonusSymbol, result.getAppliedBonusSymbol());
        assertEquals(expectedReward, result.getReward());
        assertEquals(expectedWinningCombinations, result.getAppliedWinningCombinations());
    }
}
