package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2AnalyticsAPIAction extends V3Action {

    public Long categoryId;
    public String type;
    public String getModuleFromCategory()throws Exception
    {
        if(categoryId == null || categoryId < 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid CategoryId");
        }
        if(type == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Category Type");
        }
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
        FacilioChain chain = V2AnalyticsTransactionChain.getReadingsFromCategoryChain();
        FacilioContext context = chain.getContext();
        context.put("category", categoryId);
        context.put("type", type);
        chain.execute();
        setData("fields", context.get("fields"));
        return V3Action.SUCCESS;
    }
}
