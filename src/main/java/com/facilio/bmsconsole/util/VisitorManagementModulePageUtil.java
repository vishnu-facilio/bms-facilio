package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VisitorManagementModulePageUtil {
    public static JSONObject getSummaryWidgetDetailsForVisitorModule(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField lastVisitedSpaceField = moduleBean.getField("lastVisitedSpace", moduleName);
        FacilioField lastVisitedPeopleField = moduleBean.getField("lastVisitedPeople", moduleName);
        FacilioField lastVisitedTimeField = moduleBean.getField("lastVisitedTime", moduleName);
        FacilioField lastVisitDurationField = moduleBean.getField("lastVisitDuration", moduleName);
        FacilioField visitorTypeField = moduleBean.getField("visitorType", moduleName);

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
        SummaryWidgetGroup systemInfoWidgetGroup=new SummaryWidgetGroup();
        SummaryWidgetGroup addressWidgetGroup=new SummaryWidgetGroup();


        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Visitor Name", nameField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "Visitor Phone",phoneField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "Last Visited Place",lastVisitedSpaceField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, lastVisitedPeopleField,1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, lastVisitedTimeField,2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Last Visited Duration", lastVisitDurationField,2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Visitor Type", visitorTypeField,2, 3, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        addressWidgetGroup.setName("address");
        addressWidgetGroup.setDisplayName("Address");
        addressWidgetGroup.setColumns(4);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(addressWidgetGroup, null,streetField,1, 1, 1,addressField);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(addressWidgetGroup,null, cityField, 1, 2, 1,addressField);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(addressWidgetGroup, null,stateField, 1, 3, 1,addressField);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(addressWidgetGroup, null,zipField,1, 4, 1,addressField);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(addressWidgetGroup,null, countryField,2, 1, 1,addressField);

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
    public static JSONObject getWidgetGroup(boolean isMobile,String moduleName) throws Exception {//
        JSONObject notesWidgetParam = new JSONObject();
        JSONObject attachmentsWidgetParam = new JSONObject();

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName),"Module Name Not found");

        if(moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG)){
            notesWidgetParam.put("notesModuleName", "newvisitorlognotes");
            attachmentsWidgetParam.put("attachmentsModuleName", "newvisitorlogattachments");
        }
        else if(moduleName.equals(FacilioConstants.ContextNames.VISITOR)){
            notesWidgetParam.put("notesModuleName", "visitornotes");
            attachmentsWidgetParam.put("attachmentsModuleName", "visitorattachments");
        }
        else if(moduleName.equals(FacilioConstants.ContextNames.INVITE_VISITOR)){
            notesWidgetParam.put("notesModuleName", "invitevisitornotes");
            attachmentsWidgetParam.put("attachmentsModuleName", "invitevisitorattachments");

        }
        else {
            throw new IllegalArgumentException("Not Notes and Attachment params found");
        }
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

    public static JSONObject getSummaryWidgetDetailsForVisitorLogModule(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("visitorName", moduleName);
        FacilioField phoneField = moduleBean.getField("visitorPhone", moduleName);
        FacilioField checkInTimeField = moduleBean.getField("checkInTime", moduleName);
        FacilioField checkOutTimeField = moduleBean.getField("checkOutTime", moduleName);
        FacilioField hostField = moduleBean.getField("host", moduleName);
        FacilioField visitedSpaceField = moduleBean.getField("visitedSpace", moduleName);
        FacilioField visitorTypeField = moduleBean.getField("visitorType", moduleName);
        FacilioField purposeOfVisitField = moduleBean.getField("purposeOfVisit", moduleName);


        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemInfoWidgetGroup=new SummaryWidgetGroup();


        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, nameField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, checkInTimeField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, checkOutTimeField,1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, hostField,2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Visted Space", visitedSpaceField,2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "Visitor Type",visitorTypeField,2, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, purposeOfVisitField,2, 4, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
    public static JSONObject getSummaryWidgetDetailsForIniviteVisitorModule (String moduleName, ApplicationContext app) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("visitorName", moduleName);
        FacilioField phoneField = moduleBean.getField("visitorPhone", moduleName);
        FacilioField checkInTimeField = moduleBean.getField("checkInTime", moduleName);
        FacilioField checkOutTimeField = moduleBean.getField("checkOutTime", moduleName);
        FacilioField hostField = moduleBean.getField("host", moduleName);
        FacilioField visitedSpaceField = moduleBean.getField("visitedSpace", moduleName);
        FacilioField visitorTypeField = moduleBean.getField("visitorType", moduleName);
        FacilioField purposeOfVisitField = moduleBean.getField("purposeOfVisit", moduleName);
        FacilioField isInvitationSentField = moduleBean.getField("isInvitationSent", moduleName);
        FacilioField isVipField = moduleBean.getField("isVip", moduleName);

        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemInfoWidgetGroup=new SummaryWidgetGroup();


        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, nameField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "Check-In Time",checkInTimeField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "CheckOut Time",checkOutTimeField,1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, hostField,2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, visitedSpaceField,2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, visitorTypeField,2, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, purposeOfVisitField,2, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "Send Invite Mail",isInvitationSentField,3, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, "VIP",isVipField,3, 2, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
    public static JSONObject getSummaryWidgetDetailsForGroupInvitesModule (String moduleName, ApplicationContext app) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);

        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemInfoWidgetGroup=new SummaryWidgetGroup();


        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, descriptionField,1, 1, 1);

        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
}
