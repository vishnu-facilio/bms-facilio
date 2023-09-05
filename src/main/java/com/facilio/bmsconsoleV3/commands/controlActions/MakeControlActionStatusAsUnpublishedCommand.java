package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.Map;

public class MakeControlActionStatusAsUnpublishedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal());
        }
        return false;
    }
}
