package com.facilio.mailtracking.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateMailRecordsModuleNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3OutgoingMailLogContext> outgoingMailContexts = Constants.getRecordList((FacilioContext) context);
        if(!CollectionUtils.isEmpty(outgoingMailContexts)) {
            ModuleBean modBean = Constants.getModBean();
            for(V3OutgoingMailLogContext record : outgoingMailContexts) {
                Long recordModId = record.getRecordsModuleId();
                if(recordModId != null && !recordModId.equals(-1L)) {
                    FacilioModule module =  modBean.getModule(record.getRecordsModuleId());
                    if(module != null) {
                        record.setRecordsModuleName(module.getDisplayName());
                    }
                }
            }
        }
        return false;
    }
}
