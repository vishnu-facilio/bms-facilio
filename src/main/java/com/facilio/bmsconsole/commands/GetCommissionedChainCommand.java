package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.command.FacilioCommand;
import com.facilio.unitconversion.Unit;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;




public class GetCommissionedChainCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Map<String,Object>> data = (List<Map<String, Object>>) context.get("data");
        Set<Long> resourceIdSet =data.stream().map(x->(Long)x.get("resourceId")).collect(Collectors.toSet());
        Set<Long>fieldIdSet =data.stream().map(x->(Long)x.get("fieldId")).collect(Collectors.toSet());
        Map<Long, String> resourceMap = CommissioningApi.getResources(resourceIdSet);
        Map<Long, Map<String, Object>> fieldMap = CommissioningApi.getFields(fieldIdSet);
        Map<Integer, String> unitMap = unitMap(data);
        context.put("resourceMap",resourceMap);
        context.put("fieldMap",fieldMap);
        context.put("unitMap",unitMap);
        return false;


    }
    public static Map<Integer, String> unitMap(List<Map<String,Object>> points){
        Map<Integer, String> unitMap = new HashMap<>();
        for(int i = 0, size = points.size(); i < size; i++){
            Map<String, Object> point = (Map<String, Object>) points.get(i);
            if (point.get("unit") != null && !unitMap.containsKey(point.get("unit"))) {
                int unitId = Integer.parseInt(point.get("unit").toString());
                if (unitId > 0) {
                    Unit unit = Unit.valueOf(unitId);
                    unitMap.put(unitId, unit.getSymbol());
                } else {
                    point.put("unit", null);
                }
            }
        }
        return unitMap;
    }
}
