package com.scratchgame.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class BonusSymbolProbability {
    @JsonProperty("symbols")
    private Map<String, Integer> symbols;
}
