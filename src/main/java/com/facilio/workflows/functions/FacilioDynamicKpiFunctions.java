package com.facilio.workflows.functions;

import com.facilio.modules.AggregateOperator;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateRange;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.facilio.readingkpi.ReadingKpiAPI.getReadingKpi;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.READING_KPI)
public class FacilioDynamicKpiFunctions {
    public Object getResult(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
        Integer kpiType = ((Long) objects[0]).intValue();
        if (!Objects.equals(kpiType, KPIType.DYNAMIC.getIndex())) {
            throw new IllegalArgumentException("Only Dynamic Kpis are supported");
        }
        Long kpiId = (Long) objects[1];
        List<Long> parentIds = Collections.singletonList((Long) objects[2]);
        Long startTime = (Long) objects[3];
        Long endTime = (Long) objects[4];

        ReadingKPIContext dynamicKpi = getReadingKpi(kpiId);
        Double kpiValue = null;
        JSONObject cardValue = new JSONObject();
        cardValue.put("unit",dynamicKpi.getUnitLabel());
        cardValue.put("dataType","DECIMAL");
        List<Map<String, Object>> result =  ReadingKpiAPI.getResultForDynamicKpi(parentIds, new DateRange(startTime, endTime), null, dynamicKpi.getNs()).get(objects[2]);
        if(!result.isEmpty() && result.get(0)!=null) {
            kpiValue = (Double) result.get(0).get("result");
        }
        cardValue.put("value",kpiValue);
        return cardValue;
    }
}
