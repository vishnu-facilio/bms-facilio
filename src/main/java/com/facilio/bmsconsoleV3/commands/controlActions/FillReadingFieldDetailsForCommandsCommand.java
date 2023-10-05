package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillReadingFieldDetailsForCommandsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3CommandsContext> commandsContextList = (List<V3CommandsContext>) recordMap.get(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        if(CollectionUtils.isEmpty(commandsContextList)){
            return false;
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(V3CommandsContext commandsContext : commandsContextList){
            if(commandsContext.getFieldId() == null){
                continue;
            }
            FacilioField field = moduleBean.getField(commandsContext.getFieldId());
            commandsContext.setReadingField(field);
        }
        return false;
    }
}
