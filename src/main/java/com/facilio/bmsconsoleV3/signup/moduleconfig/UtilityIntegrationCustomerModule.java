package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.*;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class UtilityIntegrationCustomerModule extends BaseModuleConfig {

    public UtilityIntegrationCustomerModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationCustomerModule = addUtilityIntegrationCustomerModule();
        modules.add(utilityIntegrationCustomerModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        pollMeterJob();
    }
    public void pollMeterJob() throws Exception {

        List<String> timeStr = Arrays.asList("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00",
                "20:00","21:00","22:00","23:00");
        ScheduleInfo si = new ScheduleInfo();
        si.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        si.setTimes(timeStr);
        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "PollMeters", DateTimeUtil.getCurrenTime(), si, "facilio");

        ScheduleInfo scheduleInfo2 = new ScheduleInfo();
        scheduleInfo2.addTime("01:00");
        scheduleInfo2.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_DAY);
        List<Integer> values = new ArrayList<>();
        values.add(1);
        scheduleInfo2.setValues(values);
        FacilioTimer.scheduleCalendarJob(AccountUtil.getCurrentOrg().getId(), "UtilityBillMissingDisputeJob", DateTimeUtil.getCurrenTime(), scheduleInfo2, "facilio");
    }

    public FacilioModule addUtilityIntegrationCustomerModule() throws Exception{

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationCustomer", "Utility Integration Customer", "Utility_Integration_Customer",FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        DateField createdTime =  FieldFactory.getDefaultField("authorizationSubmittedTime", "Authorization Submitted Time", "CREATED_TIME", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(createdTime);

        StringField customerEmail = (StringField) FieldFactory.getDefaultField("customerEmail", "Customer Email", "CUSTOMER_EMAIL", FieldType.STRING);
        fields.add(customerEmail);

        StringField customerName = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true);
        fields.add(customerName);

        StringField customerId = (StringField) FieldFactory.getDefaultField("customerId", "Customer Id", "CUSTOMER_UID", FieldType.STRING);
        fields.add(customerId);

        StringField formUid = (StringField) FieldFactory.getDefaultField("formUid","Form UID","FORM_UID",FieldType.STRING);
        fields.add(formUid);

        StringField templateUid = (StringField) FieldFactory.getDefaultField("templateUid","Template UID","TEMPLATE_UID",FieldType.STRING);
        fields.add(templateUid);

        BooleanField isArchived =  FieldFactory.getDefaultField("isArchived", "Is Archived", "IS_ARCHIVED", FieldType.BOOLEAN);
        fields.add(isArchived);

        BooleanField isRevoked =  FieldFactory.getDefaultField("isRevoked", "Is Revoked", "IS_REVOKED", FieldType.BOOLEAN);
        fields.add(isRevoked);


        StringField status = (StringField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.STRING);
        fields.add(status);

        StringField userEmail = (StringField) FieldFactory.getDefaultField("userEmail", "User Email", "USER_EMAIL", FieldType.STRING);
        fields.add(userEmail);

        StringField userUid = (StringField) FieldFactory.getDefaultField("userUid", "User UID", "USER_UID", FieldType.STRING);
        fields.add(userUid);

        StringField userStatus = (StringField) FieldFactory.getDefaultField("userStatus", "User Status", "USER_STATUS", FieldType.STRING);
        fields.add(userStatus);

        DateField expires =  FieldFactory.getDefaultField("expires", "Expiry Date", "EXPIRES", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(expires);

        BooleanField isExpired =  FieldFactory.getDefaultField("isExpired", "Is Epired", "IS_EXPIRED", FieldType.BOOLEAN);
        fields.add(isExpired);


        StringField utilityID = (StringField) FieldFactory.getDefaultField("utilityID", "Utility ID", "UTILITY_ID", FieldType.STRING);
        fields.add(utilityID);

        StringField meta = (StringField) FieldFactory.getDefaultField("meta", "Meta", "META_JSON", FieldType.STRING);
        fields.add(meta);

        NumberField noOfConnections = (NumberField)  FieldFactory.getDefaultField("noOfConnections","No.of Connections","NO_OF_CONNECTIONS", FieldType.NUMBER);
        fields.add(noOfConnections);


        SystemEnumField customerType = (SystemEnumField) FieldFactory.getDefaultField("customerType", "Customer Type", "CUSTOMER_TYPE", FieldType.SYSTEM_ENUM);
        customerType.setEnumName("Type");
        fields.add(customerType);

        StringField secretState = (StringField) FieldFactory.getDefaultField("secretState", "Secret State", "SECRET_STATE", FieldType.STRING);
        fields.add(secretState);

        DateField revoked =  FieldFactory.getDefaultField("revoked", "Revoked", "REVOKED", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(revoked);


        module.setFields(fields);
        return module;
    }
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> utilityCustomerModule = new ArrayList<FacilioView>();
        utilityCustomerModule.add(getUtilityCustomerViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        groupDetails.put("views", utilityCustomerModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUtilityCustomerViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityIntegrationCustomer.ID", FieldType.NUMBER), true));

        FacilioView utilityCustomerView = new FacilioView();
        utilityCustomerView.setName("all");
        utilityCustomerView.setDisplayName("Utility Integration Customer");

        utilityCustomerView.setModuleName(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        utilityCustomerView.setSortFields(sortFields);

        List<ViewField> utilityCustomerViewFields = new ArrayList<>();

        utilityCustomerViewFields.add(new ViewField("name","Name"));
        utilityCustomerViewFields.add(new ViewField("customerId","Customer Id"));
        utilityCustomerViewFields.add(new ViewField("customerEmail","Customer Email"));
        utilityCustomerViewFields.add(new ViewField("noOfConnections","No.of Connections"));
        utilityCustomerViewFields.add(new ViewField("utilityID","Utility ID"));
        utilityCustomerViewFields.add(new ViewField("expires","Expiry Date"));
        utilityCustomerViewFields.add(new ViewField("customerType","Customer Type"));

        utilityCustomerView.setFields(utilityCustomerViewFields);

        return utilityCustomerView;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, getSystemPage());
        return  pageTemp;
    }
    private static List<PagesContext> getSystemPage() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);

        JSONObject moduleData = new JSONObject();
        moduleData.put("summaryWidgetName", "meterWidget");

        JSONObject meterHistoryWidgetParam = new JSONObject();
        meterHistoryWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_METER_ACTIVITY);

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY);
        List<PagesContext> disputePages = new ArrayList<>();

        PagesContext billMissingTemplatePage = new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilityintegrationcustomersummary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilityintegrationcustomersummaryfields", null, null)
                .addWidget("utilityintegrationcustomersummaryfieldswidget", "Account  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER))
                .widgetDone()
                .sectionDone()
                .addSection("meterConnections", null, null)
                .addWidget("meterWidget", "Meter Connections", PageWidget.WidgetType.METER_WIDGET, "webMeterWidget-10*12", 0, 0, moduleData, getSummaryWidgetDetails(FacilioConstants.UTILITY_INTEGRATION_METER))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilityintegrationcustomerrelated", "BILLS", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilityintegrationcustomerrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("utilityintegrationcustomerlist", "", "")
                .addWidget("utilitycustomerrelatedList", "", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, fetchAllRelatedListForModule(customerModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilityintegrationcustomerhistory", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Customer History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
//                .addSection("activity", null, null)
//                .addWidget("activity", "Meter History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, meterHistoryWidgetParam, null)
//                .widgetDone()
//                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        disputePages.add(billMissingTemplatePage);


        return disputePages;
    }
    public static JSONObject fetchAllRelatedListForModule(FacilioModule module) throws Exception {
        BulkRelatedListContext bulkRelatedListWidget = new BulkRelatedListContext();
        List<RelatedListWidgetContext> relLists = new ArrayList<>();
        if(module != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule.ModuleType> subModuleTypesToFetch = RelatedListWidgetUtil.getSubModuleTypesToFetchForRelatedList(module);

            List<String> modulesToAdd = new ArrayList<>();
            List<String> modulesToRemove = new ArrayList<>();
            RelatedListWidgetUtil.addOrRemoveModulesFromRelatedLists(module, modulesToAdd, modulesToRemove);

            List<FacilioModule> subModules = modBean.getSubModules(module.getName(), subModuleTypesToFetch.toArray(new FacilioModule.ModuleType[]{}));
            if(CollectionUtils.isNotEmpty(modulesToAdd)) {
                List<String> existingSubModuleNames = subModules
                        .stream()
                        .map(FacilioModule::getName)
                        .collect(Collectors.toList());
                List<String> modulesToAddInSubModules =  new ArrayList<>(modulesToAdd);
                modulesToAddInSubModules.removeAll(existingSubModuleNames);
                //modulesToAddInSubModules.addAll(modulesToAdd);
                subModules.addAll(modBean.getModuleList(modulesToAddInSubModules));
            }

            if(CollectionUtils.isNotEmpty(modulesToRemove)) {
                subModules.removeIf(mod->modulesToRemove.contains(mod.getName()));
            }
            for(FacilioModule subModule : subModules ) {
                if (subModule.isModuleHidden()) {
                    continue;
                }

                List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                List<FacilioField> fields = allFields.stream()
                        .filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId()))
                        .collect(Collectors.toList());

                long moduleId = module.getModuleId();
                if (CollectionUtils.isNotEmpty(fields)) {

                    for(FacilioField field : fields ) {
                        //if (PageFactory.relatedListHasPermission(moduleId, subModule, field)) {
                        RelatedListWidgetContext relList = new RelatedListWidgetContext();
                        if (StringUtils.isNotEmpty(((LookupField) field).getRelatedListDisplayName())) {
                            relList.setDisplayName(((LookupField) field).getRelatedListDisplayName());
                        } else {
                            relList.setDisplayName(field.getDisplayName());
                        }
                        relList.setSubModuleName(subModule.getName());
                        relList.setSubModuleId(subModule.getModuleId());
                        relList.setFieldName(field.getName());
                        relList.setFieldId(field.getFieldId());
                        relLists.add(relList);
                        // }
                    }
                }
            }
        }
        bulkRelatedListWidget.setRelatedList(relLists);
        return FieldUtil.getAsJSON(bulkRelatedListWidget);
    };

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("id", moduleName);
        FacilioField phoneField = moduleBean.getField("name", moduleName);
        FacilioField emailField = moduleBean.getField("noOfConnections", moduleName);
        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
        FacilioField supplier = moduleBean.getField("utilityID",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplier, 1, 4, 1);

        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup1);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
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
        commentWidgetParam.put("notesModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

}
