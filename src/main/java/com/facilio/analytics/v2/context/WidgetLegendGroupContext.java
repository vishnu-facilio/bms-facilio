package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WidgetLegendGroupContext implements Serializable {
    private Long id;
    private String name;
    private Long reportId;
    List<WidgetLegendVarianceContext> variances;
}