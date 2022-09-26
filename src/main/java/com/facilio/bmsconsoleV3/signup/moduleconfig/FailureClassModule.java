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

public class FailureClassModule extends BaseModuleConfig {
    public FailureClassModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FAILURE_CLASS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> failureClass = new ArrayList<FacilioView>();
        failureClass.add(getAllFailureClass().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FAILURE_CLASS);
        groupDetails.put("views", failureClass);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFailureClass() {

        FacilioModule failureClassModule = ModuleFactory.getFailureClassModule();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setColumnName("ID");
        id.setDataType(FieldType.NUMBER);
        id.setModule(failureClassModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Failure Class");
        allView.setModuleName(failureClassModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(id, false)));

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule failureClassModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CLASS);

        FacilioForm failureClassModuleForm = new FacilioForm();
        failureClassModuleForm.setDisplayName("Failure Class");
        failureClassModuleForm.setName("default_failureclass_web");
        failureClassModuleForm.setModule(failureClassModule);
        failureClassModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        failureClassModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> failureClassModuleFields = new ArrayList<>();
        failureClassModuleFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        failureClassModuleFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//        failureClassModuleForm.setFields(failureClassModuleFields);

        FormSection section = new FormSection("Default", 1, failureClassModuleFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        failureClassModuleForm.setSections(Collections.singletonList(section));
        failureClassModuleForm.setIsSystemForm(true);
        failureClassModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(failureClassModuleForm);
    }
}
