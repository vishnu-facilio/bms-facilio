package com.facilio.analytics.v2.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2AnalyticsMeasureContext {

    private long id;
    private long reportId;
    private String measure_props;
    private Long relationship_id;
    private Long measure_field_id;
    private Long parent_lookup_field_id;
    private Long criteria_id;
}
