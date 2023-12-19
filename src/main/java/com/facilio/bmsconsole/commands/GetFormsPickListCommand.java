package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.actions.picklist.PickListUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetFormsPickListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName=(String)context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        String fieldName=(String)context.getOrDefault(FacilioConstants.ContextNames.MODULE_FIELD_NAME,null);
        JSONObject pagination= (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.PAGINATION,null);
        String search= (String) context.getOrDefault(FacilioConstants.ContextNames.SEARCH,null);
        String filter= (String) context.getOrDefault(FacilioConstants.ContextNames.FILTERS,null);

        String defaults= (String) context.getOrDefault(FacilioConstants.ContextNames.DEFAULT,null);
        boolean excludeParentFilter = (boolean) context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER,false);
        String clientCriteria= (String) context.getOrDefault(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA,null);
        String orderBy= (String) context.getOrDefault(FacilioConstants.ContextNames.ORDER_BY,null);
        String orderType= (String) context.getOrDefault(FacilioConstants.ContextNames.ORDER_TYPE,null);
        boolean withCount= (boolean) context.getOrDefault(FacilioConstants.ContextNames.WITH_COUNT,false);
        String extendedModuleName = (String) context.getOrDefault(FacilioConstants.ContextNames.EXTENDED_MODULE_NAME,null);

        Map<String, List<Object>> queryParams= (Map<String, List<Object>>) context.getOrDefault(FacilioConstants.ContextNames.QUERY_PARAMS,null);
        int page=1,perPage=50;
        if(pagination!=null){
            page= (int) pagination.get("page");
            perPage= (int) pagination.get("perPage");
        }

        FacilioModule module = Constants.getModBean().getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module==null  ,"Invalid  module name:"+moduleName);
        FacilioField field = Constants.getModBean().getField(fieldName,moduleName);

        FacilioUtil.throwIllegalArgumentException(field==null  ,"Field  cannot be null for Form PickList");

        String lookupModuleName=null;
        if(field instanceof BaseLookupField) {
            lookupModuleName = ((BaseLookupField) field).getLookupModule().getName();
        }
        else if(Objects.equals(field.getName(),"siteId")){
            lookupModuleName="site";
        }
        else{
            throw new IllegalArgumentException("The Fields is Not a LookupFields");
        }

        if(StringUtils.isNotEmpty(extendedModuleName)){
            lookupModuleName = extendedModuleName;
        }

        Criteria serverCriteria = ticketStatusModuleSpecialHandlingCriteria(field,module);

        if(LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
            List<String> localSearchDisabled = Arrays.asList(FacilioConstants.ContextNames.USERS,FacilioConstants.ContextNames.READING_RULE_MODULE);
            context.put(FacilioConstants.ContextNames.IS_SPECIAL_MODULE,true);
            context.put(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(lookupModuleName, page, perPage, search, filter ,defaults));
            context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.PICK_LIST.name());
            context.put(FacilioConstants.PickList.LOCAL_SEARCH, !localSearchDisabled.contains(lookupModuleName));
        }
        else {
            FacilioContext pickListContext = V3Util.fetchPickList(lookupModuleName, filter, excludeParentFilter,clientCriteria,defaults, orderBy, orderType,search, page, perPage,withCount, queryParams, serverCriteria);
            context.put(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
            if (pickListContext.containsKey(FacilioConstants.ContextNames.META)) {
                context.put(FacilioConstants.ContextNames.META, pickListContext.get(FacilioConstants.ContextNames.META));
            }
        }
        return false;
    }

    private Criteria ticketStatusModuleSpecialHandlingCriteria(FacilioField field,FacilioModule module){
        Criteria serverCriteria = null;
        if (field.getName().equals("moduleState")) {
            serverCriteria = new Criteria();
            serverCriteria.addAndCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        } else if (field.getName().equals("approvalStatus")) {
            serverCriteria = new Criteria();
            serverCriteria.addAndCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", null, CommonOperators.IS_EMPTY));
        }
        return serverCriteria;
    }
}
