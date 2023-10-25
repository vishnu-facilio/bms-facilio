package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.*;


public class UtilityIntegrationTariffModule extends BaseModuleConfig {
    public static List<String> supportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

    public UtilityIntegrationTariffModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_TARIFF);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationTariffModule = addUtilityIntegrationTariffModule();
        modules.add(utilityIntegrationTariffModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        List<FacilioModule> modules1 = new ArrayList<>();

//        FacilioModule utilityIntegrationTariffSlabModule = addUtilityIntegrationTariffSlabModule();
//        modules1.add(utilityIntegrationTariffSlabModule);
//
//        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
//        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
//        addModuleChain1.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,utilityIntegrationTariffModule.getName());
//        addModuleChain1.execute();

    }

    public FacilioModule addUtilityIntegrationTariffModule() throws Exception{

            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

            FacilioModule module = new FacilioModule("utilityIntegrationTariff", "Utility Integration Tariff", "Utility_Integration_Tariff",FacilioModule.ModuleType.BASE_ENTITY);

            List<FacilioField> fields = new ArrayList<>();

            NumberField flatRatePerUnit = FieldFactory.getDefaultField("flatRatePerUnit","Flat Rate Per Unit","FLAT_RATE_PER_UNIT",FieldType.DECIMAL);
            fields.add(flatRatePerUnit);

            NumberField fuelSurcharge = FieldFactory.getDefaultField("fuelSurcharge","Fuel Surcharge","FUEL_SURCHARGE",FieldType.DECIMAL);
            fields.add(fuelSurcharge);

            DateField fromDate =  FieldFactory.getDefaultField("fromDate", "From", "FROM_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
            fields.add(fromDate);

            DateField toDate =  FieldFactory.getDefaultField("toDate", "To", "TO_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
            fields.add(toDate);

            StringField unit = (StringField) FieldFactory.getDefaultField("unit", "Unit", "UNIT", FieldType.STRING);
            fields.add(unit);

            SystemEnumField utilityProviders = (SystemEnumField) FieldFactory.getDefaultField("utilityProviders", "Utility Providers", "UTILITY_PROVIDER", FieldType.SYSTEM_ENUM);
            utilityProviders.setEnumName("UtilityProviders");
            fields.add(utilityProviders);

            SystemEnumField utilityType = (SystemEnumField) FieldFactory.getDefaultField("utilityType", "Utility Type", "UTILITY_TYPE", FieldType.SYSTEM_ENUM);
            utilityType.setEnumName("UtilityType");
            fields.add(utilityType);

            StringField name = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true);
            fields.add(name);

            StringField description = (StringField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING);
            fields.add(description);

            module.setFields(fields);
            return module;
        }
    public FacilioModule addUtilityIntegrationTariffSlabModule() throws Exception{

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationTariffSlab", "Utility Integration Tariff Slab", "Utility_Integration_Tariff_Slab",FacilioModule.ModuleType.SUB_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        StringField name = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true);
        fields.add(name);

        NumberField from =  FieldFactory.getDefaultField("from", "From", "FROM_RANGE", FieldType.DECIMAL);
        fields.add(from);

        NumberField to =  FieldFactory.getDefaultField("to", "To ", "TO_RANGE", FieldType.DECIMAL);
        fields.add(to);

        NumberField price = FieldFactory.getDefaultField("price","Price","PRICE", FieldType.DECIMAL);
        fields.add(price);

//        NumberField consumption = FieldFactory.getDefaultField("consumption","Consumption","CONSUMPTION",FieldType.DECIMAL);
//        fields.add(consumption);
//
//        NumberField amount = FieldFactory.getDefaultField("amount","Amount","AMOUNT",FieldType.DECIMAL);
//        fields.add(amount);

        LookupField tariff = FieldFactory.getDefaultField("tariff", "Utility Tariff", "UTILITY_INTEGRATION_TARIFF", FieldType.LOOKUP);
        tariff.setLookupModule(bean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF));
        fields.add(tariff);

        module.setFields(fields);
        return module;
    }
        public List<Map<String, Object>> getViewsAndGroups() {
            List<Map<String, Object>> groupVsViews = new ArrayList<>();
            Map<String, Object> groupDetails;

            int order = 1;
            ArrayList<FacilioView> utilityTariffModule = new ArrayList<FacilioView>();
            utilityTariffModule.add(getUtilityTariffViews().setOrder(order++));

            groupDetails = new HashMap<>();
            groupDetails.put("name", "systemviews");
            groupDetails.put("displayName", "System Views");
            groupDetails.put("moduleName", FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
            groupDetails.put("views", utilityTariffModule);
            groupDetails.put("appLinkNames", UtilityIntegrationTariffModule.supportedApps);
            groupVsViews.add(groupDetails);

            return groupVsViews;
        }

        private FacilioView getUtilityTariffViews() {

            List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityIntegrationTariff.ID", FieldType.NUMBER), true));

            FacilioView utilityTariffView = new FacilioView();
            utilityTariffView.setName("all");
            utilityTariffView.setDisplayName("All Tariff");
            utilityTariffView.setModuleName(FacilioConstants.UTILITY_INTEGRATION_TARIFF);
            utilityTariffView.setSortFields(sortFields);
            utilityTariffView.setAppLinkNames(UtilityIntegrationTariffModule.supportedApps);

            List<ViewField> utilityTariffViewFields = new ArrayList<>();

            utilityTariffViewFields.add(new ViewField("name","Name"));
            utilityTariffViewFields.add(new ViewField("description","Description"));
            utilityTariffViewFields.add(new ViewField("utilityProviders","Utility Providers"));
            utilityTariffViewFields.add(new ViewField("utilityType","Utility Type"));

            utilityTariffView.setFields(utilityTariffViewFields);

            return utilityTariffView;
        }
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityIntegrationTariffModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF);
        FacilioModule utilityIntegrationTariffSlabModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB);

        FacilioForm utilityIntegrationTariffModuleForm = new FacilioForm();
        utilityIntegrationTariffModuleForm.setDisplayName("UTILITY INTEGRATION TARIFF");
        utilityIntegrationTariffModuleForm.setName("default_utilityIntegrationTariff_web");
        utilityIntegrationTariffModuleForm.setModule(utilityIntegrationTariffModule);
        utilityIntegrationTariffModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        utilityIntegrationTariffModuleForm.setAppLinkNamesForForm(UtilityIntegrationTariffModule.supportedApps);

        List<FormField> utilityIntegrationTariffModuleFormDefaultFields = new ArrayList<>();
        int seqNum = 0;
        utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seqNum, 1));
        utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seqNum, 1));
        //utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("fromDate", FacilioField.FieldDisplayType.DATE, "From", FormField.Required.OPTIONAL, ++seqNum, 2));
        //utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("toDate", FacilioField.FieldDisplayType.DATE, "To", FormField.Required.OPTIONAL, ++seqNum, 2));
        //utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("flatRatePerUnit", FacilioField.FieldDisplayType.DECIMAL, "Flat Rate Per Unit", FormField.Required.OPTIONAL, ++seqNum, 2));
        utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("utilityProviders", FacilioField.FieldDisplayType.SELECTBOX, "Utility Providers", FormField.Required.REQUIRED, ++seqNum, 2));
        utilityIntegrationTariffModuleFormDefaultFields.add(new FormField("utilityType", FacilioField.FieldDisplayType.SELECTBOX, "Utility Type", FormField.Required.REQUIRED, ++seqNum, 2));

        List<FormField> utilityIntegrationTariffModuleFormFields = new ArrayList<>();
        utilityIntegrationTariffModuleFormFields.addAll(utilityIntegrationTariffModuleFormDefaultFields);

        FormSection defaultSection = new FormSection("New Tariff", 1, utilityIntegrationTariffModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);
        utilityIntegrationTariffModuleForm.setSections(Collections.singletonList(defaultSection));
        utilityIntegrationTariffModuleForm.setIsSystemForm(true);
        utilityIntegrationTariffModuleForm.setType(FacilioForm.Type.FORM);


        FacilioForm utilityIntegrationTariffSlabForm = new FacilioForm();
        utilityIntegrationTariffSlabForm.setDisplayName("Utility Integration Tariff Slab");
        utilityIntegrationTariffSlabForm.setName("default_"+FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB+"_web");
        utilityIntegrationTariffSlabForm.setModule(utilityIntegrationTariffSlabModule);
        utilityIntegrationTariffSlabForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        utilityIntegrationTariffSlabForm.setAppLinkNamesForForm(UtilityIntegrationTariffSlabModule.supportedApps);

        List<FormField> utilityIntegrationTariffSlabFormFields = new ArrayList<>();
        int seq = 0;
        utilityIntegrationTariffSlabFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,++seq, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("from", FacilioField.FieldDisplayType.NUMBER, "From", FormField.Required.REQUIRED, ++seq, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("to", FacilioField.FieldDisplayType.NUMBER, "To", FormField.Required.REQUIRED, ++seq, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("price", FacilioField.FieldDisplayType.NUMBER, "Price", FormField.Required.REQUIRED, ++seq, 1));
        //utilityIntegrationTariffSlabFormFields.add(new FormField("tariff", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Tariff", FormField.Required.OPTIONAL, "utilityIntegrationTariff",++seqNum, 1));

        FormSection section = new FormSection("Slabs", 1, utilityIntegrationTariffSlabFormFields, true);
        section.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webWorkOrderFormSections = new ArrayList<>();
        webWorkOrderFormSections.add(section);


        utilityIntegrationTariffSlabForm.setSections(webWorkOrderFormSections);
        utilityIntegrationTariffSlabForm.setIsSystemForm(true);
        utilityIntegrationTariffSlabForm.setName("Slab");
        utilityIntegrationTariffSlabForm.setType(FacilioForm.Type.SUB_FORM);

        utilityIntegrationTariffModuleForm.setSubFormList(Arrays.asList(utilityIntegrationTariffSlabForm));
        List<FacilioForm> moduleForms = new ArrayList<>();
        moduleForms.add(utilityIntegrationTariffModuleForm);


        return moduleForms;


    }


}

