package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobPlanModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.JOB_PLAN;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        JSONObject multiresourceWidgetParam = new JSONObject();
        multiresourceWidgetParam.put("summaryWidgetName", "multiResourceWidget");
        multiresourceWidgetParam.put("module", "\"" + jobPlanModule + "\"");

        org.json.simple.JSONObject historyWidgetParam = new org.json.simple.JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY);

        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Job Plan Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()


                .addSection("taskSections", null, null)
                .addWidget("taskSectionWidget", "Task List", PageWidget.WidgetType.JOBPLAN_TASK_SECTIONS, "flexiblewebjobplantasksectionwidget_5", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("legislations", null, null)
                .addWidget("legislationsWidget", "Legislation, Regulations and Guidance", PageWidget.WidgetType.JOBPLAN_SFG_LEGISLATIONS, "flexibleweblegislationsWidget_5", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plan", "Plans", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("planSection", null, null)
                .addWidget("plansWidget", "Plans", PageWidget.WidgetType.PLANS, "flexiblewebJPplansWidget_5", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("notesAndInformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0,  historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    private static org.json.simple.JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField jobPlanCategoryField = moduleBean.getField("jobPlanCategory", moduleName);
        FacilioField jpStatusField = moduleBean.getField("jpStatus", moduleName);
        FacilioField jobPlanVersionField = moduleBean.getField("jobPlanVersion", moduleName);
        FacilioField sfgVersionField = moduleBean.getField("sfgVersion", moduleName);
        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField contentField = moduleBean.getField("content", moduleName);
        FacilioField notesField = moduleBean.getField("notes", moduleName);
        FacilioField scheduleIdField = moduleBean.getField("scheduleId", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanCategoryField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jpStatusField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, jobPlanVersionField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sfgVersionField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, scheduleIdField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, contentField, 3, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, notesField, 4, 1, 4);


        widgetGroup.setName("generalInformation");
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
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    private static org.json.simple.JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        org.json.simple.JSONObject notesWidgetParam = new org.json.simple.JSONObject();
        notesWidgetParam.put("notesModuleName", "jobplannotes");
        org.json.simple.JSONObject attachmentWidgetParam = new org.json.simple.JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "jobplanattachments");
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
