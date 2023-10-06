package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ControlActionPage implements TemplatePageFactory{

    @Override
    public String getModuleName() {
        return FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject controlActionActivityWidgetParam = new JSONObject();
        controlActionActivityWidgetParam.put("activityModuleName", FacilioConstants.Control_Action.CONTROL_ACTION_ACTIVITY_MODULE_NAME);
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("controlactioncommands", "Command Results", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlactionControlfields", null, null)
                .addWidget("controlactionCommandswidget", "List of Commands", PageWidget.WidgetType.COMMANDS_LIST_VIEW, "webCommandsListView_29_12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("notesandcomments",null,null)
                .addWidget("widgetGroup", "Comments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlactionbasicdetails", "Actions", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlactionsummaryfields", null, null)
                .addWidget("controlactionsummaryfieldswidget", "Control Action", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME , app))
                .widgetDone()
                .sectionDone()
                .addSection("controlactionControlfields", null, null)
                .addWidget("controlactionControlfieldswidget", "Actions", PageWidget.WidgetType.ACTIONS_LIST_VIEW, "webActionsListView_32_12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlactioncriteria","Criteria",PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlactionCriteriafields", null, null)
                .addWidget("controlactionassetcriteriafieldswidget", "Asset Field Criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 0, 0, getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.ASSET, "assetCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactionsitecriteriafieldswidget", "Site Field Criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 6, 0, getSummaryCriteriaListWidgetDetails(FacilioConstants.ContextNames.SITE,"siteCriteriaId"),null)
                .widgetDone()
                .addWidget("controlactioncontrollercriteriafieldswidget", "Controller Field Criteria", PageWidget.WidgetType.CRITERIA_LIST_VIEW, "webCriteriaListView_28_6", 0, 28, getSummaryCriteriaListWidgetDetails("controllers","controllerCriteriaId"),null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("controlActionHistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("controlActionHistoryFields", null, null)
                .addWidget("controlActionHistory", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, controlActionActivityWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    public static JSONObject getSummaryCriteriaListWidgetDetails(String criteriaModuleName , String criteriaFieldName){
        JSONObject widgetDetails = new JSONObject();
        widgetDetails.put("criteriaModuleName",criteriaModuleName);
        widgetDetails.put("criteriaFieldName" , criteriaFieldName);
        return widgetDetails;
    }
    public static JSONObject getSummaryWidgetDetails(String moduleName , ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField controlActionType = moduleBean.getField("controlActionType", moduleName);
        FacilioField assetCategory = moduleBean.getField("assetCategory", moduleName);
        FacilioField startTime = moduleBean.getField("scheduledActionDateTime", moduleName);
        FacilioField revertTime = moduleBean.getField("revertActionDateTime", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, controlActionType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetCategory, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, startTime, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, revertTime, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 3, 1, 4);

        widgetGroup.setName("controlActionModuleDetails");
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
    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", FacilioConstants.Control_Action.CONTROL_ACTION_NOTES_MODULE_NAME);

        JSONObject attachmentsWidgetParam = new JSONObject();
        notesWidgetParam.put("attachmentsModuleName", FacilioConstants.Control_Action.CONTROL_ACTION_ATTACHMENT_MODULE_NAME);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
