package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddCriteriaFromControlActionTemplateCommand extends FacilioCommand {
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
        for(V3ControlActionTemplateContext controlActionTemplateContext : controlActionTemplateContextList){
            if(controlActionTemplateContext.getSiteCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.SITE,controlActionTemplateContext.getSiteCriteria());
                controlActionTemplateContext.setSiteCriteriaId(CriteriaAPI.addCriteria(controlActionTemplateContext.getSiteCriteria()));
            }
            if(controlActionTemplateContext.getAssetCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.ASSET,controlActionTemplateContext.getAssetCriteria());
                controlActionTemplateContext.setAssetCriteriaId(CriteriaAPI.addCriteria(controlActionTemplateContext.getAssetCriteria()));
            }
            if(controlActionTemplateContext.getControllerCriteria() != null){
                CriteriaAPI.updateConditionField(FacilioConstants.ContextNames.CONTROLLER,controlActionTemplateContext.getControllerCriteria());
                controlActionTemplateContext.setControllerCriteriaId(CriteriaAPI.addCriteria(controlActionTemplateContext.getControllerCriteria()));
            }

        }


        return false;
    }
}
