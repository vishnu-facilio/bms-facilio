package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
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

        FormField startTime = new FormField("startTime", FacilioField.FieldDisplayType.TIME, "Start Time", FormField.Required.REQUIRED, 3, 2);
        startTime.setConfig(timeConfig);
        ShiftModuleFormFields.add(startTime);
        FormField endTime = new FormField("endTime", FacilioField.FieldDisplayType.TIME, "End Time", FormField.Required.REQUIRED, 3, 2);
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

        ShiftModuleFormFields.add(new FormField("weekend", FacilioField.FieldDisplayType.WEEK_MATRIX, "Week Off", FormField.Required.REQUIRED, 5, 1 ));
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

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createShiftPage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private static List<PagesContext> createShiftPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Shift.SHIFT_ACTIVITY);

        return new ModulePages()
                .addPage("shift", "Shift", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("shiftSummary", "Summary", PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("shiftSummaryFields", null, null)
                .addWidget("shiftSummaryFieldsWidget", "Shift Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ContextNames.SHIFT))
                .widgetDone()
                .sectionDone()
                .addSection("shiftWeekOff", null, null)
                .addWidget("shiftWeekOffWidget", "Week Off", PageWidget.WidgetType.WEEK_OFF, "webWeekOff_7_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("shiftHistory", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                .pageDone().getCustomPages();


    }


    private static JSONObject getSummaryWidgetDetails(ApplicationContext app,String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> shiftFields = moduleBean.getAllFields(FacilioConstants.ContextNames.SHIFT);
        Map<String, FacilioField> shiftFieldsMap = FieldFactory.getAsMap(shiftFields);


        FacilioField description = shiftFieldsMap.get("description");
        FacilioField startTime = shiftFieldsMap.get("startTime");
        FacilioField endTime = shiftFieldsMap.get("endTime");
        FacilioField associatedBreaks = shiftFieldsMap.get("associatedBreaks");
        FacilioField associatedEmployees = shiftFieldsMap.get("associatedEmployees");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, startTime, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, endTime, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, associatedBreaks, 2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup, associatedEmployees, 2,4,1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);


        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }


}

