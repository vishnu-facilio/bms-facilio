package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetActionAndCriteriaToControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() == 0){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            List<V3ActionContext> actionContextList = ControlActionAPI.getActionOfControlAction(controlActionContext.getId());
            if(CollectionUtils.isNotEmpty(actionContextList)){
                for(V3ActionContext actionContext : actionContextList ){
                    actionContext.setReadingField(moduleBean.getField(actionContext.getReadingFieldId()));
                }
                controlActionContext.setActionContextList(actionContextList) ;
            }
            if(controlActionContext.getSiteCriteriaId() != null && controlActionContext.getSiteCriteriaId() > 0){
                controlActionContext.setSiteCriteria(CriteriaAPI.getCriteria(controlActionContext.getSiteCriteriaId()));
            }
            if(controlActionContext.getAssetCriteriaId() != null && controlActionContext.getAssetCriteriaId() > 0){
                controlActionContext.setAssetCriteria(CriteriaAPI.getCriteria(controlActionContext.getAssetCriteriaId()));
            }
            if(controlActionContext.getControllerCriteriaId() != null && controlActionContext.getControllerCriteriaId() > 0){
                controlActionContext.setControllerCriteria(CriteriaAPI.getCriteria(controlActionContext.getControllerCriteriaId()));
            }
        }
        return false;
    }
}
