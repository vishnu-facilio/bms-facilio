package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3BaseSpaceHazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcludeAvailableWorkOrderHazards extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> queryMap = (Map<String, List>) context.get("queryParams");
        List excludeAvailableParentAssociatedHazards = queryMap.get("excludeAvailableParentAssociatedHazards");
        List parentModule = queryMap.get("parentModuleName");
        if (excludeAvailableParentAssociatedHazards != null && parentModule != null && parentModule.size() > 0 && excludeAvailableParentAssociatedHazards.size() > 0) {
            String parentId = (String) excludeAvailableParentAssociatedHazards.get(0);
            String parentModuleName = (String) queryMap.get("parentModuleName").get(0);
            List<Long> parentIds = fetchAvailableIds(parentId,parentModuleName);
            if (CollectionUtils.isNotEmpty(parentIds)) {
                Condition hazardCondition = CriteriaAPI.getCondition("ID", "id",
                        StringUtils.join(parentIds, ','), NumberOperators.NOT_EQUALS);
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, hazardCondition);
            }
        }
        return false;
    }
    public static List<Long> fetchAvailableIds(String parentId,String parentModuleName) throws Exception{
        List<Long> parentIds = new ArrayList<>();
        if(parentModuleName.equals("workorderHazard")) {
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("WORKORDER_ID", "workorder", parentId, NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
            Map<Long, V3WorkorderHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORKORDER_HAZARD, null, V3WorkorderHazardContext.class, criteria, null);
            if (props != null) {
                for (V3WorkorderHazardContext workorderHazard : props.values()) {
                    V3HazardContext hazard = workorderHazard.getHazard();
                    parentIds.add(hazard.getId());
                }
            }
        }else if(parentModuleName.equals("assetHazard")){
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("ASSET_ID", "asset", parentId, NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
            Map<Long, V3AssetHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.ASSET_HAZARD, null, V3AssetHazardContext.class, criteria, null);
            if (props != null) {
                for (V3AssetHazardContext assetHazard : props.values()) {
                    V3HazardContext hazard = assetHazard.getHazard();
                    parentIds.add(hazard.getId());
                }
            }
        }else if(parentModuleName.equals("spaceHazard")){
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("BASESPACE_ID", "space", parentId, NumberOperators.EQUALS);
            criteria.addAndCondition(condition);
            Map<Long, V3BaseSpaceHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BASESPACE_HAZARD, null, V3BaseSpaceHazardContext.class, criteria, null);
            if (props != null) {
                for (V3BaseSpaceHazardContext spaceHazard : props.values()) {
                    V3HazardContext hazard = spaceHazard.getHazard();
                    parentIds.add(hazard.getId());
                }
            }
        }
        return parentIds;
    }
}
