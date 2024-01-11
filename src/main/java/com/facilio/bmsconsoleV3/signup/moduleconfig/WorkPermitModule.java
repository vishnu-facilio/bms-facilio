package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.WorkPermitTemplatePageFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.cb.util.ChatBotMLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class WorkPermitModule extends BaseModuleConfig{

    private static final Logger LOGGER = LogManager.getLogger(WorkPermitModule.class.getName());
    public WorkPermitModule(){
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workPermitModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);

        FacilioForm workPermitForm = new FacilioForm();
        workPermitForm.setDisplayName("WORK PERMIT");
        workPermitForm.setName("default_workpermit_web");
        workPermitForm.setModule(workPermitModule);
        workPermitForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workPermitForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> workPermitFormDefaultFields = new ArrayList<>();
        workPermitFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        workPermitFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        workPermitFormDefaultFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        workPermitFormDefaultFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Location", FormField.Required.OPTIONAL, "basespace", 3, 3));
        workPermitFormDefaultFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 4, 2));
        workPermitFormDefaultFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL, "people", 4, 3));
        workPermitFormDefaultFields.add(new FormField("expectedStartTime", FacilioField.FieldDisplayType.DATETIME, "Valid From", FormField.Required.REQUIRED, 5, 2));
        workPermitFormDefaultFields.add(new FormField("expectedEndTime", FacilioField.FieldDisplayType.DATETIME, "Valid To", FormField.Required.REQUIRED, 5, 3));
        workPermitFormDefaultFields.add(new FormField("ticket", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.OPTIONAL, "ticket", 6, 1));
        workPermitFormDefaultFields.add(new FormField("workPermitType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Permit Type", FormField.Required.OPTIONAL, "workPermitType", 7, 1));

        List<FormField> workPermitFormFields = new ArrayList<>();
        workPermitFormFields.addAll(workPermitFormDefaultFields);

        FormSection defaultSection = new FormSection("PERMIT INFORMATION", 1, workPermitFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);

        workPermitForm.setSections(sections);
        workPermitForm.setIsSystemForm(true);
        workPermitForm.setType(FacilioForm.Type.FORM);


        FacilioForm portalWorkPermitForm = new FacilioForm();
        portalWorkPermitForm.setDisplayName("WORK PERMIT");
        portalWorkPermitForm.setName("default_workpermit_portal");
        portalWorkPermitForm.setModule(workPermitModule);
        portalWorkPermitForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalWorkPermitForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        List<FormField> portalWorkPermitFormFields = new ArrayList<>();
        portalWorkPermitFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Permit Name", FormField.Required.REQUIRED, 1, 1));
        portalWorkPermitFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        portalWorkPermitFormFields.add(new FormField("expectedStartTime", FacilioField.FieldDisplayType.DATETIME, "Valid From", FormField.Required.OPTIONAL, 3, 1));
        portalWorkPermitFormFields.add(new FormField("expectedEndTime", FacilioField.FieldDisplayType.DATETIME, "Valid To", FormField.Required.OPTIONAL, 4, 1));
        portalWorkPermitFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED,"vendors", 6, 1));
        portalWorkPermitFormFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL, "people",7, 1));


        FormSection portalDefaultSection = new FormSection("PERMIT INFORMATION", 1, portalWorkPermitFormFields, true);
        portalDefaultSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> section = new ArrayList<>();
        section.add(portalDefaultSection);

        portalWorkPermitForm.setSections(section);
        portalWorkPermitForm.setIsSystemForm(true);
        portalWorkPermitForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> workPermitModuleForms = new ArrayList<>();
        workPermitModuleForms.add(workPermitForm);
        workPermitModuleForms.add(portalWorkPermitForm);

        return workPermitModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workPermit = new ArrayList<FacilioView>();
        workPermit.add(getAllWorkPermitView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        groupDetails.put("views", workPermit);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    @Override
    public void addData() throws Exception {
        String moduleName = FacilioConstants.ContextNames.WorkPermit.WORKPERMIT;
        List<FacilioModule> workPermitModuleList = new ArrayList<>();
        FacilioModule workPermitModule = addWorkPermitModule(moduleName);
        workPermitModuleList.add(workPermitModule);
        FacilioModule workPermitCheckListModule = addWorkPermitCheckListModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_CHECKLIST);
        workPermitModuleList.add(workPermitCheckListModule);

        LOGGER.info("Adding WorkPermit fields");
        FacilioChain addModuleChain = TransactionChainFactory.getAddFieldsChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, workPermitModuleList);
        addModuleChain.execute();
        LOGGER.info("Added WorkPermit fields");

        addSystemButtons(moduleName);
    }

    private FacilioModule addWorkPermitModule(String moduleName) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        module.setStateFlowEnabled(true);
        List<FacilioField> workPermitFields = getWorkPermitFields(module);
        module.setFields(workPermitFields);
        return module;
    }

    private FacilioModule addWorkPermitCheckListModule(String moduleName) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        List<FacilioField> workPermitCheckListFields = getWorkPermitCheckListFields(module);
        module.setFields(workPermitCheckListFields);
        return module;
    }
    private List<FacilioField> getWorkPermitCheckListFields(FacilioModule workPermitCheckListModule) throws Exception {
        List<FacilioField> workPermitCheckListFields = new ArrayList<>();

        LookupField workPermit = FieldFactory.getDefaultField("workPermit", "Work Permit", "WORK_PERMIT_ID", FieldType.LOOKUP,FacilioField.FieldDisplayType.LOOKUP_SIMPLE,true);
        workPermit.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
        workPermit.setModule(workPermitCheckListModule);
        workPermit.setRequired(true);
        workPermit.setDefault(true);
        workPermitCheckListFields.add(workPermit);

        LookupField checklist = FieldFactory.getDefaultField("checklist", "Checklist", "CHECKLIST_ID", FieldType.LOOKUP,FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        checklist.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST));
        checklist.setModule(workPermitCheckListModule);
        checklist.setRequired(true);
        checklist.setDefault(true);
        workPermitCheckListFields.add(checklist);

        BooleanField isReviewed = FieldFactory.getDefaultField("isReviewed","Is Reviewed","IS_REVIEWED",FieldType.BOOLEAN, FacilioField.FieldDisplayType.DECISION_BOX,false);
        isReviewed.setDefault(true);
        workPermitCheckListFields.add(isReviewed);

        SystemEnumField required = FieldFactory.getDefaultField("required","Work Type","REQUIRED",FieldType.SYSTEM_ENUM, FacilioField.FieldDisplayType.TEXTBOX,false);
        required.setEnumName("Required");
        required.setRequired(true);
        required.setDefault(true);
        workPermitCheckListFields.add(required);

        FacilioField remarksField = FieldFactory.getDefaultField("Remarks","Remarks","REMARKS",FieldType.STRING, FacilioField.FieldDisplayType.TEXTBOX,false);
        remarksField.setDefault(true);
        workPermitCheckListFields.add(remarksField);

        FacilioField reviewRemarksField = FieldFactory.getDefaultField("reviewerRemarks","Reviewer Remarks","REVIEWER_REMARKS",FieldType.STRING, FacilioField.FieldDisplayType.TEXTBOX,false);
        reviewRemarksField.setDefault(true);
        workPermitCheckListFields.add(reviewRemarksField);

        return workPermitCheckListFields;
    }


        private List<FacilioField> getWorkPermitFields(FacilioModule workPermitModule) throws Exception {
        List<FacilioField> workPermitFields = new ArrayList<>();
        NumberField localIdField = FieldFactory.getDefaultField("localId","ID","LOCAL_ID",FieldType.NUMBER, FacilioField.FieldDisplayType.NUMBER,false);
        localIdField.setDefault(true);
        workPermitFields.add(localIdField);

        StringField nameField = FieldFactory.getDefaultField("name","Subject","NAME",FieldType.STRING, FacilioField.FieldDisplayType.TEXTBOX,true);
        nameField.setRequired(true);
        workPermitFields.add(nameField);

        StringField descField = FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA,false);
        descField.setDefault(true);
        workPermitFields.add(descField);

        DateField expectedStartTime = FieldFactory.getDefaultField("expectedStartTime","Valid From","EXPECTED_START_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME,false);
        expectedStartTime.setDefault(true);
        workPermitFields.add(expectedStartTime);

        DateField expectedEndTime = FieldFactory.getDefaultField("expectedEndTime","Valid Till","EXPECTED_END_TIME",FieldType.DATE_TIME, FacilioField.FieldDisplayType.DATETIME,false);
        expectedEndTime.setDefault(true);
        expectedEndTime.setRequired(true);
        workPermitFields.add(expectedEndTime);

        SystemEnumField workTypeField = FieldFactory.getDefaultField("workType","Work Type","WORK_TYPE",FieldType.SYSTEM_ENUM, FacilioField.FieldDisplayType.TEXTBOX,false);
        workTypeField.setEnumName("WorkType");
        workTypeField.setRequired(true);
        workTypeField.setDefault(true);
        workPermitFields.add(workTypeField);

        LookupField vendor = FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR_ID", FieldType.LOOKUP,FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        vendor.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDORS));
        vendor.setModule(workPermitModule);
        vendor.setRequired(true);
        vendor.setDefault(true);
        workPermitFields.add(vendor);

        LookupField tickets = FieldFactory.getDefaultField("ticket", "Work Order", "TICKET_ID", FieldType.LOOKUP,FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        tickets.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET));
        tickets.setModule(workPermitModule);
        tickets.setRequired(true);
        tickets.setDefault(true);
        workPermitFields.add(tickets);

        LookupField moduleState = FieldFactory.getDefaultField("moduleState", "Module State", "MODULE_STATE", FieldType.LOOKUP,FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleState.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        moduleState.setModule(workPermitModule);
        moduleState.setDefault(true);
        moduleState.setRequired(true);
        workPermitFields.add(moduleState);

        LookupField requestedBy = FieldFactory.getDefaultField("requestedBy", "Requested By", "REQUESTED_BY", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        requestedBy.setModule(workPermitModule);
        requestedBy.setSpecialType("users");
        requestedBy.setDefault(true);
        workPermitFields.add(requestedBy);

        LookupField vendorContact = FieldFactory.getDefaultField("vendorContact", "Vendor Contact", "VENDOR_CONTACT_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        vendorContact.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.CONTACT));
        vendorContact.setModule(workPermitModule);
        vendorContact.setDefault(true);
        workPermitFields.add(vendorContact);

        LookupField workPermitType = FieldFactory.getDefaultField("workPermitType", "Work Permit Type", "WORK_PERMIT_TYPE_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        workPermitType.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE));
        workPermitType.setModule(workPermitModule);
        workPermitType.setDefault(true);
        workPermitType.setRequired(false);
        workPermitFields.add(workPermitType);

        LookupField approvalStatus = FieldFactory.getDefaultField("approvalStatus", "Approval State", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStatus.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        approvalStatus.setModule(workPermitModule);
        approvalStatus.setDefault(true);
        approvalStatus.setRequired(true);
        workPermitFields.add(approvalStatus);

        NumberField recurringInfoField = FieldFactory.getDefaultField("recurringInfoId","Recurring Info ID","RECURRING_INFO_ID",FieldType.NUMBER, FacilioField.FieldDisplayType.NUMBER,false);
        recurringInfoField.setDefault(true);
        workPermitFields.add(recurringInfoField);

        NumberField stateFlowField = FieldFactory.getDefaultField("stateFlowId","Recurring Info ID","STATE_FLOW_ID",FieldType.NUMBER, FacilioField.FieldDisplayType.NUMBER,false);
        stateFlowField.setRequired(true);
        workPermitFields.add(stateFlowField);

        NumberField approvalFlowId = FieldFactory.getDefaultField("approvalFlowId","Recurring Info ID","APPROVAL_FLOW_ID",FieldType.NUMBER, FacilioField.FieldDisplayType.NUMBER,false);
        approvalFlowId.setRequired(true);
        approvalFlowId.setDefault(true);
        workPermitFields.add(approvalFlowId);

        BooleanField recurringField = FieldFactory.getDefaultField("isRecurring","Is Recurring","IS_RECURRING",FieldType.BOOLEAN, FacilioField.FieldDisplayType.DECISION_BOX,false);
        recurringField.setRequired(true);
        recurringField.setDefault(true);
        workPermitFields.add(recurringField);

        BooleanField isPreValidationDone = FieldFactory.getDefaultField("isPreValidationDone","Prerequisites Reviewed","IS_PRE_VALIDATION_DONE",FieldType.BOOLEAN, FacilioField.FieldDisplayType.DECISION_BOX,false);
        isPreValidationDone.setRequired(true);
        isPreValidationDone.setDefault(true);
        workPermitFields.add(isPreValidationDone);

        BooleanField isPostValidationDone = FieldFactory.getDefaultField("isPostValidationDone","Permit Closed","IS_POST_VALIDATION_DONE",FieldType.BOOLEAN, FacilioField.FieldDisplayType.DECISION_BOX,false);
        isPostValidationDone.setRequired(true);
        isPostValidationDone.setDefault(true);
        workPermitFields.add(isPostValidationDone);

        SystemEnumField permitEnumField = FieldFactory.getDefaultField("permitStatus","Permit Status","PERMIT_STATUS",FieldType.SYSTEM_ENUM, FacilioField.FieldDisplayType.SELECTBOX,false);
        permitEnumField.setModule(workPermitModule);
        permitEnumField.setDefault(true);
        permitEnumField.setEnumName("PermitStatus");
        workPermitFields.add(permitEnumField);

        SystemEnumField permitType = FieldFactory.getDefaultField("permitType","Permit Type","PERMIT_TYPE",FieldType.SYSTEM_ENUM, FacilioField.FieldDisplayType.SELECTBOX,false);
        permitType.setModule(workPermitModule);
        permitType.setDefault(true);
        permitType.setEnumName("PermitType");
        workPermitFields.add(permitType);

        LookupField issuedToUser = FieldFactory.getDefaultField("issuedToUser", "Permit Holder", "ISSUED_TO_USER", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SECTION);
        issuedToUser.setModule(workPermitModule);
        issuedToUser.setDefault(true);
        issuedToUser.setSpecialType("users");
        workPermitFields.add(issuedToUser);

        LookupField people = FieldFactory.getDefaultField("people", "Contact", "PEOPLE_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SECTION);
        people.setModule(workPermitModule);
        people.setDefault(true);
        people.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        workPermitFields.add(people);

        LookupField space = FieldFactory.getDefaultField("space", "Location", "BASE_SPACE_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        space.setModule(workPermitModule);
        space.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE));
        workPermitFields.add(space);

        LookupField tenant = FieldFactory.getDefaultField("tenant", "Tenant", "'TENANT_ID'", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        tenant.setModule(workPermitModule);
        tenant.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.TENANT));
        tenant.setDefault(true);
        workPermitFields.add(tenant);

        return workPermitFields;
    }

    public void addSystemButtons(String moduleName) throws Exception {
        addListButtons(moduleName);
        addSummaryButtons(moduleName);
    }

    public void addSummaryButtons(String moduleName) throws Exception {
        // preRequisites button
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
        SystemButtonRuleContext preRequisitesBtn = new SystemButtonRuleContext();
        preRequisitesBtn.setName("preRequisites");
        preRequisitesBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        preRequisitesBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        preRequisitesBtn.setIdentifier("preRequisites_summary");
        preRequisitesBtn.setPermissionRequired(true);
        preRequisitesBtn.setPermission("UPDATE");
        preRequisitesBtn.setCriteria(getPrerequisitesNotFilledCriteria(fieldMap));
        SystemButtonApi.addSystemButton(moduleName, preRequisitesBtn);

        //post checklist button
        SystemButtonRuleContext postChecklistBtn = new SystemButtonRuleContext();
        postChecklistBtn.setName("postCheckList");
        postChecklistBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        postChecklistBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        postChecklistBtn.setIdentifier("postCheckList_summary");
        postChecklistBtn.setPermissionRequired(true);
        postChecklistBtn.setCriteria(getPostCheckListNotFilledCriteria(fieldMap));
        postChecklistBtn.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName, postChecklistBtn);

        // preRequisites review button
        SystemButtonRuleContext preRequisitesReviewBtn = new SystemButtonRuleContext();
        preRequisitesReviewBtn.setName("preReview");
        preRequisitesReviewBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        preRequisitesReviewBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        preRequisitesReviewBtn.setIdentifier("preReview_summary");
        preRequisitesReviewBtn.setPermissionRequired(true);
        preRequisitesReviewBtn.setPermission("UPDATE_REVIEW");
        preRequisitesReviewBtn.setCriteria(getPrevalidationNotDoneCriteria(fieldMap));
        SystemButtonApi.addSystemButton(moduleName, preRequisitesReviewBtn);

        //post checklist button review
        SystemButtonRuleContext postChecklistReviewBtn = new SystemButtonRuleContext();
        postChecklistReviewBtn.setName("postReview");
        postChecklistReviewBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        postChecklistReviewBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        postChecklistReviewBtn.setIdentifier("postReview_summary");
        postChecklistReviewBtn.setPermissionRequired(true);
        postChecklistReviewBtn.setPermission("UPDATE_REVIEW");
        postChecklistReviewBtn.setCriteria(getPostvalidationNotDoneCriteria(fieldMap));
        SystemButtonApi.addSystemButton(moduleName, postChecklistReviewBtn);

        SystemButtonApi.addSummaryEditButton(moduleName);
    }

    private Criteria getPrerequisitesNotFilledCriteria(Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"),CommonOperators.IS_EMPTY));
        criteria.setPattern("(1)");
        return criteria;
    }

    private Criteria getPrevalidationNotDoneCriteria(Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), WorkPermitContext.PermitStatus.PRE_REQUISITES_FILLED.getIndex().toString(), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), String.valueOf(WorkPermitContext.PermitStatus.PRE_VALIDATION_DONE.getIndex()), NumberOperators.NOT_EQUALS));
        criteria.setPattern("(1 AND 2)");
        return criteria;
    }
    private Criteria getPostCheckListNotFilledCriteria(Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), String.valueOf(WorkPermitContext.PermitStatus.PRE_VALIDATION_DONE.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), WorkPermitContext.PermitStatus.POST_REQUISITES_FILLED.getIndex().toString(), NumberOperators.NOT_EQUALS));
        criteria.setPattern("(1 AND 2)");
        return criteria;
    }
    private Criteria getPostvalidationNotDoneCriteria(Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), WorkPermitContext.PermitStatus.POST_REQUISITES_FILLED.getIndex().toString(),  NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("permitStatus"), WorkPermitContext.PermitStatus.POST_VALIDATION_DONE.getIndex().toString(),  NumberOperators.NOT_EQUALS));
        criteria.setPattern("(1 AND 2)");
        return criteria;
    }

    public void addListButtons(String moduleName) throws Exception{
        SystemButtonApi.addCreateButton(moduleName);
        SystemButtonApi.addListEditButton(moduleName);
        SystemButtonApi.addListDeleteButton(moduleName);
        SystemButtonApi.addExportAsExcel(moduleName);
        SystemButtonApi.addExportAsCSV(moduleName);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception{
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNameList = new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createWorkPermitDefaultPage(app, module, true, false));
        }
        return appNameVsPage;
    }

    private List<PagesContext> createWorkPermitDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY);

        return new ModulePages()
                .addPage("workPermitDefaultPage","Default Work Permit page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("workPermitPdfViewer", null, null)
                .addWidget("workPermitPdfViewerWidget", "Summary", PageWidget.WidgetType.PDF_VIEWER, "flexiblewebpdfviewer_19", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("notesAndInformation","Notes & Information", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Permit details",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_6", 0, 0, null, WorkPermitTemplatePageFactory.getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, WorkPermitTemplatePageFactory.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("workPermitRelatedlist", "Related List", "List of related records across modules")
                .addWidget("permitrelated", "Related",PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("workpermitactivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    private static FacilioView getAllWorkPermitView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Permit");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
