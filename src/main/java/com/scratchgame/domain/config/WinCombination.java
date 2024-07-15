package com.scratchgame.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WinCombination {
    @JsonProperty("reward_multiplier")
    private double rewardMultiplier;

    @JsonProperty("when")
    private String when;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("group")
    private String group;

    @JsonProperty("covered_areas")
    private String[][] coveredAreas;
}
