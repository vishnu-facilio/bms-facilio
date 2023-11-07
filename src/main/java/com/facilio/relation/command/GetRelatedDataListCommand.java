package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.ConfigParams;
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
        boolean isSubFormRecord = (boolean) context.get(FacilioConstants.ContextNames.IS_SUB_FORM_RECORD);
        boolean withCount = (boolean) context.get(Constants.WITH_COUNT);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        boolean withoutCustomButtons = (boolean) context.get(Constants.WITHOUT_CUSTOMBUTTONS);
        boolean excludeParentFilter = (boolean) context.get(Constants.EXCLUDE_PARENT_CRITERIA);
        boolean fetchClassificationData=(boolean) context.getOrDefault(Constants.FETCH_CLASSIFICATION,false);
        String selectableFieldNames=(String)context.get(FacilioConstants.ContextNames.SELECTABLE_FIELD_NAMES);
        boolean fetchOnlyViewColumnFields=(boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        String clientCriteria = (String) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
        Criteria filterServerCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        RelationMappingContext.Position relationPosition = (RelationMappingContext.Position) context.get(FacilioConstants.ContextNames.RELATION_POSITION_TYPE);
        RelationContext relationContext = (RelationContext) context.getOrDefault(FacilioConstants.ContextNames.RELATION, new RelationContext());
        String extendedModuleName = (String) context.get(FacilioConstants.ContextNames.EXTENDED_MODULE_NAME);

        FacilioContext listContext;
        JSONObject recordJSON = new JSONObject();
        Map<String, Object> paginationObject = new HashMap<>();

        if (relationContext.isVirtual()) {
            FacilioChain chain = ReadOnlyChainFactoryV3.getFetchVirtualRelationDataChain();
            FacilioContext chainContext = chain.getContext();
//            chainContext.put(FacilioConstants.ContextNames.PAGE, page);
//            chainContext.put(FacilioConstants.ContextNames.PER_PAGE, perPage);
            chainContext.put(FacilioConstants.ContextNames.ID, context.get(FacilioConstants.ContextNames.ID));
            chainContext.put(FacilioConstants.ContextNames.RELATION_NAME, context.get(FacilioConstants.ContextNames.RELATION_NAME));
            chain.execute();

            List<Long> recordIdList = (List<Long>) chainContext.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
            if (CollectionUtils.isEmpty(recordIdList)) {
                context.put(FacilioConstants.ContextNames.RESULT, recordJSON);
                return false;
            }
            String fetchRecordsModuleName = (String) chainContext.get(FacilioConstants.ContextNames.MODULE_NAME);

            if (fetchSummary) {
                FacilioContext summaryContext = V3Util.getSummary(fetchRecordsModuleName, recordIdList, queryParams,fetchClassificationData,null);
                Map<String, List> recordMap = (Map<String, List>) summaryContext.get(Constants.RECORD_MAP);
                List list = recordMap.get(fetchRecordsModuleName);

                recordJSON.put(fetchRecordsModuleName, FieldUtil.getAsJSONArray(list, ModuleBaseWithCustomFields.class));
                paginationObject.put("totalCount", list.size());
                JSONObject meta = new JSONObject();
                meta.put("pagination", paginationObject);
                context.put(FacilioConstants.ContextNames.META, meta);
            } else {
                ConfigParams configParams = new ConfigParams();
                configParams.setSelectableFieldNames(selectableFieldNames);
                configParams.setIsSubFormRecord(isSubFormRecord);

                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getIdCondition(recordIdList, Constants.getModBean().getModule(fetchRecordsModuleName)));
                filterServerCriteria = filterServerCriteria == null || filterServerCriteria.isEmpty() ? new Criteria() : filterServerCriteria;
                filterServerCriteria.andCriteria(criteria);

                listContext = V3Util.fetchList(fetchRecordsModuleName, true, viewName, filters, excludeParentFilter, clientCriteria,
                        orderBy, orderType,search, page, perPage, withCount, queryParams, filterServerCriteria, withoutCustomButtons,fetchOnlyViewColumnFields,quickFilter,configParams);

                recordJSON = Constants.getJsonRecordMap(listContext);

                if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
                    context.put(FacilioConstants.ContextNames.META, listContext.get(FacilioConstants.ContextNames.META));
                }
            }
        } else {
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
                    FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList((long) moduleDataObj.get("id")), queryParams,fetchClassificationData,null);

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
                ConfigParams configParams = new ConfigParams();
                configParams.setSelectableFieldNames(selectableFieldNames);
                configParams.setIsSubFormRecord(isSubFormRecord);

                if(StringUtils.isNotEmpty(extendedModuleName)){
                    moduleName = extendedModuleName;
                }

                listContext = V3Util.fetchList(moduleName, true, viewName, filters, excludeParentFilter, clientCriteria,
                        orderBy, orderType,search, page, perPage, withCount, queryParams, filterServerCriteria, withoutCustomButtons,fetchOnlyViewColumnFields,quickFilter,configParams);

                recordJSON = Constants.getJsonRecordMap(listContext);

                if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
                    context.put(FacilioConstants.ContextNames.META, listContext.get(FacilioConstants.ContextNames.META));
                }
            }
        }

        context.put(FacilioConstants.ContextNames.RESULT, recordJSON);

        return false;
    }
}
