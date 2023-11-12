package com.facilio.bmsconsoleV3.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetFormsRelationPickListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName=(String)context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        JSONObject pagination= (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.PAGINATION,null);
        String search= (String) context.getOrDefault(FacilioConstants.ContextNames.SEARCH,null);
        String filter= (String) context.getOrDefault(FacilioConstants.ContextNames.FILTERS,null);

        String defaults= (String) context.getOrDefault(FacilioConstants.ContextNames.DEFAULT,null);
        boolean excludeParentFilter = (boolean) context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER,false);
        String clientCriteria= (String) context.getOrDefault(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA,null);
        String orderBy= (String) context.getOrDefault(FacilioConstants.ContextNames.ORDER_BY,null);
        String orderType= (String) context.getOrDefault(FacilioConstants.ContextNames.ORDER_TYPE,null);
        boolean withCount= (boolean) context.getOrDefault(FacilioConstants.ContextNames.WITH_COUNT,false);

        Map<String, List<Object>> queryParams= (Map<String, List<Object>>) context.getOrDefault(FacilioConstants.ContextNames.QUERY_PARAMS,null);
        int page=1,perPage=50;
        if(pagination!=null){
            page= (int) pagination.get("page");
            perPage= (int) pagination.get("perPage");
        }

        FacilioContext pickListContext = V3Util.fetchPickList(moduleName, filter, excludeParentFilter,clientCriteria,defaults, orderBy, orderType,search, page, perPage,withCount, queryParams, null);
        context.put(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
        if (pickListContext.containsKey(FacilioConstants.ContextNames.META)) {
            context.put(FacilioConstants.ContextNames.META, pickListContext.get(FacilioConstants.ContextNames.META));
        }

        return false;
    }
}
