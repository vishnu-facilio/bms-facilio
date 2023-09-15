package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class FloorModule extends BaseModuleConfig {

    public FloorModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.FLOOR);
    }

    @Override
    public void addData() throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, floorModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, floorModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(floorModule.getModuleId());
            stateFlowRuleContext.setModule(floorModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(floorModule);
            activeToInactive.setModuleId(floorModule.getModuleId());
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
//            FacilioForm defaultForm = new FacilioForm();
//            defaultForm.setName("standard");
//            defaultForm.setModule(floorModule);
//            defaultForm.setDisplayName("Standard");
//            defaultForm.setFormType(FacilioForm.FormType.WEB);
//            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
//            defaultForm.setShowInWeb(true);
//
//            FormSection section = new FormSection();
//            section.setName("Default Section");
//            section.setSectionType(FormSection.SectionType.FIELDS);
//            section.setShowLabel(true);
//
//            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(floorModule.getName()));
//            List<FormField> fields = new ArrayList<>();
//            fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//            fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));
//
//            fields.add(new FormField(fieldMap.get("building").getFieldId(), "building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
//
//            fields.add(new FormField(fieldMap.get("floorlevel").getFieldId(), "floorlevel", FacilioField.FieldDisplayType.NUMBER, "Floor Level", FormField.Required.OPTIONAL, 6, 1));
//
//            section.setFields(fields);
//            section.setSequenceNumber(1);
//
//            defaultForm.setSections(Collections.singletonList(section));
//            FormsAPI.createForm(defaultForm, floorModule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> floor = new ArrayList<FacilioView>();
        floor.add(getAllFloors().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FLOOR);
        groupDetails.put("views", floor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFloors() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Floors");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);

        FacilioForm defaultFloorForm = new FacilioForm();
        defaultFloorForm.setName("default_floor_web");
        defaultFloorForm.setModule(floorModule);
        defaultFloorForm.setDisplayName("Floor");
        defaultFloorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        defaultFloorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultFloorForm.setShowInWeb(true);

        List<FormField> defaultFloorFormFields = new ArrayList<>();
        defaultFloorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultFloorFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultFloorFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.REQUIRED,  "building",3, 2, true));
        defaultFloorFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",3, 3, true));
        defaultFloorFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 2));
        defaultFloorFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 4, 3));
        defaultFloorFormFields.add(new FormField("floorlevel", FacilioField.FieldDisplayType.NUMBER, "Floor Level", FormField.Required.OPTIONAL, 5, 2));
        defaultFloorFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",7, 2));

//        defaultFloorForm.setFields(defaultFloorFormFields);

        FormSection section = new FormSection("Default", 1, defaultFloorFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultFloorForm.setSections(Collections.singletonList(section));
        defaultFloorForm.setIsSystemForm(true);
        defaultFloorForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultFloorForm);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp);
        return scopeConfigList;
    }
}
