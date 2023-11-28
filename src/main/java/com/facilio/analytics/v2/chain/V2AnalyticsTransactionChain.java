package com.facilio.analytics.v2.chain;

import com.facilio.analytics.v2.command.*;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsoleV3.commands.meter.GetUtilityTypeReadingsCommand;
import com.facilio.bmsconsoleV3.commands.reports.ConstructReportDeleteCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.weather.commands.GetWeatherReadingFieldsCommand;

public class V2AnalyticsTransactionChain
{
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }
    public static FacilioChain newV2FetchReportDataChain()
    {
        FacilioChain c = FacilioChain.getNonTransactionChain();
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
        c.addCommand(new FormatHeatMapDataCommand());
        c.addCommand(new GetTrendLineCommand());
        c.addCommand(new FormatForTimeDuration());
        c.addCommand(new FetchCriteriaReportCommand());
        c.addCommand(new FetchCustomBaselineData());
        return c;
    }
    public static FacilioChain getCHAnalyticsDataChain()throws Exception{
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2FetchAnalyticsReportDataCommand());
        return chain;
    }
//    public static FacilioChain getMySqlAnalyticsDataChain()throws Exception{
//        FacilioChain chain = FacilioChain.getNonTransactionChain();
//        chain.addCommand(new FetchReportDataCommand());
//        return chain;
//    }
    public static FacilioChain getCategoryModuleChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetModuleFromCategoryCommand());
        return chain;
    }
    public static FacilioChain getAnalyticsReportDataOldChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
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
    public static FacilioChain getReportWithDataChain(Boolean isDataNeeded)throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2GetAnalyticReportCommand());
        if(isDataNeeded == null || (isDataNeeded != null && isDataNeeded == Boolean.TRUE)){
            chain.addCommand(V2AnalyticsTransactionChain.newV2FetchReportDataChain());
        }
        return chain;
    }
    public static FacilioChain getAnalyticReportListChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2AnalyticsReportListCommand());
        return chain;
    }
    public static FacilioChain getReadingsFromCategoryChain(String type)throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        if(type != null && (type.equals("asset") || type.equals("fault"))) {
            chain.addCommand(new GetCategoryReadingsCommand());
        }else if(type != null && type.equals("meter")) {
            chain.addCommand(new GetUtilityTypeReadingsCommand());
        }else if(type != null && type.equals("weather")){
            chain.addCommand(new GetWeatherReadingFieldsCommand());
        }
        chain.addCommand(new V2GetReadingsFromCategoryCommand());
        return chain;
    }
    public static FacilioChain getCHCardAnalyticsCardData()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2FetchAnalyticDataCommand());
        chain.addCommand(new ApplyConditionalFormattingForCard());
        return chain;
    }
    public static FacilioChain getAnalyticsCardDataChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2ConstructCardCommand());
        return chain;
    }
    public static FacilioChain getAnalyticsKPIChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2AnalyticsGetKPIListCommand());
        return chain;
    }

    public static FacilioChain getReadingsForAlarmChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2FetchReadingFromAlarmCommand());
        return chain;
    }
    public static FacilioChain getAnalyticCardDataChain()throws Exception
    {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new V2GetAnalyticCardWidgetCommand());
        chain.addCommand(new V2FetchAnalyticDataCommand());
        chain.addCommand(new ApplyConditionalFormattingForCard());
        return chain;
    }


}

