package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillDefaultValuesForMetersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3MeterContext> meterContexts = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(meterContexts)) {

            for (V3MeterContext meterContext : meterContexts) {

                if(meterContext.getIsVirtual() == null) {
                    meterContext.setIsVirtual(false);
                }
            }
        }

        return false;
    }
}
