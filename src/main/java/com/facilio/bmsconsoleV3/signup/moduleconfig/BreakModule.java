package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
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
    public void addData() throws Exception {
        try {
            addSystemButtons();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
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

        List<String> apps = Arrays.asList(
                FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.FSM_APP,
                FacilioConstants.ApplicationLinkNames.IWMS_APP
        );
        allView.setAppLinkNames(apps);

        return allView;
    }

    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule breakModule = modBean.getModule(FacilioConstants.ContextNames.BREAK);

        FacilioForm breakModuleForm = new FacilioForm();
        breakModuleForm.setDisplayName("New Break");
        breakModuleForm.setName("default_break_web");
        breakModuleForm.setModule(breakModule);
        breakModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        List<String> apps = Arrays.asList(
                FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.FSM_APP,
                FacilioConstants.ApplicationLinkNames.IWMS_APP
        );
        breakModuleForm.setAppLinkNamesForForm(apps);

        List<FormField> breakModuleFormFields = new ArrayList<>();

        breakModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 2));
        breakModuleFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        breakModuleFormFields.add(new FormField("breakType", FacilioField.FieldDisplayType.SELECTBOX, "Break Type", FormField.Required.REQUIRED,"breaktype", 3, 2));
        breakModuleFormFields.add(new FormField("shifts", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Applicable Shifts", FormField.Required.OPTIONAL, "shift", 4, 2));
        breakModuleFormFields.add(new FormField("breakTime", FacilioField.FieldDisplayType.DURATION, "Allowed Duration", FormField.Required.OPTIONAL, "duration", 5, 1));

        FormSection section = new FormSection("Default", 1, breakModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        breakModuleForm.setSections(Collections.singletonList(section));
        breakModuleForm.setIsSystemForm(true);
        breakModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(breakModuleForm);
    }

    private void addSystemButtons() throws Exception {


        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Add Break");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BREAK,create);

        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.BREAK);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.BREAK);
        SystemButtonApi.addBulkDeleteButton(FacilioConstants.ContextNames.BREAK);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.BREAK);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.BREAK);



    }
}

