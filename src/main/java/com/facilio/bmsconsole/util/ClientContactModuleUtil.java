package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientContactModuleUtil {
    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField clientField = moduleBean.getField("client", moduleName);
        FacilioField isPrimaryField = moduleBean.getField("isPrimaryContact", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, clientField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, isPrimaryField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField,1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdByField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdTimeField,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedByField,2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedTimeField,2, 4, 1);


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
    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Notes", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
