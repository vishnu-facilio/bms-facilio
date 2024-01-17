package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

@Setter
@Getter
public class V2AnalyticsAPIAction extends V3Action {

    public Long categoryId;
    public String type;
    public String searchText;
    public Long readingRuleId;
    public Long resourceId;
    /**
     * below variables for lookup data
     */
    private int page = 1;
    private int perPage = 10;
    private String filters;
    private String moduleName;
    public String getModuleFromCategory()throws Exception
    {
        validateInput();
        FacilioChain chain = V2AnalyticsTransactionChain.getCategoryModuleChain();
        FacilioContext context = chain.getContext();
        context.put("categoryId", categoryId);
        context.put("type", type);
        chain.execute();
        setData("moduleName", context.get("moduleName"));
        return V3Action.SUCCESS;
    }

    public String readings()throws Exception
    {
        validateInput();
        FacilioChain chain = V2AnalyticsTransactionChain.getReadingsFromCategoryChain(type);
        FacilioContext context = chain.getContext();
        context.put("category", categoryId);
        context.put("type", type);
        context.put("searchText", searchText);
        List<FacilioModule> existingReadings = new ArrayList<>();
        context.put(FacilioConstants.ContextNames.MODULE_LIST, existingReadings);
        if(type != null && type.equals("asset")) {
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE,ModuleFactory.getAssetCategoryReadingRelModule());
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
        }
        else if(type != null && type.equals("meter")){
            context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, categoryId);
        }
        chain.execute();
        setData("fields", context.get("fields"));
        return V3Action.SUCCESS;
    }

    public String kpis()throws Exception
    {
        validateInput();
        FacilioChain chain = V2AnalyticsTransactionChain.getAnalyticsKPIChain();
        chain.getContext().put("categoryId", categoryId);
        chain.getContext().put("type", type);
        chain.getContext().put("searchText", searchText);
        chain.execute();
        if(chain.getContext().containsKey("kpis")) {
            setData("kpis", chain.getContext().get("kpis"));
        }
        return V3Action.SUCCESS;
    }

    public void validateInput()throws Exception
    {
        if(categoryId == null || categoryId < 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid CategoryId");
        }
        if(type == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Category Type");
        }
    }


    public String getFieldsFromAlarm()throws Exception {
        if (readingRuleId == null || readingRuleId < 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Reading Rule Id");
        }
        if (resourceId == null || resourceId < 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Resource Id");
        }
        FacilioChain chain = V2AnalyticsTransactionChain.getReadingsForAlarmChain();
        chain.getContext().put(FacilioConstants.ContextNames.READING_RULE_ID, readingRuleId);
        chain.getContext().put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
        chain.execute();
        if(chain.getContext().containsKey("measures"))
        {
            setData("measures", chain.getContext().get("measures"));
        }
        return V3Action.SUCCESS;
    }

    public String weatherReadings()throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getReadingsFromCategoryChain("weather");
        FacilioContext context = chain.getContext();
        context.put("type", "weather");
        context.put("searchText", searchText);
        chain.execute();
        setData("fields", context.get("fields"));
        return V3Action.SUCCESS;
    }

    public String lookupData()throws Exception
    {
        FacilioChain getChain = ReadOnlyChainFactoryV3.getRelatedModuleDataChain();
        FacilioContext chainContext = getChain.getContext();
        setLookupDataContext(chainContext);
        chainContext.put(FacilioConstants.ContextNames.IS_FROM_DASHBOARD, true);
        chainContext.put(FacilioConstants.ContextNames.RELATED_MODULE_NAME, getModuleName());
        getChain.execute();

        setData((JSONObject) chainContext.get(FacilioConstants.ContextNames.RESULT));
        if (chainContext.containsKey(FacilioConstants.ContextNames.META)) {
            setMeta((JSONObject) chainContext.get(FacilioConstants.ContextNames.META));
        }
        return V3Action.SUCCESS;
    }

    private void setLookupDataContext(Context context)throws Exception
    {
        Map<String, List<Object>> queryParams = new HashMap<>();
        queryParams.put("viewname", Arrays.asList("hidden-all"));
        queryParams.put("includeParentFilter", Arrays.asList("true"));
        queryParams.put("perPage", Arrays.asList(perPage+""));
        queryParams.put("withCount", Arrays.asList("true"));
        queryParams.put("page", Arrays.asList(page+""));
        if(filters != null && !filters.equals("")) {
            queryParams.put("filters", Arrays.asList(filters));
        }

        context.put("unAssociated", false);
        context.put(FacilioConstants.ContextNames.ID, 0);
        context.put(Constants.WITH_COUNT, Boolean.TRUE);
        context.put(FacilioConstants.ContextNames.CV_NAME, null);
        context.put(FacilioConstants.ContextNames.FILTERS, filters);
        context.put(FacilioConstants.ContextNames.ORDER_BY, null);
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, null);
        context.put(FacilioConstants.ContextNames.PAGE, page);
        context.put(FacilioConstants.ContextNames.PARAMS, null);
        context.put(FacilioConstants.ContextNames.PER_PAGE, this.getPerPage());
        context.put(FacilioConstants.ContextNames.SEARCH, this.getSearch());
        context.put(Constants.EXCLUDE_PARENT_CRITERIA, false);
        context.put(Constants.WITHOUT_CUSTOMBUTTONS, false);
        context.put(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        context.put(FacilioConstants.ContextNames.QUERY_PARAMS, queryParams);
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, null);
        context.put(FacilioConstants.ContextNames.IS_SUB_FORM_RECORD,false);
    }
}
