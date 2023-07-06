package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
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
    }
    private void addTerritoryModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule territoryModule = new FacilioModule(FacilioConstants.Territory.TERRITORY,"Territory","TERRITORY", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<FacilioField> territoryFields = new ArrayList<>();
        territoryFields.add(new StringField(territoryModule,"name","Name",FacilioField.FieldDisplayType.TEXTBOX,"NAME", FieldType.STRING,true,false,true,true));
        territoryFields.add(new StringField(territoryModule,"description","Description",FacilioField.FieldDisplayType.TEXTAREA,"DESCRIPTION",FieldType.STRING,false,false,true,false));
        territoryFields.add(new StringField(territoryModule,"color","Territory Color",FacilioField.FieldDisplayType.COLOR_PICKER,"COLOR",FieldType.STRING,true,false,true,false));
        territoryFields.add(new StringField(territoryModule,"geography","Geography",FacilioField.FieldDisplayType.TEXTAREA,"GEO_JSON",FieldType.STRING,true,false,true,false));
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
}
