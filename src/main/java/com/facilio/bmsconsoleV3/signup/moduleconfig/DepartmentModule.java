package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
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
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class DepartmentModule extends BaseModuleConfig{
    public DepartmentModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.DEPARTMENT);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> department = new ArrayList<FacilioView>();
        department.add(getAllDepartmentView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.DEPARTMENT);
        groupDetails.put("views", department);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDepartmentView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Departments");
        allView.setModuleName(FacilioConstants.ContextNames.DEPARTMENT);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule departmentModule = modBean.getModule(FacilioConstants.ContextNames.DEPARTMENT);

        FacilioForm departmentForm = new FacilioForm();
        departmentForm.setDisplayName("NEW DEPARTMENT");
        departmentForm.setName("default_department_web");
        departmentForm.setModule(departmentModule);
        departmentForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        departmentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> departmentFormFields = new ArrayList<>();
        departmentFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        departmentFormFields.add(new FormField("color", FacilioField.FieldDisplayType.TEXTBOX, "Color", FormField.Required.OPTIONAL, 2, 1));
        FormField noOfDesks = new FormField("noOfDesks", FacilioField.FieldDisplayType.NUMBER, "No of Desks", FormField.Required.OPTIONAL, 3, 3);
        noOfDesks.setHideField(true);
        departmentFormFields.add(noOfDesks);
        FormField noOfEmployee = new FormField("noOfEmployee", FacilioField.FieldDisplayType.NUMBER, "No of Employee", FormField.Required.OPTIONAL, 4, 3);
        noOfEmployee.setHideField(true);
        departmentFormFields.add(noOfEmployee);
//        departmentForm.setFields(departmentFormFields);

        FormSection departmentFormSection = new FormSection("Default", 1, departmentFormFields, false);
        departmentFormSection.setSectionType(FormSection.SectionType.FIELDS);
        departmentForm.setSections(Collections.singletonList(departmentFormSection));

        return Collections.singletonList(departmentForm);
    }
}
