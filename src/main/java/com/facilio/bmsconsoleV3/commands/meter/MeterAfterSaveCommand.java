package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class MeterAfterSaveCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3MeterContext> v3meterList = (List<V3MeterContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.Meter.METER));
        if(v3meterList != null && !v3meterList.isEmpty())
        {
            V3MeterContext meter = v3meterList.get(0);
            context.put(FacilioConstants.ContextNames.RECORD_ID,meter.getId());
            context.put(FacilioConstants.ContextNames.PARENT_ID,meter.getId());
        }

        return false;
    }
}
