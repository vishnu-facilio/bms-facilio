package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PlannedMaintenanceModule extends BaseModuleConfig{
    public PlannedMaintenanceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> plannedMaintenance = new ArrayList<FacilioView>();
        plannedMaintenance.add(getAllPlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getActivePlannedMaintenanceView().setOrder(order++));
        plannedMaintenance.add(getInActivePlannedMaintenanceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        groupDetails.put("views", plannedMaintenance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPlannedMaintenanceView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Planned Maintenance");

        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getInActivePlannedMaintenanceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getInActivePlannedMaintenanceCondition());
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("inactive");
        allView.setDisplayName("Unpublished");
        allView.setCriteria(criteria);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getActivePlannedMaintenanceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActivePlannedMaintenanceCondition());
        FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(plannedMaintenanceModule);
        FacilioView allView = new FacilioView();
        allView.setName("active");
        allView.setDisplayName("Published");
        allView.setCriteria(criteria);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    public static Condition getActivePlannedMaintenanceCondition() {
        FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField booleanField = new FacilioField();
        booleanField.setName("isActive");
        booleanField.setColumnName("IS_ACTIVE");
        booleanField.setDataType(FieldType.BOOLEAN);
        booleanField.setModule(module);

        Condition open = new Condition();
        open.setField(booleanField);
        open.setOperator(BooleanOperators.IS);
        open.setValue("true");

        return open;
    }

    public static Condition getInActivePlannedMaintenanceCondition() {
        FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
        FacilioField booleanField = new FacilioField();
        booleanField.setName("isActive");
        booleanField.setColumnName("IS_ACTIVE");
        booleanField.setDataType(FieldType.BOOLEAN);
        booleanField.setModule(module);

        Condition open = new Condition();
        open.setField(booleanField);
        open.setOperator(BooleanOperators.IS);
        open.setValue("false");

        return open;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenance = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);

        FacilioForm defaultPlannedMaintenanceForm = new FacilioForm();
        defaultPlannedMaintenanceForm.setDisplayName("Planned Maintenance");
        defaultPlannedMaintenanceForm.setName("default_plannedmaintenance_web");
        defaultPlannedMaintenanceForm.setModule(plannedMaintenance);
        defaultPlannedMaintenanceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultPlannedMaintenanceForm.setShowInWeb(true);
        defaultPlannedMaintenanceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> defaultPlannedMaintenanceFormFields = new ArrayList<>();
        defaultPlannedMaintenanceFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.REQUIRED, "tickettype", 1, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 2, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 4, 2));
        defaultPlannedMaintenanceFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 5, 3));
        defaultPlannedMaintenanceFormFields.add(new FormField("dueDuration", FacilioField.FieldDisplayType.DURATION, "Due Duration", FormField.Required.OPTIONAL, "duration", 6, 1));
        defaultPlannedMaintenanceFormFields.add(new FormField("estimatedWorkDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.OPTIONAL, "duration", 7, 1));
        FormField groups = new FormField("groups", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Team", FormField.Required.OPTIONAL, "groups", 8, 1);
        groups.addToConfig("isFiltersEnabled", true); // groups is special form field without actual field
        groups.addToConfig("lookupModuleName", "groups");
        defaultPlannedMaintenanceFormFields.add(groups);
        defaultPlannedMaintenanceFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 9, 1));
        defaultPlannedMaintenanceForm.setFields(defaultPlannedMaintenanceFormFields);

        FormSection section = new FormSection("Default", 1, defaultPlannedMaintenanceFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultPlannedMaintenanceForm.setSections(Collections.singletonList(section));
        defaultPlannedMaintenanceForm.setIsSystemForm(true);
        defaultPlannedMaintenanceForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultPlannedMaintenanceForm);
    }
}

