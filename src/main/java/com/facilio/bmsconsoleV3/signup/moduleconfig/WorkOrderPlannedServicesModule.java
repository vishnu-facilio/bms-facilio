package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WorkOrderPlannedServicesModule extends BaseModuleConfig {
    public WorkOrderPlannedServicesModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlannedServices = new ArrayList<FacilioView>();
        workOrderPlannedServices.add(getAllWorkOrderPlannedServices().setOrder(order++));
        workOrderPlannedServices.add(getWorkOrderPlannedServicesDetails().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderPlannedServices);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderPlannedServices() {
        FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedServicesModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All WorkOrder Planned Services");
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getWorkOrderPlannedServicesDetails() {
        FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedServicesModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("WorkOrder Planned Service Details");
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedServicesModule = modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);

        FacilioForm plannedServicesForm = new FacilioForm();
        plannedServicesForm.setDisplayName("WORK ORDER PLANNED SERVICES");
        plannedServicesForm.setName("default_workOrderPlannedServices_web");
        plannedServicesForm.setModule(plannedServicesModule);
        plannedServicesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        plannedServicesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> plannedServicesFormFields = new ArrayList<>();
        plannedServicesFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, 1, 2));
        plannedServicesFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.REQUIRED, 2, 3));
        plannedServicesFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.NUMBER, "Duration", FormField.Required.REQUIRED, 3, 2));
        FormField totalCost = new FormField("totalCost", FacilioField.FieldDisplayType.NUMBER, "Total Cost", FormField.Required.REQUIRED, 4, 3);
        totalCost.setIsDisabled(true);
        plannedServicesFormFields.add(totalCost);


        FormSection section = new FormSection("Default", 1, plannedServicesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedServicesForm.setSections(Collections.singletonList(section));
        plannedServicesForm.setIsSystemForm(true);
        plannedServicesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedServicesForm);

    }

}
