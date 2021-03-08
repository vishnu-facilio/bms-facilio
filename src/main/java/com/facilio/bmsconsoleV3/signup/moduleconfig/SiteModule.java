package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SiteModule extends SignUpData {

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, siteModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, siteModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(siteModule.getModuleId());
            stateFlowRuleContext.setModule(siteModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(siteModule);
            activeToInactive.setModuleId(siteModule.getModuleId());
            activeToInactive.setActivityType(EventType.STATE_TRANSITION);
            activeToInactive.setExecutionOrder(1);
            activeToInactive.setButtonType(1);
            activeToInactive.setFromStateId(activeStatus.getId());
            activeToInactive.setToStateId(inactiveStatus.getId());
            activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
            activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
            activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
            WorkflowRuleAPI.addWorkflowRule(activeToInactive);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addStateflowFieldsToExistingSites() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

        StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(siteModule);
        FacilioStatus active = TicketAPI.getStatus(siteModule, "active");

        SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
                .module(siteModule)
                .beanClass(SiteContext.class)
                .select(modBean.getAllFields(siteModule.getName()));
        SelectRecordsBuilder.BatchResult<SiteContext> batches = builder.getInBatches("ID", 100);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(modBean.getField("moduleState", siteModule.getName()));
        fields.add(modBean.getField("stateFlowId", siteModule.getName()));
        while (batches.hasNext()) {
            List<SiteContext> siteContexts = batches.get();
            if (CollectionUtils.isNotEmpty(siteContexts)) {
                for (SiteContext siteContext : siteContexts) {
                    if (siteContext.getStateFlowId() <= 0) {
                        siteContext.setModuleState(active);
                        siteContext.setStateFlowId(defaultStateFlow.getId());

                        UpdateRecordBuilder<SiteContext> updateRecordBuilder = new UpdateRecordBuilder<SiteContext>()
                                .module(siteModule)
                                .fields(fields)
                                .andCondition(CriteriaAPI.getIdCondition(siteContexts.stream().map(SiteContext::getId).collect(Collectors.toList()), siteModule));
                        updateRecordBuilder.update(siteContext);
                    }
                }
            }
        }
    }
}
