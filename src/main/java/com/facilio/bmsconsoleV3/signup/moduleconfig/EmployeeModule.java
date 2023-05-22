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
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class EmployeeModule extends BaseModuleConfig{
    public EmployeeModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.EMPLOYEE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> employee = new ArrayList<FacilioView>();
        employee.add(getAllHiddenEmployees().setOrder(order++));
        employee.add(getAllEmployees().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.EMPLOYEE);
        groupDetails.put("views", employee);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));
        allView.setHidden(true);
        return allView;
    }

    private static FacilioView getAllEmployees() {

        FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-employees");
        allView.setDisplayName("All Employees");
        allView.setModuleName(employeeModule.getName());
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule employeeModule = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);

        FacilioForm employeeContactForm = new FacilioForm();
        employeeContactForm.setDisplayName("NEW EMPLOYEE");
        employeeContactForm.setName("default_employee_web");
        employeeContactForm.setModule(employeeModule);
        employeeContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        employeeContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> employeeContactFormFields = new ArrayList<>();
        employeeContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        employeeContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        employeeContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        employeeContactFormFields.add(new FormField("isAssignable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Assignable", FormField.Required.OPTIONAL, 4, 2));
        employeeContactFormFields.add(new FormField("isLabour", FacilioField.FieldDisplayType.DECISION_BOX, "Is Labour", FormField.Required.OPTIONAL, 5, 3));
//        employeeContactForm.setFields(employeeContactFormFields);

        FormSection section = new FormSection("Default", 1, employeeContactFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        employeeContactForm.setSections(Collections.singletonList(section));
        employeeContactForm.setIsSystemForm(true);
        employeeContactForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(employeeContactForm);
    }

}
