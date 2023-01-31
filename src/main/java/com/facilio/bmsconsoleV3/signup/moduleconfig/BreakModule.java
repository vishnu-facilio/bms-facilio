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

public class BreakModule extends BaseModuleConfig{
    public BreakModule(){
        setModuleName(FacilioConstants.ContextNames.BREAK);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> breakModule = new ArrayList<FacilioView>();
        breakModule.add(getAllBreakView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BREAK);
        groupDetails.put("views", breakModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBreakView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(ModuleFactory.getBreakModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Break");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

        return allView;
    }

    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule shiftModule = modBean.getModule(FacilioConstants.ContextNames.BREAK);

        FacilioForm shiftModuleForm = new FacilioForm();
        shiftModuleForm.setDisplayName("New Break");
        shiftModuleForm.setName("default_break_web");
        shiftModuleForm.setModule(shiftModule);
        shiftModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        shiftModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> breakModuleFormFields = new ArrayList<>();

        breakModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 2));
        breakModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        breakModuleFormFields.add(new FormField("breakType", FacilioField.FieldDisplayType.SELECTBOX, "Break Type", FormField.Required.REQUIRED,"breaktype", 3, 2));
        breakModuleFormFields.add(new FormField("shifts", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Applicable Shifts", FormField.Required.REQUIRED, "shift", 4, 2));
        breakModuleFormFields.add(new FormField("breakTime", FacilioField.FieldDisplayType.DURATION, "Allowed Duration", FormField.Required.OPTIONAL, "duration", 5, 1));

        FormSection section = new FormSection("Default", 1, breakModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        shiftModuleForm.setSections(Collections.singletonList(section));
        shiftModuleForm.setIsSystemForm(true);
        shiftModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(shiftModuleForm);
    }
}

