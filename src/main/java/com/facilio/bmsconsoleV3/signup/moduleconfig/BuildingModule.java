package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
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

public class BuildingModule extends BaseModuleConfig {

    public BuildingModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.BUILDING);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioStatus activeStatus = new FacilioStatus();
        activeStatus.setStatus("active");
        activeStatus.setDisplayName("Active");
        activeStatus.setTypeCode(1);
//        TicketAPI.addStatus(activeStatus, buildingModule);

        FacilioStatus inactiveStatus = new FacilioStatus();
        inactiveStatus.setStatus("inactive");
        inactiveStatus.setDisplayName("In Active");
        inactiveStatus.setTypeCode(2);
//        TicketAPI.addStatus(inactiveStatus, buildingModule);

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
//        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

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
//        WorkflowRuleAPI.addWorkflowRule(activeToInactive);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> building = new ArrayList<FacilioView>();
        building.add(getAllBuildings().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BUILDING);
        groupDetails.put("views", building);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBuildings() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Buildings");
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
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioForm defaultBuildingForm = new FacilioForm();
        defaultBuildingForm.setName("default_building_web");
        defaultBuildingForm.setModule(buildingModule);
        defaultBuildingForm.setDisplayName("Building");
        defaultBuildingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        defaultBuildingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultBuildingForm.setShowInWeb(true);

        List<FormField> defaultBuildingFormfields = new ArrayList<>();
        defaultBuildingFormfields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultBuildingFormfields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultBuildingFormfields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 3, 1));
        defaultBuildingFormfields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",4, 2));
        defaultBuildingFormfields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 4, 3));
        defaultBuildingFormfields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 5, 2));
        defaultBuildingFormfields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Total Built Area", FormField.Required.OPTIONAL, 5, 3));
        defaultBuildingFormfields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 6, 2));
        defaultBuildingFormfields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",7, 2));

//        defaultBuildingForm.setFields(defaultBuildingFormfields);

        FormSection Section = new FormSection("Default", 1, defaultBuildingFormfields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        defaultBuildingForm.setSections(Collections.singletonList(Section));
        defaultBuildingForm.setIsSystemForm(true);
        defaultBuildingForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultBuildingForm);
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
