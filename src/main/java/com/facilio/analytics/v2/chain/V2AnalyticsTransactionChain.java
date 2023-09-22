package com.facilio.analytics.v2.chain;

import com.facilio.analytics.v2.command.GetModuleFromCategoryCommand;
import com.facilio.chain.FacilioChain;

public class V2AnalyticsTransactionChain
{
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getCategoryModuleChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new GetModuleFromCategoryCommand());
        return chain;
    }
}
