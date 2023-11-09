package com.facilio.bmsconsoleV3.commands.controlActions;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;
public class ValidateActionsOfControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionTemplateContext> controlActionTemplateContextList = (List<V3ControlActionTemplateContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionTemplateContextList)){
            return false;
        }
        for(V3ControlActionTemplateContext controlActionTemplateContext : controlActionTemplateContextList) {
            if(CollectionUtils.isEmpty(controlActionTemplateContext.getActionContextList())){
                throw new IllegalArgumentException("Action Can't be Empty");
            }
            for(V3ActionContext actionContext : controlActionTemplateContext.getActionContextList()){
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