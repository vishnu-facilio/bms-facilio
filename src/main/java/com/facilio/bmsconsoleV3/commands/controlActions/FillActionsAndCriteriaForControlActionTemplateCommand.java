package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillActionsAndCriteriaForControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() == 0){
            return false;
        }
        List<V3ControlActionTemplateContext> controlActionTemplateContextList = (List<V3ControlActionTemplateContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionTemplateContextList)){
            return false;
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(V3ControlActionContext controlActionContextTemplate : controlActionTemplateContextList){
            List<V3ActionContext> actionContextList = ControlActionAPI.getActionOfControlAction(controlActionContextTemplate.getId());
            if(CollectionUtils.isNotEmpty(actionContextList)){
                for(V3ActionContext actionContext : actionContextList ){
                    actionContext.setReadingField(moduleBean.getField(actionContext.getReadingFieldId()));
                }
                controlActionContextTemplate.setActionContextList(actionContextList) ;
            }
            if(controlActionContextTemplate.getSiteCriteriaId() != null && controlActionContextTemplate.getSiteCriteriaId() > 0){
                controlActionContextTemplate.setSiteCriteria(CriteriaAPI.getCriteria(controlActionContextTemplate.getSiteCriteriaId()));
            }
            if(controlActionContextTemplate.getAssetCriteriaId() != null && controlActionContextTemplate.getAssetCriteriaId() > 0){
                controlActionContextTemplate.setAssetCriteria(CriteriaAPI.getCriteria(controlActionContextTemplate.getAssetCriteriaId()));
            }
            if(controlActionContextTemplate.getControllerCriteriaId() != null && controlActionContextTemplate.getControllerCriteriaId() > 0){
                controlActionContextTemplate.setControllerCriteria(CriteriaAPI.getCriteria(controlActionContextTemplate.getControllerCriteriaId()));
            }
            if(controlActionContextTemplate.getControlActionExecutionType() == V3ControlActionContext.ControlActionExecutionType.SANDBOX.getVal()){
                controlActionContextTemplate.setIsSandBox(true);
            }
        }
        return false;
    }
}
