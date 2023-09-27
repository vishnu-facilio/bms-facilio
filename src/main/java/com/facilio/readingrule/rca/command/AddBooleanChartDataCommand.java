package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddBooleanChartDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<RCAScoreReadingContext> readingContexts = (List<RCAScoreReadingContext>) context.get(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS);
        DateRange dateRange = ReadingRuleRcaAPI.getDateRange(context);
        for(RCAScoreReadingContext readingContext : readingContexts) {
            List<Long[]> booleanChartData = NewReadingRuleAPI.getBooleanChartData(readingContext.getRcaRule().getId(), readingContext.getRcaFault().getResource().getId(), dateRange.getStartTime(), dateRange.getEndTime());
            readingContext.setBooleanChartData(booleanChartData);
        }
        context.put(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS, readingContexts);
        return false;
    }
}
