package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PickListUtil {

    public static List<FieldOption<Long>> getSpecialModulesPickList(String moduleName, int page, int perPage, String search) throws Exception {
        return getSpecialModulesPickList(moduleName,page,perPage,search,null,null);
    }
    public static List<FieldOption<Long>> getSpecialModulesPickList(String moduleName, int page, int perPage, String search,String filters,String _default) throws Exception {
        return getSpecialModulesPickList(moduleName,page,perPage,search,filters,_default, null);
    }
        
    public static List<FieldOption<Long>> getSpecialModulesPickList(String moduleName, int page, int perPage, String search,String filters,String _default, Criteria criteria) throws Exception {
        return getSpecialModulesPickList(moduleName, page, perPage, search, filters, _default, criteria, null, null);
    }

    public static List<FieldOption<Long>> getSpecialModulesPickList(String moduleName, int page, int perPage, String search, String filters, String _default, Criteria criteria, String orderBy, String orderType) throws Exception {
        Map<String,Object> paramsData = new HashMap<>();
        paramsData.put("page", page);
        paramsData.put("perPage", perPage);
        paramsData.put("search" , search);
        if (StringUtils.isNotEmpty(filters)) {
            JSONObject filter = FacilioUtil.parseJson(filters);
            paramsData.put("filters",filter);
        }
        if(StringUtils.isNotEmpty(_default)){
            paramsData.put("_default",_default);
        }
        if(criteria != null) {
            paramsData.put("serverCriteria", criteria);
        }
        if (StringUtils.isNotEmpty(orderType)) {
            paramsData.put("orderBy", orderBy);
            paramsData.put("orderType", orderType);
        }
        return LookupSpecialTypeUtil.getNewPickList(moduleName, paramsData);
    }

    public static FacilioContext fetchPickListData(FacilioContext contextData) throws Exception {
        FacilioChain pickListChain = ReadOnlyChainFactory.newPicklistFromDataChain();
        FacilioContext pickListContext = pickListChain.getContext();
        if(contextData != null) {
            pickListContext.putAll(contextData);
        }
        pickListChain.execute();
        return pickListContext;
    }

    public static void populatePicklistContext (FacilioContext context, String moduleName, String filters, String search, Criteria clientCriteria, String clientCriteriaStr, String defaultIds, String viewName, int page, int perPage) throws Exception {
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, clientCriteria);
        Boolean isToFetchDecommissionedResource = (Boolean) context.get(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE);
        List<String> resourceModules = Arrays.asList("site", "building","floor", "space", "asset", "resource", "basespace");
        if(!BooleanUtils.isTrue(isToFetchDecommissionedResource) && resourceModules.contains(moduleName)) {
                clientCriteria = clientCriteria !=null ? clientCriteria : new Criteria();
                clientCriteria.addOrCondition(CriteriaAPI.getCondition("Resources.IS_DECOMMISSIONED", FacilioConstants.ContextNames.DECOMMISSION, "false", BooleanOperators.IS));
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        if (search != null) {
            context.put(FacilioConstants.ContextNames.SEARCH, search);
        }
        if (page != 0) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", page);
            pagination.put("perPage", perPage);
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }

        if (StringUtils.isNotEmpty(clientCriteriaStr)) {
            JSONObject json = FacilioUtil.parseJson(clientCriteriaStr);
            Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
            context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
        }

        if (StringUtils.isNotEmpty(filters)) {
            JSONObject json = FacilioUtil.parseJson(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        if (StringUtils.isNotEmpty(defaultIds)) {
            String[] ids = FacilioUtil.splitByComma(defaultIds);
            List<Long> defaultIdList = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
            context.put(FacilioConstants.PickList.DEFAULT_ID_LIST, defaultIdList);
            if(resourceModules.contains(moduleName) && CollectionUtils.isNotEmpty(defaultIdList) && !BooleanUtils.isTrue(isToFetchDecommissionedResource)) {
                clientCriteria.addOrCondition(CriteriaAPI.getCondition("Resources.ID", AgentConstants.ID, StringUtils.join(defaultIdList, ","), NumberOperators.EQUALS));
            }
        }
        if (StringUtils.isNotEmpty(viewName)) {
            context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
        }
      //  context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, clientCriteria);
    }
}
