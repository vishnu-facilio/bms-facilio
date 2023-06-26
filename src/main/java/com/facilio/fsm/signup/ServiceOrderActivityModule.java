package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceOrderActivityModule extends BaseModuleConfig  {
    public ServiceOrderActivityModule(){
        setModuleName(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY);
    }

    public void addData() throws Exception {
        FacilioModule serviceOrderModule = constructServiceOrderActivityModule();
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(serviceOrderModule));
//        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        FacilioModule parentModule = bean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        FacilioModule childModule = bean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY);
        bean.addSubModule(parentModule.getModuleId(),childModule.getModuleId());
    }

    private FacilioModule constructServiceOrderActivityModule() throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY, "Service Order Activity", "ServiceOrderActivity", FacilioModule.ModuleType.ACTIVITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField createdby = FieldFactory.getDefaultField("createdby","CreatedBy","SYS_CREATED_BY", FieldType.LOOKUP);
        createdby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(createdby);

        FacilioField createdTime = FieldFactory.getDefaultField("createdtime","CreatedTime","SYS_CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);

        LookupField modifiedby = FieldFactory.getDefaultField("modifiedby","ModifiedBy","SYS_MODIFIED_BY",FieldType.LOOKUP);
        modifiedby.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(modifiedby);

        FacilioField modifiedTime = FieldFactory.getDefaultField("modifiedtime","ModifiedTime","SYS_MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(modifiedTime);

        FacilioField parentId = FieldFactory.getDefaultField("parentid","ParentId","PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField ttime = FieldFactory.getDefaultField("ttime","Ttime","TTIME", FieldType.DATE_TIME);
        fields.add(ttime);

        FacilioField activitytype = FieldFactory.getDefaultField("activitytype","ActivityType","ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(activitytype);

        LookupField donebyid = FieldFactory.getDefaultField("donebyid","DoneBy","DONE_BY_ID",FieldType.LOOKUP);
        donebyid.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(donebyid);

        FacilioField info = FieldFactory.getDefaultField("info","Info","INFO", FieldType.STRING,FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(info);

        return module;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER_ACTIVITY);

        FacilioForm webWorkOrderForm = new FacilioForm();
        webWorkOrderForm.setDisplayName("Standard");
        webWorkOrderForm.setName("default_serviceorderactivity_web");
        webWorkOrderForm.setModule(serviceOrderModule);
        webWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> webWorkOrderFormDefaultFields = new ArrayList<>();
        webWorkOrderFormDefaultFields.add(new FormField("activitytype", FacilioField.FieldDisplayType.NUMBER, "Activity Type", FormField.Required.REQUIRED, 1, 1));
        webWorkOrderFormDefaultFields.add(new FormField("info", FacilioField.FieldDisplayType.TEXTAREA, "Info", FormField.Required.OPTIONAL, 2, 1));

        List<FormField> webWorkOrderFormFields = new ArrayList<>();
        webWorkOrderFormFields.addAll(webWorkOrderFormDefaultFields);

        FormSection defaultSection = new FormSection("SERVICEORDERACTIVITY", 1, webWorkOrderFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        webWorkOrderForm.setSections(Collections.singletonList(defaultSection));
        webWorkOrderForm.setIsSystemForm(true);
        webWorkOrderForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(webWorkOrderForm);
    }

}
