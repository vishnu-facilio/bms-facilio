package com.facilio.readingkpi;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.time.DateRange;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

import static com.facilio.bmsconsole.util.BmsJobUtil.scheduleOneTimeJobWithProps;
import static com.udojava.evalex.Expression.e;

@Getter
@Setter
@Log4j
public class ReadingKpiAction extends V3Action {
    private Long startTime;
    private Long endTime;
    private List<Long> historicalLoggerAssetIds;

    public String runHistorical() throws Exception {
        if (getId() == -1 || getStartTime() == -1 || getEndTime() == -1) {
            throw new IllegalArgumentException("Insufficient parameters for Historical Kpi calculation");
        }
        ReadingKPIContext kpi = ReadingKpiAPI.getReadingKpi(getId());
        if (kpi == null) {
            throw new IllegalArgumentException("Invalid formula ID for historical formula calculation");
        }

        JSONObject props = new JSONObject();
        props.put(FacilioConstants.ContextNames.START_TIME, startTime);
        props.put(FacilioConstants.ContextNames.END_TIME, endTime);
        props.put(FacilioConstants.ReadingKpi.IS_HISTORICAL, true);
        props.put(FacilioConstants.ContextNames.RESOURCE_LIST, historicalLoggerAssetIds);
        props.put(FacilioConstants.ReadingKpi.READING_KPI, kpi.getId());

        scheduleOneTimeJobWithProps(kpi.getId(), "ScheduledKpiHistoricalCalculationJob", 1, "facilio", props);
        setData(SUCCESS, "Historical KPI Calculation is started and will be notified when done");
        return SUCCESS;
    }

}
