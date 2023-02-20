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
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class TenantModule extends BaseModuleConfig{
    public TenantModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenant = new ArrayList<FacilioView>();
        tenant.add(getAllTenantsView().setOrder(order++));
        tenant.add(getActiveTenantsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT);
        groupDetails.put("views", tenant);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantsView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenants");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getActiveTenantsView() {
        FacilioField localId = new FacilioField();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getTenantStateCondition("Active"));


        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView activeTenantsView = new FacilioView();
        activeTenantsView.setName("active");
        activeTenantsView.setDisplayName("Residing Tenants");
        activeTenantsView.setSortFields(Arrays.asList(new SortField(localId, false)));
        activeTenantsView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        activeTenantsView.setAppLinkNames(appLinkNames);

        return activeTenantsView;
    }

    private static Condition getTenantStateCondition(String state) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusCondition = new Condition();
        statusCondition.setField(statusTypeField);
        statusCondition.setOperator(StringOperators.IS);
        statusCondition.setValue(state);

        Criteria statusCriteria = new Criteria() ;
        statusCriteria.addAndCondition(statusCondition);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getTenantsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition condition = new Condition();
        condition.setField(statusField);
        condition.setOperator(LookupOperator.LOOKUP);
        condition.setCriteriaValue(statusCriteria);

        return condition;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);

        FacilioForm tenantForm = new FacilioForm();
        tenantForm.setDisplayName("Tenant");
        tenantForm.setName("default_tenant_web");
        tenantForm.setModule(tenantModule);
        tenantForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        tenantForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> tenantFormFields = new ArrayList<>();
        tenantFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Tenant Logo", FormField.Required.OPTIONAL,1,1));
        tenantFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        tenantFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        tenantFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 1));
        tenantFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 6, 1));
        tenantFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 7, 1));
        tenantFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 8, 1));
        tenantFormFields.add(new FormField("tenantType", FacilioField.FieldDisplayType.SELECTBOX, "Tenant Type", FormField.Required.OPTIONAL, 9, 1));
        tenantFormFields.add(new FormField("inTime", FacilioField.FieldDisplayType.DATE, "Lease Start Date", FormField.Required.OPTIONAL, 10, 1));
        tenantFormFields.add(new FormField("outTime", FacilioField.FieldDisplayType.DATE, "Lease End Date", FormField.Required.OPTIONAL, 11, 1));
        tenantFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 12, 1));

        FormSection tenantFormSection = new FormSection("Default", 1, tenantFormFields, false);
        tenantFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantForm.setSections(Collections.singletonList(tenantFormSection));
        tenantForm.setIsSystemForm(true);
        tenantForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> tenantModuleForms = new ArrayList<>();
        tenantModuleForms.add(tenantForm);

        return tenantModuleForms;
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("primaryContactName");
        fieldNames.add("primaryContactEmail");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
