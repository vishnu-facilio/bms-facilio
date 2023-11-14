package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ItemTemplatePage;
import com.facilio.bmsconsole.TemplatePages.StoreRoomTemplatePage;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class StoreRoomModule extends BaseModuleConfig{
    public StoreRoomModule(){
        setModuleName(FacilioConstants.ContextNames.STORE_ROOM);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> storeRoom = new ArrayList<FacilioView>();
        storeRoom.add(getAllStoreRooms().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.STORE_ROOM);
        groupDetails.put("views", storeRoom);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllStoreRooms() {

        FacilioModule storeRoomModule = ModuleFactory.getStoreRoomModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(storeRoomModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Stores");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule storeRoomModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);

        FacilioForm storeRoomForm = new FacilioForm();
        storeRoomForm.setDisplayName("NEW STORE ROOM");
        storeRoomForm.setName("web_default");
        storeRoomForm.setModule(storeRoomModule);
        storeRoomForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        storeRoomForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> storeRoomFormFields = new ArrayList<>();
        storeRoomFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        storeRoomFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        storeRoomFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Located Site", FormField.Required.REQUIRED, "site", 3, 1));
        storeRoomFormFields.add(new FormField("owner", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Owner", FormField.Required.OPTIONAL,"users", 4, 1));
        FormField field = new FormField("servingsites", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Serving Sites", FormField.Required.OPTIONAL,14, 1);
        JSONObject filterObj = new JSONObject();
        filterObj.put("skipSiteFilter", true);
        field.setConfig(filterObj);
        storeRoomFormFields.add(field);
        storeRoomFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 6, 2));
        storeRoomFormFields.add(new FormField("isGatePassRequired", FacilioField.FieldDisplayType.DECISION_BOX, "Gate Pass Needed", FormField.Required.OPTIONAL, 6, 3));
//        storeRoomForm.setFields(storeRoomFormFields);

        FormSection section = new FormSection("Default", 1, storeRoomFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        storeRoomForm.setSections(Collections.singletonList(section));
        storeRoomForm.setIsSystemForm(true);
        storeRoomForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(storeRoomForm);
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module= Constants.getModBean().getModule(FacilioConstants.ContextNames.STORE_ROOM);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            PagesContext storeRoomPage = StoreRoomTemplatePage.getStoreRoomPage(app, module, false, true);
            List<PagesContext> pagesList = new ArrayList<>();
            pagesList.add(storeRoomPage);
            appNameVsPage.put(appName,pagesList);
        }
        return appNameVsPage;
    }
    @Override
    public void addData() throws Exception {
        addSystemButton();
    }

    private static void addSystemButton() throws Exception{
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,editButton);

        SystemButtonRuleContext notificationPreferences = new SystemButtonRuleContext();
        notificationPreferences.setName("Notification Preferences");
        notificationPreferences.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        notificationPreferences.setIdentifier(FacilioConstants.ContextNames.NOTIFICATION_PREFERENCES);
        notificationPreferences.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,notificationPreferences);

        SystemButtonRuleContext bulkAddItems = new SystemButtonRuleContext();
        bulkAddItems.setName("Bulk Add Items");
        bulkAddItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        bulkAddItems.setIdentifier(FacilioConstants.ContextNames.STOREROOM_BULK_ADD_ITEMS);
        bulkAddItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,bulkAddItems);

        SystemButtonRuleContext bulkAddTools = new SystemButtonRuleContext();
        bulkAddTools.setName("Bulk Add Tools");
        bulkAddTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        bulkAddTools.setIdentifier(FacilioConstants.ContextNames.STOREROOM_BULK_ADD_TOOLS);
        bulkAddTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,bulkAddTools);

        SystemButtonRuleContext storeRoomBulkAdjustItems = new SystemButtonRuleContext();
        storeRoomBulkAdjustItems.setName("Bulk Adjust Items");
        storeRoomBulkAdjustItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        storeRoomBulkAdjustItems.setIdentifier(FacilioConstants.ContextNames.STOREROOM_BULK_ADJUST_ITEMS);
        storeRoomBulkAdjustItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,storeRoomBulkAdjustItems);

        SystemButtonRuleContext storeRoomBulkAdjustTools = new SystemButtonRuleContext();
        storeRoomBulkAdjustTools.setName("Bulk Adjust Tools");
        storeRoomBulkAdjustTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        storeRoomBulkAdjustTools.setIdentifier(FacilioConstants.ContextNames.STOREROOM_BULK_ADJUST_TOOLS);
        storeRoomBulkAdjustTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,storeRoomBulkAdjustTools);


        SystemButtonRuleContext storeRoomAddItems = new SystemButtonRuleContext();
        storeRoomAddItems.setName("Add Items");
        storeRoomAddItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        storeRoomAddItems.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ADD_ITEMS);
        storeRoomAddItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,storeRoomAddItems);


        SystemButtonRuleContext storeRoomAddTools = new SystemButtonRuleContext();
        storeRoomAddTools.setName("Add Tools");
        storeRoomAddTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        storeRoomAddTools.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ADD_TOOLS);
        storeRoomAddTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,storeRoomAddTools);


        SystemButtonRuleContext issueItems = new SystemButtonRuleContext();
        issueItems.setName("Issue Available Items");
        issueItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueItems.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ISSUE_ITEMS);
        issueItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,issueItems);

        SystemButtonRuleContext issueTools = new SystemButtonRuleContext();
        issueTools.setName("Issue Available Tools");
        issueTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueTools.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ISSUE_TOOLS);
        issueTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,issueTools);

        SystemButtonRuleContext issueReservedItems = new SystemButtonRuleContext();
        issueReservedItems.setName("Issue Reserved Items");
        issueReservedItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueReservedItems.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ISSUE_RESERVED_ITEMS);
        issueReservedItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,issueReservedItems);

        SystemButtonRuleContext issueReservedTools = new SystemButtonRuleContext();
        issueReservedTools.setName("Issue Reserved Tools");
        issueReservedTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueReservedTools.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ISSUE_RESERVED_TOOLS);
        issueReservedTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,issueReservedTools);

        SystemButtonRuleContext issueToolsToPerson = new SystemButtonRuleContext();
        issueToolsToPerson.setName("Issue Tools to Person");
        issueToolsToPerson.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueToolsToPerson.setIdentifier(FacilioConstants.ContextNames.STOREROOM_ISSUE_TOOLS_TO_PERSON);
        issueToolsToPerson.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,issueToolsToPerson);

        SystemButtonRuleContext returnToolsToPerson = new SystemButtonRuleContext();
        returnToolsToPerson.setName("Return Tools to Person");
        returnToolsToPerson.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        returnToolsToPerson.setIdentifier(FacilioConstants.ContextNames.STOREROOM_RETURN_TOOLS_TO_PERSON);
        returnToolsToPerson.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.STORE_ROOM,returnToolsToPerson);
    }
}
