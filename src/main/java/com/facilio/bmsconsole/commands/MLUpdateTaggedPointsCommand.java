package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.CommissioningApi;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;
@Log4j
public class MLUpdateTaggedPointsCommand extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> points = (List<Map<String, Object>>) context.get(AgentConstants.POINTS);

        Map<Long,String>assetCategory = AssetsAPI.getCategoryList().stream().collect(Collectors.toMap(AssetCategoryContext::getId,AssetCategoryContext::getName));
        Map<Long,String>resources=CommissioningApi.getResources(points.stream().map(x->(Long)x.get("resourceId")).filter(Objects::nonNull).collect(Collectors.toSet()));
        Map<Long,Map<String,Object>> fields=CommissioningApi.getFields(points.stream().map(x->(Long)x.get("fieldId")).filter(Objects::nonNull).collect(Collectors.toSet()));

        Map<String,Map<String,Map<Long,String>>>updateMap = new HashMap<>();

        for(Map<String,Object>point : points){
            if(point.containsKey("resourceId") || point.containsKey("fieldId")){
                Map<String,Map<Long,String>>pointMap = new HashMap<>();
                pointMap.put("category", Collections.singletonMap((Long) point.get("categoryId"),assetCategory.get(point.get("categoryId"))));
                pointMap.put("assetName",  Collections.singletonMap((Long) point.get("resourceId"),resources.get(point.get("resourceId"))) );
                pointMap.put("reading",Collections.singletonMap((Long) point.get("fieldId"),(String) fields.get(point.get("fieldId")).get("name"))  );


                updateMap.put((String) point.get("name"),pointMap);
            }
        }

        if(updateMap!=null && !updateMap.isEmpty()){
            try {
                BmsPointsTaggingUtil.updateTaggedPointList(updateMap);
                return false;
            }catch (Exception e){
                LOGGER.error("Exception while updating commissioned points",e);
            }
        }
        return true;
    }
}
