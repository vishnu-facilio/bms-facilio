package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class ServiceRequestModule extends BaseModuleConfig{
    public ServiceRequestModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> serviceRequest = new ArrayList<FacilioView>();
        serviceRequest.add(getAllServiceRequests().setOrder(order++));
        serviceRequest.add(getMyServiceRequets().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SERVICE_REQUEST);
        groupDetails.put("views", serviceRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllServiceRequests() {

        FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", serviceRequestsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service Requests");
        allView.setSortFields(sortFields);
        allView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getMyServiceRequets() {

        Criteria criteria = new Criteria();
        FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();
//		criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(getMyServiceRequestCondition());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("dueDate");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("DUE_DATE");
        createdTime.setModule(serviceRequestsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("myopenservicerequests");
        openTicketsView.setDisplayName("My Service Requests");
        openTicketsView.setCriteria(criteria);
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }

    public static Condition getMyServiceRequestCondition() {
        LookupField userField = new LookupField();
        userField.setName("assignedTo");
        userField.setColumnName("ASSIGNED_TO_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getServiceRequestModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequestModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);

        FacilioForm serviceRequestForm = new FacilioForm();
        serviceRequestForm.setDisplayName("SERVICE REQUEST");
        serviceRequestForm.setName("default_serviceRequest_web");
        serviceRequestForm.setModule(serviceRequestModule);
        serviceRequestForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceRequestForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> serviceRequestFormFields = new ArrayList<>();
        serviceRequestFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        serviceRequestFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        serviceRequestFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 1));
        serviceRequestFormFields.add(new FormField("requester", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requester", FormField.Required.REQUIRED, "people" , 4, 1));
        serviceRequestFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 5, 1));
        serviceRequestFormFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 6, 1));
        serviceRequestFormFields.add(new FormField("urgency", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Urgency", FormField.Required.OPTIONAL, "servicerequestpriority", 7, 1));
        serviceRequestFormFields.add(new FormField("classification", FacilioField.FieldDisplayType.SELECTBOX, "Classification", FormField.Required.OPTIONAL, "classification" , 8, 1));
        serviceRequestFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 8, 1));
        serviceRequestForm.setFields(serviceRequestFormFields);

        FormSection section = new FormSection("Default", 1, serviceRequestFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        serviceRequestForm.setSections(Collections.singletonList(section));
        serviceRequestForm.setIsSystemForm(true);
        serviceRequestForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(serviceRequestForm);
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("site");
        fieldNames.add("resource");
        fieldNames.add("dueDate");
        fieldNames.add("assignedTo");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
