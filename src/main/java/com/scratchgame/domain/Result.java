package com.scratchgame.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Result {
    @JsonProperty("matrix")
    private String[][] matrix;
    @JsonProperty("reward")
    private double reward;
    @JsonProperty("applied_winning_combinations")
    private Map<String, List<String>> appliedWinningCombinations;
    @JsonProperty("applied_bonus_symbol")
    private String appliedBonusSymbol;
}
