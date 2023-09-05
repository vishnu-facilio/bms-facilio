package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FetchReadingFieldDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ActionContext> actionContextList = (List<V3ActionContext>) recordMap.get(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(actionContextList)){
            return false;
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(V3ActionContext actionContext : actionContextList){
            FacilioField readingField = moduleBean.getField(actionContext.getReadingFieldId());
            actionContext.setReadingField(readingField);
        }
        return false;
    }
}
