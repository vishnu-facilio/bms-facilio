package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioStatus activeStatus = new FacilioStatus();
        activeStatus.setStatus("active");
        activeStatus.setDisplayName("Active");
        activeStatus.setTypeCode(1);
        TicketAPI.addStatus(activeStatus, buildingModule);

        FacilioStatus inactiveStatus = new FacilioStatus();
        inactiveStatus.setStatus("inactive");
        inactiveStatus.setDisplayName("In Active");
        inactiveStatus.setTypeCode(2);
        TicketAPI.addStatus(inactiveStatus, buildingModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(buildingModule.getModuleId());
        stateFlowRuleContext.setModule(buildingModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
        activeToInactive.setName("Mark as Inactive");
        activeToInactive.setModule(buildingModule);
        activeToInactive.setModuleId(buildingModule.getModuleId());
        activeToInactive.setActivityType(EventType.STATE_TRANSITION);
        activeToInactive.setExecutionOrder(1);
        activeToInactive.setButtonType(1);
        activeToInactive.setFromStateId(activeStatus.getId());
        activeToInactive.setToStateId(inactiveStatus.getId());
        activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
        activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
        WorkflowRuleAPI.addWorkflowRule(activeToInactive);
    }

    public static void addStateflowFieldsToExistingBuildings() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(buildingModule);
        FacilioStatus active = TicketAPI.getStatus(buildingModule, "active");

        SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
                .module(buildingModule)
                .beanClass(BuildingContext.class)
                .select(modBean.getAllFields(buildingModule.getName()));
        SelectRecordsBuilder.BatchResult<BuildingContext> batches = builder.getInBatches("ID", 100);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(modBean.getField("moduleState", buildingModule.getName()));
        fields.add(modBean.getField("stateFlowId", buildingModule.getName()));
        while (batches.hasNext()) {
            List<BuildingContext> buildingContexts = batches.get();
            if (CollectionUtils.isNotEmpty(buildingContexts)) {
                for (BuildingContext buildingContext : buildingContexts) {
                    if (buildingContext.getStateFlowId() <= 0) {
                        buildingContext.setModuleState(active);
                        buildingContext.setStateFlowId(defaultStateFlow.getId());

                        UpdateRecordBuilder<BuildingContext> updateRecordBuilder = new UpdateRecordBuilder<BuildingContext>()
                                .module(buildingModule)
                                .fields(fields)
                                .andCondition(CriteriaAPI.getIdCondition(buildingContexts.stream().map(BuildingContext::getId).collect(Collectors.toList()), buildingModule));
                        updateRecordBuilder.update(buildingContext);
                    }
                }
            }
        }
    }
}
