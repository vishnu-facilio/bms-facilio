package com.facilio.analytics.v2.chain;

import com.facilio.analytics.v2.command.*;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsoleV3.commands.meter.GetUtilityTypeReadingsCommand;
import com.facilio.bmsconsoleV3.commands.reports.ConstructReportDeleteCommand;
import com.facilio.chain.FacilioChain;

public class V2AnalyticsTransactionChain
{
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }
    public static FacilioChain newV2FetchReportDataChain()
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReportDrilldownCommand());
        c.addCommand(new FilterFieldCommand());
        c.addCommand(new V2FetchAnalyticDataCommand());
        c.addCommand(new ConstructReportDataCommand());
        c.addCommand(new CalculateAggregationCommand());
        c.addCommand(new NewTransformReportDataCommand());
        c.addCommand(new CalculateAggregationCommand()); //For new ones created in Derivations
        c.addCommand(new FetchReportAdditionalInfoCommand());
        c.addCommand(new FetchResourcesCommand());
        c.addCommand(new HandleGroupByDataCommand());
        c.addCommand(new AddRegressionPointsCommand());
        c.addCommand(new FormatHeatMapDataCommand());
        c.addCommand(new GetTrendLineCommand());
        c.addCommand(new FormatForTimeDuration());
        c.addCommand(new FetchCriteriaReportCommand());
        c.addCommand(new FetchCustomBaselineData());
        return c;
    }
    public static FacilioChain getCHAnalyticsDataChain()throws Exception{
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2FetchAnalyticsReportDataCommand());
        return chain;
    }
    public static FacilioChain getMySqlAnalyticsDataChain()throws Exception{
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new FetchReportDataCommand());
        return chain;
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
        chain.addCommand(V2AnalyticsTransactionChain.newV2FetchReportDataChain());
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
        chain.addCommand(V2AnalyticsTransactionChain.newV2FetchReportDataChain());
        return chain;
    }
    public static FacilioChain getAnalyticReportListChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2AnalyticsReportListCommand());
        return chain;
    }
    public static FacilioChain getReadingsFromCategoryChain(String type)throws Exception
    {
        FacilioChain chain = getDefaultChain();
        if(type != null && type.equals("asset")) {
            chain.addCommand(new GetCategoryReadingsCommand());
        }else if(type != null && type.equals("meter")) {
            chain.addCommand(new GetUtilityTypeReadingsCommand());
        }
        chain.addCommand(new V2GetReadingsFromCategoryCommand());
        return chain;
    }
    public static FacilioChain getCHCardAnalyticsCardData()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2FetchAnalyticDataCommand());
        chain.addCommand(new ApplyConditionalFormattingForCard());
        return chain;
    }
    public static FacilioChain getAnalyticsCardDataChain()throws Exception
    {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new V2ConstructCardCommand());
        return chain;
    }


}

