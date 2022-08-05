package com.facilio.agentv2.commands;

import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import java.util.*;
import java.util.stream.Collectors;
@Log4j
public class MLGetTaggedPointsCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try{
        CommissioningLogContext log = (CommissioningLogContext) context.get(FacilioConstants.ContextNames.LOG);
        boolean prefillMldata = log.isPrefillMlData();
        if (prefillMldata) {
            List<String> controllerNames = log.getControllers().stream().map(x -> (String) x.get("name")).collect(Collectors.toList());
            Map<String, Map<String, Object>> recordMap = new HashMap<>();
            if (controllerNames != null && !controllerNames.isEmpty()) {
                    recordMap = BmsPointsTaggingUtil.getTaggedPointList(controllerNames);
            }
            if (recordMap != null && !recordMap.isEmpty()) {
                JSONArray pointsJson = log.getPoints();
                Map<Long, String> resources = (Map<Long, String>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
                Map<Long, Map<String, Object>> fieldMap = (Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.FIELDS);

                List<Long> newResourceids = new ArrayList<>();
                List<Long> newFieldIds = new ArrayList<>();


                Map<String, Map<String, Object>> finalRecordMap = recordMap;
                pointsJson.forEach(item -> {
                    HashMap<String, Object> point = (HashMap<String, Object>) item;
                    if (finalRecordMap.containsKey(point.get("name"))) {
                        Map<String, Object> pointMap = finalRecordMap.get(point.get("name"));
                        List<Long> resourceIds = ((List<String>) pointMap.get("assetIds")).stream().map(Long::parseLong).collect(Collectors.toList());
                        newResourceids.addAll(resourceIds);
                        point.put("suggestedResourceId", resourceIds);
                        List<Long> fieldIds = ((List<String>) pointMap.get("readingIds")).stream().map(Long::parseLong).collect(Collectors.toList());
                        newFieldIds.addAll(fieldIds);
                        point.put("suggestedFieldId", fieldIds);
                        point.put("suggestedCategoryId", ((List<String>) pointMap.get("categoryIds")).stream().map(Long::parseLong).collect(Collectors.toList()));
                    }
                });

                resources.putAll(CommissioningApi.getResources(new HashSet<>(newResourceids)));
                fieldMap.putAll(CommissioningApi.getFields(new HashSet<>(newFieldIds)));

                return false;
            }
            return true;
         }
         }catch (Exception e){
            LOGGER.error("Exception while getting points suggestions",e);
        }
        return false;
    }
}

