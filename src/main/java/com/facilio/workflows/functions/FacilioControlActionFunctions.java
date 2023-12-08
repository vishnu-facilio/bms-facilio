package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
public class FacilioControlActionFunctions {
    public Object createControlAction(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule controlActionModule = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);

        Long controlActionTemplateId = (Long) objects[0];
        List<Long> assetIdList = new ArrayList<>();
        if(objects[1] != null){
            assetIdList = (List<Long>) objects[1];
        }
        Long scheduleActionTime = (Long) objects[2];
        Long revertActionTime = -1L;
        if(objects.length == 4){
            revertActionTime = (Long) objects[3];
        }
        List<ModuleBaseWithCustomFields> records = new ArrayList<>();
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);



        Map<String, Object> asProperties = FieldUtil.getAsProperties(controlActionTemplateContext);
        V3ControlActionContext controlActionRecord = FieldUtil.getAsBeanFromMap(asProperties, V3ControlActionContext.class);
        List<PeopleContext> firstLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId,FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
        List<PeopleContext> secondLevelApprovalList = ControlActionAPI.getApprovalList(controlActionTemplateId,FacilioConstants.Control_Action.CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME);
        if(CollectionUtils.isNotEmpty(firstLevelApprovalList)){
            controlActionRecord.setFirstLevelApproval(firstLevelApprovalList);
        }
        if(CollectionUtils.isNotEmpty(secondLevelApprovalList)){
            controlActionRecord.setSecondLevelApproval(secondLevelApprovalList);
        }
        records.add(controlActionRecord);
        controlActionRecord.setControlActionTemplate(controlActionTemplateContext);
        controlActionRecord.setControlActionSourceType(V3ControlActionContext.ControlActionSourceTypeEnum.CONTROL_ACTION_TEMPLATE.getVal());
        controlActionRecord.setScheduledActionDateTime(scheduleActionTime);

        boolean revertOperationCheck = false;
        if(CollectionUtils.isNotEmpty(controlActionTemplateContext.getActionContextList())){
            for(V3ActionContext actionContext : controlActionTemplateContext.getActionContextList()){
                if(actionContext.getRevertActionValue() != null){
                    revertOperationCheck = true;
                    break;
                }
            }
        }
        if(revertOperationCheck && revertActionTime < 0){
            throw new IllegalArgumentException("Pls Provide Revert Action time");
        }
        if(revertOperationCheck && revertActionTime > System.currentTimeMillis()){
            controlActionRecord.setRevertActionDateTime(revertActionTime);
        }

        controlActionRecord.setControlActionStatus(V3ControlActionContext.ControlActionStatus.PUBLISHED.getVal());
        controlActionRecord.setControlActionTemplate(controlActionTemplateContext);
        if(CollectionUtils.isNotEmpty(assetIdList)){
            Condition condition = CriteriaAPI.getIdCondition(assetIdList,modBean.getModule(FacilioConstants.ContextNames.ASSET));
            if(controlActionTemplateContext.getAssetCriteriaId() != null){
                Criteria existingAssetCriteria = CriteriaAPI.getCriteria(controlActionTemplateContext.getAssetCriteriaId());
                existingAssetCriteria.addAndCondition(condition);
                controlActionRecord.setAssetCriteria(existingAssetCriteria);
            }
            else{
                Criteria criteria = new Criteria();
                criteria.addAndCondition(condition);
                controlActionRecord.setAssetCriteria(criteria);
            }

        }
        if(controlActionTemplateContext.getSiteCriteriaId() != null){
            controlActionRecord.setSiteCriteria(CriteriaAPI.getCriteria(controlActionTemplateContext.getSiteCriteriaId()));
        }
        if(controlActionTemplateContext.getControllerCriteriaId() != null){
            controlActionRecord.setControllerCriteria(CriteriaAPI.getCriteria(controlActionTemplateContext.getControllerCriteriaId()));
        }
        if(CollectionUtils.isEmpty(assetIdList) && controlActionTemplateContext.getAssetCriteriaId() != null){
            controlActionRecord.setAssetCriteria(CriteriaAPI.getCriteria(controlActionTemplateContext.getAssetCriteriaId()));
        }

        if(CollectionUtils.isNotEmpty(records)) {
            V3Util.createRecord(controlActionModule, records);
        }
        return "Control Action Created";
    }
}
