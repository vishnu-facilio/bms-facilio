package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class ShiftModule extends BaseModuleConfig{
    public ShiftModule(){
        setModuleName(FacilioConstants.ContextNames.SHIFT);
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
        ArrayList<FacilioView> shift = new ArrayList<FacilioView>();
        shift.add(getAllShiftView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SHIFT);
        groupDetails.put("views", shift);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllShiftView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("NAME");
        name.setModule(ModuleFactory.getShiftModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Shift(s)");
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

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule shiftModule = modBean.getModule(FacilioConstants.ContextNames.SHIFT);

        FacilioForm shiftModuleForm = new FacilioForm();
        shiftModuleForm.setDisplayName("NEW SHIFT");
        shiftModuleForm.setName("default_shift_web");
        shiftModuleForm.setModule(shiftModule);
        shiftModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        List<String> apps = Arrays.asList(
                FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.FSM_APP,
                FacilioConstants.ApplicationLinkNames.IWMS_APP
        );
        shiftModuleForm.setAppLinkNamesForForm(apps);

        List<FormField> ShiftModuleFormFields = new ArrayList<>();

        JSONObject timeConfig = new JSONObject();
        timeConfig.put("step", "00:30");
        JSONObject colorPickerConfig = new JSONObject();
        JSONArray predefineColors = new JSONArray();
        String[] predefineColorsArr = {"#B1FFF4","#99D5FF", "#FFBDBD", "#CFC9FF", "#FDBCFF", "#FFADD8", "#BDFFBB", "#F1FFB9", "#FFD39F", "#FFB9A2", "#D0FFF8", "#B8E1FF", "#FFDFDF", "#DDD8FF", "#FED7FF", "#FFD4EB", "#DBFFDA", "#F8FFDA", "#FFECD5", "#FFE7DF"};
        for (String color:predefineColorsArr) {
            predefineColors.add(color);
        }
        colorPickerConfig.put("predefineColors",predefineColors);

        ShiftModuleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 2));

        FormField colorCode = new FormField("colorCode", FacilioField.FieldDisplayType.COLOR_PICKER, "Color Code", FormField.Required.REQUIRED, 1, 2);
        colorCode.setConfig(colorPickerConfig);
        ShiftModuleFormFields.add(colorCode);

        FormField descField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        ShiftModuleFormFields.add(descField);

        FormField startTime = new FormField("startTime", FacilioField.FieldDisplayType.TIME, "Start", FormField.Required.REQUIRED, 3, 2);
        startTime.setConfig(timeConfig);
        ShiftModuleFormFields.add(startTime);
        FormField endTime = new FormField("endTime", FacilioField.FieldDisplayType.TIME, "End", FormField.Required.REQUIRED, 3, 2);
        endTime.setConfig(timeConfig);
        ShiftModuleFormFields.add(endTime);

        FormField isDefaultField = new FormField("isDefault", FacilioField.FieldDisplayType.DECISION_BOX, "Is Default", FormField.Required.OPTIONAL, 4, 2);
        isDefaultField.setHideField(true);
        isDefaultField.setValue(String.valueOf(false));
        ShiftModuleFormFields.add(isDefaultField);

        FormField isActiveField = new FormField("isActive", FacilioField.FieldDisplayType.DECISION_BOX, "Is Active", FormField.Required.OPTIONAL, 4, 2);
        isActiveField.setHideField(true);
        isActiveField.setValue(String.valueOf(true));
        ShiftModuleFormFields.add(isActiveField);

        ShiftModuleFormFields.add(new FormField("weekend", FacilioField.FieldDisplayType.WEEK_MATRIX, "Days", FormField.Required.REQUIRED, 5, 1 ));
        shiftModuleForm.setFields(ShiftModuleFormFields);

        FormSection section = new FormSection("Default", 1, ShiftModuleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        shiftModuleForm.setSections(Collections.singletonList(section));
        shiftModuleForm.setIsSystemForm(true);
        shiftModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(shiftModuleForm);
    }
    private void addSystemButtons() throws Exception {


        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Add Shift");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SHIFT,create);

        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.SHIFT);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.SHIFT);
        SystemButtonApi.addBulkDeleteButton(FacilioConstants.ContextNames.SHIFT);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.SHIFT);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.SHIFT);



    }

}

