package com.facilio.analytics.v2.chain;

import com.facilio.analytics.v2.command.*;
import com.facilio.bmsconsole.commands.AddOrUpdateReportCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.reports.ConstructReportDeleteCommand;
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
    public static FacilioChain getAnalyticsReportDataOldChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2CreateOldAnalyticsReportCommand());
        chain.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
        return chain;
    }
    public static FacilioChain getCREDAnalyticsReportChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2PopulateAnalyticsDataCommand());
        chain.addCommand(new V2CreateOldAnalyticsReportCommand());
        chain.addCommand(new AddOrUpdateReportCommand());
        chain.addCommand(new V2AddNewAnalyticReportCommand());
        return chain;
    }
    public static FacilioChain getDeleteAnalyticsReportChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2AddNewAnalyticReportCommand());
        chain.addCommand(new ConstructReportDeleteCommand());
        return chain;
    }
    public static FacilioChain getReportWithDataChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2GetAnalyticReportCommand());
        chain.addCommand(ReadOnlyChainFactory.newFetchReportDataChain());
        return chain;
    }
    public static FacilioChain getAnalyticReportListChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2AnalyticsReportListCommand());
        return chain;
    }
}

