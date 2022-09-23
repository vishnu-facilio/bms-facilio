package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WorkOrderServiceModule extends BaseModuleConfig{
    public WorkOrderServiceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WO_SERVICE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderService = new ArrayList<FacilioView>();
        workOrderService.add(getAllWorkOrderService().setOrder(order++));
        workOrderService.add(getAllWorkOrderServiceDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderService);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderService() {
        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Service");
        allView.setModuleName(workOrderServiceModule.getName());

        return allView;
    }

    private static FacilioView getAllWorkOrderServiceDetailsView() {
        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Service Details");
        detailsView.setModuleName(workOrderServiceModule.getName());

        return detailsView;
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderServiceModule = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);

        FacilioForm workOrderServiceModuleForm = new FacilioForm();
        workOrderServiceModuleForm.setDisplayName("New Work Order Service");
        workOrderServiceModuleForm.setName("default_workorderService_web");
        workOrderServiceModuleForm.setModule(workOrderServiceModule);
        workOrderServiceModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workOrderServiceModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workOrderServiceModuleFormFields = new ArrayList<>();
        workOrderServiceModuleFormFields.add(new FormField("service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service", FormField.Required.REQUIRED, "Service", 1, 2,true));
        workOrderServiceModuleFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME,"Start Time", FormField.Required.OPTIONAL,2,2));
        workOrderServiceModuleFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME,"End Time", FormField.Required.OPTIONAL,2,3));
        workOrderServiceModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.TEXTBOX,"Duration", FormField.Required.OPTIONAL,3,2));
        workOrderServiceModuleFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.TEXTBOX, "Quantity", FormField.Required.OPTIONAL, 4, 2));

        FormSection workOrderServiceModuleFormSection = new FormSection("Default", 1, workOrderServiceModuleFormFields, false);
        workOrderServiceModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        workOrderServiceModuleForm.setSections(Collections.singletonList(workOrderServiceModuleFormSection));

        return Collections.singletonList(workOrderServiceModuleForm);

    }
}
