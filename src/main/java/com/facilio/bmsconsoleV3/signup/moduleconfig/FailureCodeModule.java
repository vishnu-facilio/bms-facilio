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

public class FailureCodeModule extends BaseModuleConfig {
    public FailureCodeModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.FAILURE_CODE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> failureCode = new ArrayList<FacilioView>();
        failureCode.add(getAllFailureCodes().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FAILURE_CODE);
        groupDetails.put("views", failureCode);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllFailureCodes() {

        FacilioModule failureCodeModule = ModuleFactory.getFailureCodeModule();

        FacilioField id = new FacilioField();
        id.setName("id");
        id.setColumnName("ID");
        id.setDataType(FieldType.NUMBER);
        id.setModule(failureCodeModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Failure Codes");
        allView.setModuleName(failureCodeModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(id, false)));

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule failureCodeModule = modBean.getModule(FacilioConstants.ContextNames.FAILURE_CODE);

        FacilioForm failureCodeModuleForm = new FacilioForm();
        failureCodeModuleForm.setDisplayName("Failure Code");
        failureCodeModuleForm.setName("default_failurecode_web");
        failureCodeModuleForm.setModule(failureCodeModule);
        failureCodeModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        failureCodeModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> failureCodeModuleFormFields = new ArrayList<>();
        failureCodeModuleFormFields.add(new FormField("code", FacilioField.FieldDisplayType.TEXTBOX, "Code", FormField.Required.REQUIRED, 1, 1));
        failureCodeModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//        failureCodeModuleForm.setFields(failureCodeModuleFormFields);

        FormSection section = new FormSection("Default", 1, failureCodeModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        failureCodeModuleForm.setSections(Collections.singletonList(section));
        failureCodeModuleForm.setIsSystemForm(true);
        failureCodeModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(failureCodeModuleForm);
    }
}
