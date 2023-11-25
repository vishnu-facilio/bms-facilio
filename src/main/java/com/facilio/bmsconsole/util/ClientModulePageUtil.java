package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientModulePageUtil {
    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "clientsNotes");
        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "clientsAttachment");
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Notes", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField nameField = moduleBean.getField("primaryContactName", moduleName);
        FacilioField emailField = moduleBean.getField("primaryContactEmail", moduleName);
        FacilioField phoneField = moduleBean.getField("primaryContactPhone", moduleName);

        FacilioField streetField = moduleBean.getField("street", FacilioConstants.ContextNames.LOCATION);
        FacilioField cityField = moduleBean.getField("city", FacilioConstants.ContextNames.LOCATION);
        FacilioField stateField = moduleBean.getField("state", FacilioConstants.ContextNames.LOCATION);
        FacilioField zipField = moduleBean.getField("zip", FacilioConstants.ContextNames.LOCATION);
        FacilioField countryField = moduleBean.getField("country", FacilioConstants.ContextNames.LOCATION);
        FacilioField addressField = moduleBean.getField("address", moduleName);

        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup addressWidgetGroup=new SummaryWidgetGroup();
        SummaryWidgetGroup systemInfoWidgetGroup=new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField,1, 1, 3,null);
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 2, 1, 1,null);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 2, 2, 1,null);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField,2, 3, 1,null);

        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(3);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        addressWidgetGroup.setName("address");
        addressWidgetGroup.setDisplayName("Address");
        addressWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(addressWidgetGroup, streetField,1, 1, 1,addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, cityField, 1, 2, 1,addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, stateField, 1, 3, 1,addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, zipField,1, 4, 1,addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, countryField,2, 1, 1,addressField);

        widgetGroupList.add(addressWidgetGroup);

        systemInfoWidgetGroup.setName("systemInformation");
        systemInfoWidgetGroup.setDisplayName("System Information");
        systemInfoWidgetGroup.setColumns(4);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, createdByField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, createdTimeField,1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, modifiedByField,1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(systemInfoWidgetGroup, modifiedTimeField,1, 4, 1);

        widgetGroupList.add(systemInfoWidgetGroup);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            if(lookupField!=null){
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }
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
}
