package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TerritoryModule extends BaseModuleConfig {
    public TerritoryModule(){setModuleName(FacilioConstants.Territory.TERRITORY);}
    public void addData() throws Exception {
        addTerritoryModule();
        addTerritoryLookupInPeople();
        addTerritoryLookupInSite();
        addActivityModuleForTerritory();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.Territory.TERRITORY));
    }
    private void addTerritoryModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule territoryModule = new FacilioModule(FacilioConstants.Territory.TERRITORY,"Territory","TERRITORY", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<FacilioField> territoryFields = new ArrayList<>();
        territoryFields.add(new StringField(territoryModule,"name","Name",FacilioField.FieldDisplayType.TEXTBOX,"NAME", FieldType.STRING,true,false,true,true));
        territoryFields.add(new StringField(territoryModule,"description","Description",FacilioField.FieldDisplayType.TEXTAREA,"DESCRIPTION",FieldType.STRING,false,false,true,false));
        territoryFields.add(new StringField(territoryModule,"color","Territory Color",FacilioField.FieldDisplayType.COLOR_PICKER,"COLOR",FieldType.STRING,true,false,true,false));
        territoryFields.add(new StringField(territoryModule,"geography","Geography",FacilioField.FieldDisplayType.TEXTAREA,"GEO_JSON",FieldType.STRING,true,false,true,false));

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        territoryFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        territoryFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        territoryFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        territoryFields.add(approvalFlowIdField);

        territoryModule.setFields(territoryFields);
        modules.add(territoryModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addTerritoryLookupInPeople() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        LookupField territoryLookup = new LookupField(modBean.getModule(FacilioConstants.ContextNames.PEOPLE),FacilioConstants.Territory.TERRITORY,"Territory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TERRITORY_ID",FieldType.LOOKUP,false,false,true,false,"Territory",modBean.getModule(FacilioConstants.Territory.TERRITORY));
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PEOPLE);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(territoryLookup));
        chain.execute();
    }
    private void addTerritoryLookupInSite() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        LookupField territoryLookup = new LookupField(modBean.getModule(FacilioConstants.ContextNames.SITE),FacilioConstants.Territory.TERRITORY,"Territory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TERRITORY_ID",FieldType.LOOKUP,false,false,true,false,"Territory",modBean.getModule(FacilioConstants.Territory.TERRITORY));
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.SITE);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(territoryLookup));
        chain.execute();
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule territoryModule = modBean.getModule(FacilioConstants.Territory.TERRITORY);
        FacilioForm territoryForm =new FacilioForm();
        territoryForm.setDisplayName("Standard");
        territoryForm.setName("default_asset_web");
        territoryForm.setModule(territoryModule);
        territoryForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        territoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        List<FormField> territoryFormFields = new ArrayList<>();
        territoryFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,1, 1));
        territoryFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        territoryFormFields.add(new FormField("color",FacilioField.FieldDisplayType.COLOR_PICKER,"Territory Color",FormField.Required.REQUIRED,3,3));
        territoryFormFields.add(new FormField("geography",FacilioField.FieldDisplayType.TEXTAREA,"Geography", FormField.Required.REQUIRED,4,1));
        FormSection section = new FormSection("Default", 1, territoryFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        territoryForm.setSections(Collections.singletonList(section));
        territoryForm.setIsSystemForm(true);
        territoryForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> territoryModuleForms = new ArrayList<>();
        territoryModuleForms.add(territoryForm);
        return territoryModuleForms;
    }

    public void addActivityModuleForTerritory() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule territory = modBean.getModule(FacilioConstants.Territory.TERRITORY);

        FacilioModule module = new FacilioModule(FacilioConstants.Territory.TERRITORY_ACTIVITY,
                "Territory Activity",
                "Territory_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(territory.getModuleId(), module.getModuleId());
    }
}
