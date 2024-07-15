package com.scratchgame.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Symbol {
    @JsonProperty("reward_multiplier")
    private double rewardMultiplier;

    @JsonProperty("type")
    private String type;

    @JsonProperty("impact")
    private String impact; // This field is optional, as not all symbols have it

    @JsonProperty("extra")
    private Integer extra; // This field is optional, as not all symbols have it
}
