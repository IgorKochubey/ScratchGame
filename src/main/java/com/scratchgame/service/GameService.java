package com.scratchgame.service;

import com.scratchgame.domain.Result;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class GameService {
    private final MatrixService matrixService;
    private final RewardService rewardService;

    public void play(int bettingAmount) {
        matrixService.generateMatrix();
        rewardService.calculateReward(bettingAmount);
    }

    public Result getResult() {
        Result result = new Result();
        result.setMatrix(matrixService.getMatrix());
        result.setAppliedBonusSymbol(matrixService.getAppliedBonusSymbol());
        result.setReward(rewardService.getReward());
        result.setAppliedWinningCombinations(rewardService.getAppliedWinningCombinations());
        return result;
    }
}