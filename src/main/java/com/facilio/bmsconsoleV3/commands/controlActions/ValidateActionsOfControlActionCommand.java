package com.facilio.bmsconsoleV3.commands.controlActions;
import com.amazonaws.services.dynamodbv2.xspec.NULL;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;
public class ValidateActionsOfControlActionCommand extends FacilioCommand {
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
        for(V3ControlActionContext controlActionContext : controlActionContextList) {
            if(CollectionUtils.isEmpty(controlActionContext.getActionContextList())){
                throw new IllegalArgumentException("Action can't be Empty");
            }
            for(V3ActionContext actionContext : controlActionContext.getActionContextList()){
                if(actionContext.getReadingFieldId() == null || actionContext.getReadingFieldId() <= 0){
                    throw new IllegalArgumentException("Action can't be Null");
                }
                if((actionContext.getScheduledActionOperatorType() != null && actionContext.getScheduledActionOperatorType() > 0) && actionContext.getScheduleActionValue() == null){
                    throw new IllegalArgumentException("Schedule Action Value Can't be Null");
                }
                if((actionContext.getRevertActionOperatorType() != null && actionContext.getRevertActionOperatorType() > 0 ) && actionContext.getRevertActionValue() == null){
                    throw new IllegalArgumentException("Revert Action Value Can't be Null");
                }
            }
        }
        return false;
    }
}