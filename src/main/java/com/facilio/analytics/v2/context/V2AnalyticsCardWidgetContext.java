package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.Criteria;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class V2AnalyticsCardWidgetContext
{
    private long cardId;
    public Criteria criteria;
    public Long criteriaId;
    public int criteriaType;
    public Long fieldId;
    public boolean isModuleKpi;
    public boolean isReadingKpi;
    public String parentModuleName;
    public int aggr;
    public V2TimeFilterContext timeFilter;
    public String baseline;

    @JsonIgnore
    public HashMap<String, Map<String, Object>> result = new HashMap<>();
}
