package com.scratchgame.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Config {

    @JsonProperty("columns")
    private int columns;

    @JsonProperty("rows")
    private int rows;

    @JsonProperty("symbols")
    private Map<String, Symbol> symbols;

    @JsonProperty("probabilities")
    private Probabilities probabilities;

    @JsonProperty("win_combinations")
    private Map<String, WinCombination> winCombinations;

}
