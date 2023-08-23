package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.util.VirtualMeterTemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillVirtualMeterTemplateDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VirtualMeterTemplateContext> virtualMeterTemplates = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(virtualMeterTemplates)){
            for(VirtualMeterTemplateContext vmt : virtualMeterTemplates){
                vmt.setReadings(VirtualMeterTemplateAPI.setVirtualMeterTemplateReadings(vmt.getId()));
            }
        }

        return false;
    }
}
