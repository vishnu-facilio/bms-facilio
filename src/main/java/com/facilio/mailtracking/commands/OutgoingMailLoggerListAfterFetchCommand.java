package com.facilio.mailtracking.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class OutgoingMailLoggerListAfterFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3OutgoingMailLogContext> outgoingMailContexts = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(outgoingMailContexts)) {
            return true;
        }

        ModuleBean modBean = Constants.getModBean();
        for(V3OutgoingMailLogContext record : outgoingMailContexts) {
            Long recordModId = record.getRecordsModuleId();
            if(recordModId != null && !recordModId.equals(-1L)) {
                record.setRecordsModuleName(modBean.getModule(record.getRecordsModuleId()).getName());
            }
        }
        return false;
    }
}
