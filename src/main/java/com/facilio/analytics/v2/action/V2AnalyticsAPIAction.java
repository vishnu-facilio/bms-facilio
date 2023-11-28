package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class V2AnalyticsAPIAction extends V3Action {

    public Long categoryId;
    public String type;
    public String searchText;
    public Long readingRuleId;
    public Long resourceId;
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
}
