package com.facilio.readingkpi.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.storm.InstructionType;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Objects;

import static com.facilio.connected.CommonConnectedUtil.postConRuleHistoryInstructionToStorm;

public class ForwardSchKpiExecToStormCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Integer> types = ReadingKpiAPI.getFrequencyTypesToBeFetched();
        for (Integer type : types) {
            postInstructionToStormForFrequency(type);
        }

        return false;
    }

    private void postInstructionToStormForFrequency(Integer type) throws Exception {
        List<ReadingKPIContext> kpis = ReadingKpiAPI.getActiveScheduledKpisOfFrequencyType(type);
        long endTime = DateTimeUtil.getHourStartTime();
        long startTime = endTime - Objects.requireNonNull(NamespaceFrequency.valueOf(type), "Invalid frequency Type").getMs();
        postConRuleHistoryInstructionToStorm(kpis, startTime, endTime, null, false, InstructionType.LIVE_KPI_HISTORICAL);
    }


}
