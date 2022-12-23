package com.facilio.readingkpi;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.storm.InstructionType;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.List;

import static com.facilio.bmsconsole.util.BmsJobUtil.scheduleOneTimeJobWithProps;

@Getter
@Setter
@Log4j
public class ReadingKpiAction extends V3Action {
    private Long recordId;
    private Long startTime;
    private Long endTime;
    private List<Long> historicalLoggerAssetIds;

    public String runHistorical() throws Exception {
        ReadingKPIContext kpi = validatePayload();
        try {
            if (kpi.getKpiType() == KPIType.SCHEDULED.getIndex()) {
                JSONObject props = new JSONObject();
                props.put(FacilioConstants.ContextNames.START_TIME, startTime);
                props.put(FacilioConstants.ContextNames.END_TIME, endTime);
                props.put(FacilioConstants.ReadingKpi.IS_HISTORICAL, true);
                props.put(FacilioConstants.ContextNames.RESOURCE_LIST, historicalLoggerAssetIds);
                props.put(FacilioConstants.ReadingKpi.READING_KPI, getRecordId());

                scheduleOneTimeJobWithProps(ReadingKpiLoggerAPI.getNextJobId(), FacilioConstants.ReadingKpi.READING_KPI_HISTORICAL_JOB, 1, "facilio", props);
                setData(SUCCESS, "Historical KPI Calculation is started and will be notified when done");

            } else {
                FacilioChain runStormHistorical = TransactionChainFactory.initiateStormInstructionExecChain();
                FacilioContext context = runStormHistorical.getContext();
                context.put("type", InstructionType.LIVE_KPI_HISTORICAL.getIndex());
                
                JSONObject instructionData = new JSONObject();
                instructionData.put("recordId", getRecordId());
                instructionData.put("startTime", getStartTime());
                instructionData.put("endTime", getEndTime());
                instructionData.put("assetIds", getHistoricalLoggerAssetIds());
                context.put("data", instructionData);
                runStormHistorical.execute();

                setData(SUCCESS, "Instruction Processing has begun");
            }
            return SUCCESS;
        } catch (Exception userException) {
            setData("Failed", userException.getMessage());
            throw userException;
        }
    }


    public ReadingKPIContext validatePayload() throws Exception {
        if (getId() == -1 || getStartTime() == -1 || getEndTime() == -1) {
            throw new IllegalArgumentException("Insufficient parameters for Historical Kpi calculation");
        }
        ReadingKPIContext kpi = ReadingKpiAPI.getReadingKpi(getRecordId());
        if (kpi == null) {
            throw new IllegalArgumentException("Invalid formula ID for historical formula calculation");
        }
        return kpi;
    }
}
