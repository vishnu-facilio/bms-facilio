package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
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
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class SpaceModule extends BaseModuleConfig {
    public SpaceModule(){
        setModuleName(FacilioConstants.ContextNames.SPACE);
    }

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
//            FacilioForm defaultForm = new FacilioForm();
//            defaultForm.setName("standard");
//            defaultForm.setModule(spaceModule);
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
//            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(spaceModule.getName()));
//            List<FormField> fields = new ArrayList<>();
//            fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//            fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));
//
//            fields.add(new FormField(fieldMap.get("building").getFieldId(), "building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
//            fields.add(new FormField(fieldMap.get("floor").getFieldId(), "floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL, 6, 1));
//
//            fields.add(new FormField(fieldMap.get("spaceCategory").getFieldId(), "spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, 7, 1));
//
//            section.setFields(fields);
//            section.setSequenceNumber(1);
//
//            defaultForm.setSections(Collections.singletonList(section));
//            FormsAPI.createForm(defaultForm, spaceModule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> people = new ArrayList<FacilioView>();
        people.add(getAllSpaces().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SPACE);
        groupDetails.put("views", people);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSpaces() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Spaces");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

        FacilioForm defaultSpaceWebSiteForm = new FacilioForm();
        defaultSpaceWebSiteForm.setName("default_space_web_site");
        defaultSpaceWebSiteForm.setModule(spaceModule);
        defaultSpaceWebSiteForm.setDisplayName("Standard Form From Site");
        defaultSpaceWebSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultSpaceWebSiteForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebSiteForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebSiteFormFields = new ArrayList<>();
        defaultSpaceWebSiteFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site Associated", FormField.Required.REQUIRED,"site", 5, 1,true));
        defaultSpaceWebSiteFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebSiteFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));


        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebSiteFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebSiteFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebSiteFormSection = new FormSection("Default", 1, defaultSpaceWebSiteFormFields, false);
        defaultSpaceWebSiteFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebSiteForm.setSections(Collections.singletonList(defaultSpaceWebSiteFormSection));
        defaultSpaceWebSiteForm.setIsSystemForm(true);
        defaultSpaceWebSiteForm.setType(FacilioForm.Type.FORM);


        FacilioForm defaultSpaceWebBuildingForm = new FacilioForm();
        defaultSpaceWebBuildingForm.setName("default_space_web_building");
        defaultSpaceWebBuildingForm.setModule(spaceModule);
        defaultSpaceWebBuildingForm.setDisplayName("Standard Form From Building");
        defaultSpaceWebBuildingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultSpaceWebBuildingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebBuildingForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebBuildingFormFields = new ArrayList<>();
        defaultSpaceWebBuildingFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building Associated", FormField.Required.REQUIRED,"building", 5, 1,true));
        defaultSpaceWebBuildingFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebBuildingFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));


        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebBuildingFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebBuildingFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebBuildingFormSection = new FormSection("Default", 1, defaultSpaceWebBuildingFormFields, false);
        defaultSpaceWebBuildingFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebBuildingForm.setSections(Collections.singletonList(defaultSpaceWebBuildingFormSection));
        defaultSpaceWebBuildingForm.setIsSystemForm(true);
        defaultSpaceWebBuildingForm.setType(FacilioForm.Type.FORM);


        FacilioForm defaultSpaceWebFloorForm = new FacilioForm();
        defaultSpaceWebFloorForm.setName("default_space_web_floor");
        defaultSpaceWebFloorForm.setModule(spaceModule);
        defaultSpaceWebFloorForm.setDisplayName("Standard Form From Floor");
        defaultSpaceWebFloorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultSpaceWebFloorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebFloorForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebFloorFormFields = new ArrayList<>();
        defaultSpaceWebFloorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor Associated", FormField.Required.REQUIRED,"floor", 5, 1,true));
        defaultSpaceWebFloorFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebFloorFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));

        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebFloorFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebFloorFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebFloorFormSection = new FormSection("Default", 1, defaultSpaceWebFloorFormFields, false);
        defaultSpaceWebFloorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebFloorForm.setSections(Collections.singletonList(defaultSpaceWebFloorFormSection));
        defaultSpaceWebFloorForm.setIsSystemForm(true);
        defaultSpaceWebFloorForm.setType(FacilioForm.Type.FORM);

        FacilioForm defaultSpaceWebSpaceForm = new FacilioForm();
        defaultSpaceWebSpaceForm.setName("default_space_web_space");
        defaultSpaceWebSpaceForm.setModule(spaceModule);
        defaultSpaceWebSpaceForm.setDisplayName("Standard Form From Space");
        defaultSpaceWebSpaceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultSpaceWebSpaceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebSpaceForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebSpaceFormFields = new ArrayList<>();
        defaultSpaceWebSpaceFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebSpaceFormFields.add(getSpaceAssociatedField());
        defaultSpaceWebSpaceFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebSpaceFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));

        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebSpaceFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebSpaceFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebSpaceFormSection = new FormSection("Default", 1, defaultSpaceWebSpaceFormFields, false);
        defaultSpaceWebSpaceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebSpaceForm.setSections(Collections.singletonList(defaultSpaceWebSpaceFormSection));
        defaultSpaceWebSpaceForm.setIsSystemForm(true);
        defaultSpaceWebSpaceForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> spaceModuleForms = new ArrayList<>();
        spaceModuleForms.add(defaultSpaceWebSiteForm);
        spaceModuleForms.add(defaultSpaceWebBuildingForm);
        spaceModuleForms.add(defaultSpaceWebFloorForm);
        spaceModuleForms.add(defaultSpaceWebSpaceForm);

        return spaceModuleForms;
    }

    public static FormField getSpaceAssociatedField() {
        FormField parentSpace = new FormField("parentSpace", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Associated", FormField.Required.REQUIRED,"space", 5, 1,true);
        parentSpace.addToConfig("isFiltersEnabled", true); // Adding this as parent space is a special case with no field object
        parentSpace.addToConfig("lookupModuleName", "space");
        return parentSpace;
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

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("site");
        fieldNames.add("building");
        fieldNames.add("floor");
        fieldNames.add("sysCreatedTime");
        fieldNames.add("sysCreatedBy");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
