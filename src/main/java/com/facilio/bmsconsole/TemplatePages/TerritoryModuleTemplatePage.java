package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerritoryModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.Territory.TERRITORY;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Territory.TERRITORY_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("territorySummary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("territorySummaryFields", null, null)
                .addWidget("territorySummaryFieldsWidget", "Territory", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.Territory.TERRITORY))
                .widgetDone()
                .sectionDone()
                .addSection("territoryGeography", null, null)
                .addWidget("territoryGeographyWidget", "Geography", PageWidget.WidgetType.GEOGRAPHY, "webGeography_22_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("territorySites","Sites",PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("sites", null, null)
                .addWidget("sites", "Sites", PageWidget.WidgetType.TERRITORY_SITES, "flexibleSites_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("territoryFieldAgent","Field Agents",PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("fieldAgents", null, null)
                .addWidget("fieldAgents", "Field Agents", PageWidget.WidgetType.FIELD_AGENTS, "flexibleFieldAgents_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("territoryHistory", "History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                ;


    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app,String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup GeneralInfoWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(GeneralInfoWidgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(GeneralInfoWidgetGroup, descriptionField, 1, 2, 1);

        SummaryWidgetGroup sysDetailsWidgetGroup=new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedByField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedTimeField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedByField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedTimeField, 2, 4, 1);


        GeneralInfoWidgetGroup.setName("generalInformation");
        GeneralInfoWidgetGroup.setDisplayName("General Information");
        GeneralInfoWidgetGroup.setColumns(4);

        sysDetailsWidgetGroup.setName("systemDetails");
        sysDetailsWidgetGroup.setDisplayName("System Details");
        sysDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(GeneralInfoWidgetGroup);
        widgetGroupList.add(sysDetailsWidgetGroup);

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

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.Territory.TERRITORY_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.Territory.TERRITORY_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
