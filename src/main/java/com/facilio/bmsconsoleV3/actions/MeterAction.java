package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MeterAction extends V3Action {

    private long id;
    private String utilityTypeModuleName;

    public String getMeterMonthlyConsumption() throws Exception {
        FacilioChain meterMonthlyConsumption = TransactionChainFactoryV3.getMeterMonthlyConsumptionChain();
        FacilioContext context = meterMonthlyConsumption.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.Meter.UTILITY_TYPE, utilityTypeModuleName);
        meterMonthlyConsumption.execute();
        double thisMonthConsumption = (double) context.get("thisMonthConsumption");
        double lastMonthConsumption = (double) context.get("lastMonthConsumption");
        double percentDifference = (double) context.get("percentDifference");
        int scale = (int) context.get("scale");
        setData("thisMonthConsumption",thisMonthConsumption);
        setData("lastMonthConsumption",lastMonthConsumption);
        setData("percentDifference",percentDifference);
        setData("scale",scale);
        setData("readingName",context.get("readingName"));
        return SUCCESS;
    }

    public String getMeterYearlyConsumption() throws Exception {
        FacilioChain meterYearlyConsumption = TransactionChainFactoryV3.getMeterYearlyConsumptionChain();
        FacilioContext context = meterYearlyConsumption.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.Meter.UTILITY_TYPE, utilityTypeModuleName);
        meterYearlyConsumption.execute();
        double thisYearConsumption = (double) context.get("totalConsumption");
        double lastYearConsumption = (double) context.get("lastYearConsumption");
        double percentDifference = (double) context.get("percentDifference");
        int scale = (int) context.get("scale");
        setData("totalConsumption",thisYearConsumption);
        setData("lastYearConsumption",lastYearConsumption);
        setData("percentDifference",percentDifference);
        setData("scale",scale);
        setData("readingName",context.get("readingName"));
        return SUCCESS;
    }

    public String getMeterPeak() throws Exception {
        FacilioChain meterPeak = TransactionChainFactoryV3.getMeterPeakChain();
        FacilioContext context = meterPeak.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.Meter.UTILITY_TYPE, utilityTypeModuleName);
        meterPeak.execute();
        double percentDifference = (double) context.get("percentDifference");
        int scale = (int) context.get("scale");
        if(context.get("peakDemand") != null) {
            setData("peakDemand",context.get("peakDemand"));
        }
        if(context.get("lastYearPeakDemand") != null) {
            setData("lastYearPeakDemand",context.get("lastYearPeakDemand"));
        }
        setData("percentDifference",percentDifference);
        setData("scale",scale);
        setData("readingName",context.get("readingName"));
        setData("widgetName",context.get("widgetName"));
        if(context.get("peakTime") != null) {
            setData("peakTime", context.get("peakTime"));
        }
        if(context.get("lastYearPeakTime") != null) {
            setData("lastYearPeakTime", context.get("lastYearPeakTime"));
        }
        return SUCCESS;
    }
}
