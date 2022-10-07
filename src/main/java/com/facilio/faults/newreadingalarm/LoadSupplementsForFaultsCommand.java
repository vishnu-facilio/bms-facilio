package com.facilio.faults.newreadingalarm;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.faults.GenericLoadSupplementsV3;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class LoadSupplementsForFaultsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SupplementRecord> fetchLookupsList = GenericLoadSupplementsV3.getLookupList(
                FacilioConstants.ContextNames.NEW_READING_ALARM,
                Arrays.asList("rule", "resource", "severity", "readingAlarmCategory", "readingAlarmAssetCategory")
        );
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}