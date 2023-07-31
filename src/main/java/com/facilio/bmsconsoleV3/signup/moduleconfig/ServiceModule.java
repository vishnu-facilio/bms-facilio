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

public class ServiceModule extends BaseModuleConfig{
    public ServiceModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> service = new ArrayList<FacilioView>();
        service.add(getAllServiceView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SERVICE);
        groupDetails.put("views", service);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllServiceView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getServiceModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service");
        allView.setSortFields(Arrays.asList(new SortField(name, true)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE);

        FacilioForm serviceModuleForm = new FacilioForm();
        serviceModuleForm.setDisplayName("Service");
        serviceModuleForm.setName("default_service_web");
        serviceModuleForm.setModule(serviceModule);
        serviceModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> serviceModuleFormFields = new ArrayList<>();
        serviceModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        serviceModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        serviceModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.NUMBER, "Duration(Hr)", FormField.Required.REQUIRED, 3, 1));
        serviceModuleFormFields.add(new FormField("paymentType", FacilioField.FieldDisplayType.SELECTBOX, "Payment Type", FormField.Required.REQUIRED, 4, 1));
        serviceModuleFormFields.add(new FormField("buyingPrice", FacilioField.FieldDisplayType.DECIMAL, "Buying Price", FormField.Required.OPTIONAL, 5, 1));
        serviceModuleFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price", FormField.Required.REQUIRED, 6, 1));

        FormSection section = new FormSection("Default", 1, serviceModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        serviceModuleForm.setSections(Collections.singletonList(section));
        serviceModuleForm.setIsSystemForm(true);
        serviceModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(serviceModuleForm);
    }

}
