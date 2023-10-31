package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Unit;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;




public class GetCommissionedChainCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String status = (String) context.get("status");
        if (status.equals("COMMISSIONED") && !context.containsKey(FacilioConstants.ContextNames.FETCH_COUNT)) {
            List<Map<String, Object>> data = (List<Map<String, Object>>) context.get("data");

            Map<Integer, Map<Long, String>> resourceMap = new HashMap<>();
            Map<Integer, Set<Long>> scopeVsParentIds = new HashMap<>();

            for (Map<String, Object> point : data) {
                ResourceType scope = null;
                if(point.get(AgentConstants.READING_SCOPE) != null){
                    scope = ResourceType.valueOf((int)point.get(AgentConstants.READING_SCOPE));
                }
                else {
                    scope = ResourceType.ASSET_CATEGORY;
                }
                int scopeIntVal = scope.getIndex();
                long resourceId = (long) point.get(AgentConstants.RESOURCE_ID);

                scopeVsParentIds
                        .computeIfAbsent(scopeIntVal, k -> new HashSet<>())
                        .add(resourceId);
            }

            scopeVsParentIds.forEach((scopeIntVal, parentIds) -> {
                Map<Long, String> pickList = null;
                try {
                    pickList = CommissioningApi.getParent(
                            parentIds,
                            ResourceType.valueOf(scopeIntVal).getModuleName()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                resourceMap.put(scopeIntVal, pickList);
            });


            Set<Long> fieldIdSet = data.stream().map(x -> (Long) x.get("fieldId")).collect(Collectors.toSet());
            Map<Long, Map<String, Object>> fieldMap = CommissioningApi.getFields(fieldIdSet);
            Map<Integer, String> unitMap = unitMap(data);
            context.put("resourceMap", resourceMap);
            context.put("fieldMap", fieldMap);
            context.put("unitMap", unitMap);
        }
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
