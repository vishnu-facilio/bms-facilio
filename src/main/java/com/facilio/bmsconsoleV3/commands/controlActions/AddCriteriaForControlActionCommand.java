package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class AddCriteriaForControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            if(controlActionContext.getSiteCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.SITE,controlActionContext.getSiteCriteria());
                controlActionContext.setSiteCriteriaId(CriteriaAPI.addCriteria(controlActionContext.getSiteCriteria()));
            }
            if(controlActionContext.getAssetCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.ASSET,controlActionContext.getAssetCriteria());
                controlActionContext.setAssetCriteriaId(CriteriaAPI.addCriteria(controlActionContext.getAssetCriteria()));
            }
            if(controlActionContext.getControllerCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME,controlActionContext.getControllerCriteria());
                controlActionContext.setControllerCriteriaId(CriteriaAPI.addCriteria(controlActionContext.getControllerCriteria()));
            }
        }

        return false;
    }
}
