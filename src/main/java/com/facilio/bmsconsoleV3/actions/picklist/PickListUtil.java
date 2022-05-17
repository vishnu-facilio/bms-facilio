package com.facilio.bmsconsoleV3.actions.picklist;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PickListUtil extends V3Action {

    public static List<FieldOption<Long>> getSpecialModulesPickList(String moduleName, int page, int perPage, String search) throws Exception {
        Map<String,Object> paramsData = new HashMap<>();
        paramsData.put("page", page);
        paramsData.put("perPage", perPage);
        paramsData.put("search" , search);

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
        }
        if (StringUtils.isNotEmpty(viewName)) {
            context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
        }
    }
}
