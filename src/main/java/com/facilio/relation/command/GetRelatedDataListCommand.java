package com.facilio.relation.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class GetRelatedDataListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String relationModuleName = (String) context.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME);
        String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
        String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
        String orderBy = (String) context.get(FacilioConstants.ContextNames.ORDER_BY);
        String orderType = (String) context.get(FacilioConstants.ContextNames.ORDER_TYPE);
        boolean withCount = (boolean) context.get(Constants.WITH_COUNT);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        boolean withoutCustomButtons = (boolean) context.get(Constants.WITHOUT_CUSTOMBUTTONS);
        boolean excludeParentFilter = (boolean) context.get(Constants.EXCLUDE_PARENT_CRITERIA);
        String clientCriteria = (String) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
        Criteria filterServerCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        RelationMappingContext.Position relationPosition = (RelationMappingContext.Position) context.get(FacilioConstants.ContextNames.RELATION_POSITION_TYPE);

        FacilioContext listContext;
        JSONObject recordJSON = new JSONObject();

        if (StringUtils.isNotEmpty(relationModuleName)) {
            listContext = V3Util.fetchList(relationModuleName, true, viewName, filters, excludeParentFilter, clientCriteria,
                    orderBy, orderType,search, page, perPage, withCount, queryParams, filterServerCriteria, withoutCustomButtons);

            JSONObject customRelation = Constants.getJsonRecordMap(listContext);
            ArrayList<Map<String, Object>> resultData = (ArrayList<Map<String, Object>>) customRelation.get(relationModuleName);

            if (CollectionUtils.isNotEmpty(resultData)) {
                JSONObject moduleDataObj = (JSONObject) resultData.get(0).get(relationPosition.getFieldName());
                FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList((long) moduleDataObj.get("id")), queryParams, true);

                Map<String, List> recordMap = (Map<String, List>) summaryContext.get(Constants.RECORD_MAP);
                List list = recordMap.get(moduleName);

                recordJSON.put(moduleName, FieldUtil.getAsJSONArray(list, ModuleBaseWithCustomFields.class));
            } else {
                recordJSON.put(moduleName, new ArrayList<>());
            }
        } else {
            listContext = V3Util.fetchList(moduleName, true, viewName, filters, excludeParentFilter, clientCriteria,
                    orderBy, orderType,search, page, perPage, withCount, queryParams, filterServerCriteria, withoutCustomButtons);

            recordJSON = Constants.getJsonRecordMap(listContext);

            if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
                context.put(FacilioConstants.ContextNames.META, listContext.get(FacilioConstants.ContextNames.META));
            }
        }

        context.put(FacilioConstants.ContextNames.RESULT, recordJSON);

        return false;
    }
}
