package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SpaceContext;
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

public class SpaceModule extends SignUpData {

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, spaceModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, spaceModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(spaceModule.getModuleId());
            stateFlowRuleContext.setModule(spaceModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(spaceModule);
            activeToInactive.setModuleId(spaceModule.getModuleId());
            activeToInactive.setActivityType(EventType.STATE_TRANSITION);
            activeToInactive.setExecutionOrder(1);
            activeToInactive.setButtonType(1);
            activeToInactive.setFromStateId(activeStatus.getId());
            activeToInactive.setToStateId(inactiveStatus.getId());
            activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
            activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
            activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
            WorkflowRuleAPI.addWorkflowRule(activeToInactive);

            // adding form
            FacilioForm defaultForm = new FacilioForm();
            defaultForm.setName("standard");
            defaultForm.setModule(spaceModule);
            defaultForm.setDisplayName("Standard");
            defaultForm.setFormType(FacilioForm.FormType.WEB);
            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
            defaultForm.setShowInWeb(true);

            FormSection section = new FormSection();
            section.setName("Default Section");
            section.setSectionType(FormSection.SectionType.FIELDS);
            section.setShowLabel(true);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(spaceModule.getName()));
            List<FormField> fields = new ArrayList<>();
            fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
            fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
            fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
            fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));

            fields.add(new FormField(fieldMap.get("building").getFieldId(), "building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
            fields.add(new FormField(fieldMap.get("floor").getFieldId(), "floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL, 6, 1));

            fields.add(new FormField(fieldMap.get("spaceCategory").getFieldId(), "spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, 7, 1));

            section.setFields(fields);
            section.setSequenceNumber(1);

            defaultForm.setSections(Collections.singletonList(section));
            FormsAPI.createForm(defaultForm, spaceModule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addStateflowFieldsToExistingSpaces() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

        StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(spaceModule);
        FacilioStatus active = TicketAPI.getStatus(spaceModule, "active");

        SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
                .module(spaceModule)
                .beanClass(SpaceContext.class)
                .select(modBean.getAllFields(spaceModule.getName()));
        SelectRecordsBuilder.BatchResult<SpaceContext> batches = builder.getInBatches("ID", 100);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(modBean.getField("moduleState", spaceModule.getName()));
        fields.add(modBean.getField("stateFlowId", spaceModule.getName()));
        while (batches.hasNext()) {
            List<SpaceContext> spaceContexts = batches.get();
            if (CollectionUtils.isNotEmpty(spaceContexts)) {
                for (SpaceContext spaceContext : spaceContexts) {
                    if (spaceContext.getStateFlowId() <= 0) {
                        spaceContext.setModuleState(active);
                        spaceContext.setStateFlowId(defaultStateFlow.getId());

                        UpdateRecordBuilder<SpaceContext> updateRecordBuilder = new UpdateRecordBuilder<SpaceContext>()
                                .module(spaceModule)
                                .fields(fields)
                                .andCondition(CriteriaAPI.getIdCondition(spaceContexts.stream().map(SpaceContext::getId).collect(Collectors.toList()), spaceModule));
                        updateRecordBuilder.update(spaceContext);
                    }
                }
            }
        }
    }
}
