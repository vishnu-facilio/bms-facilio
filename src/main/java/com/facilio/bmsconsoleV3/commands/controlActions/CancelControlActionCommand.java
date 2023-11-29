package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule controlActionModule = moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<FacilioField> controlActionFieldList = moduleBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(controlActionFieldList);

        Long controlActionId = (Long) context.get("controlActionId");
        List<V3CommandsContext> commandsContextList = ControlActionAPI.getCommandsOfControlAction(controlActionId);
        for(V3CommandsContext commandsContext : commandsContextList){
            commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.CANCELED.getVal());
            ControlActionAPI.updateCommand(commandsContext);
            ControlActionAPI.addCommandActivity(V3CommandsContext.ControlActionCommandStatus.CANCELED.getValue(),commandsContext.getId());
        }
        V3ControlActionContext controlActionContext = ControlActionAPI.getControlAction(controlActionId);
        controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.REJECTED.getVal());
        ControlActionAPI.updateControlAction(controlActionContext);
        ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.REJECTED.getValue(),controlActionId);
        ControlActionAPI.deleteCommandExecutionJobOfControlAction(controlActionId);
        return false;
    }
}
