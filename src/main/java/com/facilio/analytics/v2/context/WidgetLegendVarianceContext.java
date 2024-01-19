package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WidgetLegendVarianceContext implements Serializable {
    private Long id;
    private Long groupId;
    private String alias;
    private AnalyticsVariance variance;
    private String color;
    private String varianceName;
}