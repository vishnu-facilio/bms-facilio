package com.facilio.relation.command;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
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
        boolean fetchSummary = (boolean) context.getOrDefault("fetchSummary", false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String relationModuleName = (String) context.get(FacilioConstants.ContextNames.RELATION_MODULE_NAME);
        String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
        String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
        String quickFilter = (String) context.get(FacilioConstants.ContextNames.QUICK_FILTER);
        String orderBy = (String) context.get(FacilioConstants.ContextNames.ORDER_BY);
        String orderType = (String) context.get(FacilioConstants.ContextNames.ORDER_TYPE);
        boolean withCount = (boolean) context.get(Constants.WITH_COUNT);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        boolean withoutCustomButtons = (boolean) context.get(Constants.WITHOUT_CUSTOMBUTTONS);
        boolean excludeParentFilter = (boolean) context.get(Constants.EXCLUDE_PARENT_CRITERIA);
        boolean fetchOnlyViewColumnFields=(boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        String clientCriteria = (String) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
        Criteria filterServerCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        RelationMappingContext.Position relationPosition = (RelationMappingContext.Position) context.get(FacilioConstants.ContextNames.RELATION_POSITION_TYPE);

        FacilioContext listContext;
        JSONObject recordJSON = new JSONObject();
        Map<String, Object> paginationObject = new HashMap<>();

        if (fetchSummary) {
            FacilioChain relationDataChain = ReadOnlyChainFactoryV3.validateAndGetCustomRelationDataChain();

            listContext = relationDataChain.getContext();
            listContext.put(Constants.QUERY_PARAMS, queryParams);
            listContext.put(FacilioConstants.ContextNames.MODULE_NAME, relationModuleName);

            relationDataChain.execute();

            JSONObject customRelation = Constants.getJsonRecordMap(listContext);
            ArrayList<Map<String, Object>> resultData = (ArrayList<Map<String, Object>>) customRelation.get(relationModuleName);

            if (CollectionUtils.isNotEmpty(resultData)) {
                Map<String, Object> moduleDataObj = (Map<String, Object>) resultData.get(0).get(relationPosition.getFieldName());
                FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList((long) moduleDataObj.get("id")), queryParams, fetchOnlyViewColumnFields);

                Map<String, List> recordMap = (Map<String, List>) summaryContext.get(Constants.RECORD_MAP);
                List list = recordMap.get(moduleName);

                recordJSON.put(moduleName, FieldUtil.getAsJSONArray(list, ModuleBaseWithCustomFields.class));
                paginationObject.put("totalCount", list.size());
            } else {
                recordJSON.put(moduleName, new ArrayList<>());
                paginationObject.put("totalCount", 0l);
            }
            JSONObject meta = new JSONObject();
            meta.put("pagination", paginationObject);
            context.put(FacilioConstants.ContextNames.META, meta);
        } else {
            listContext = V3Util.fetchList(moduleName, true, viewName, filters, excludeParentFilter, clientCriteria,
                    orderBy, orderType,search, page, perPage, withCount, queryParams, filterServerCriteria, withoutCustomButtons,fetchOnlyViewColumnFields,quickFilter);

            recordJSON = Constants.getJsonRecordMap(listContext);

            if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
                context.put(FacilioConstants.ContextNames.META, listContext.get(FacilioConstants.ContextNames.META));
            }
        }

        context.put(FacilioConstants.ContextNames.RESULT, recordJSON);

        return false;
    }
}
