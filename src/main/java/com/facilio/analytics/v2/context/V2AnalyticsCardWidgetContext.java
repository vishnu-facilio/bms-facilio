package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.Criteria;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

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
    public String title;
    public JSONObject cardStyle;
    public String baselineTrend;
    public Boolean isModuleKpi;
    public boolean isReadingKpi;
    public Long dynamicKpiId;
    public String parentModuleName;
    public int aggr;
    public V2TimeFilterContext timeFilter;
    public String baseline;
    public String displayName;
    public String type;
    public Long reportId;
    @JsonIgnore
    public HashMap<String, Map<String, Object>> result = new HashMap<>();
    public Boolean getIsModuleKpi(){
        if(this.isModuleKpi==null){
            return false;
        }
        return true;
    }
}
