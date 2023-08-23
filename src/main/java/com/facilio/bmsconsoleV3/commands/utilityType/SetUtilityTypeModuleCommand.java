package com.facilio.bmsconsoleV3.commands.utilityType;

import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SetUtilityTypeModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3UtilityTypeContext> v3utilityTypeContexts = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isEmpty(v3utilityTypeContexts)) {
            return false;
        }

        for (V3UtilityTypeContext v3utilityTypeContext : v3utilityTypeContexts) {
            if (v3utilityTypeContext.getMeterModuleID() != null && v3utilityTypeContext.getMeterModuleID() > 0L) {
                FacilioModule module = Constants.getModBean().getModule(v3utilityTypeContext.getMeterModuleID());
                if (module != null) {
                    v3utilityTypeContext.setModuleName(module.getName());
                }
            }
        }

        return false;
    }
}
