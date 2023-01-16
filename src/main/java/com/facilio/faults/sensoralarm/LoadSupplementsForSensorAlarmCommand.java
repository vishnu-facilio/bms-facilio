package com.facilio.faults.sensoralarm;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.faults.GenericLoadSupplementsV3;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class LoadSupplementsForSensorAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SupplementRecord> fetchLookupsList = GenericLoadSupplementsV3.getLookupList(
                FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM,
                Arrays.asList("resource", "severity")
        );
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
