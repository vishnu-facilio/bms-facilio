package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
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

public class UtilityIntegrationTariffSlabModule extends BaseModuleConfig {
    public static List<String> supportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

    public UtilityIntegrationTariffSlabModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityIntegrationTariffModule = moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationTariffSlabModule = addUtilityIntegrationTariffSlabModule();
        modules.add(utilityIntegrationTariffSlabModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,utilityIntegrationTariffModule.getName());
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
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
        ArrayList<FacilioView> utilityTariffSlabModule = new ArrayList<FacilioView>();
        utilityTariffSlabModule.add(getUtilityTariffSlabViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB);
        groupDetails.put("views", utilityTariffSlabModule);
        groupDetails.put("appLinkNames", UtilityIntegrationTariffSlabModule.supportedApps);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUtilityTariffSlabViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityIntegrationTariffSlab.ID", FieldType.NUMBER), true));

        FacilioView utilityTariffSlabView = new FacilioView();
        utilityTariffSlabView.setName("all");
        utilityTariffSlabView.setDisplayName("Utility Integration Tariff Slab");
        utilityTariffSlabView.setModuleName(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB);
        utilityTariffSlabView.setSortFields(sortFields);
        utilityTariffSlabView.setAppLinkNames(UtilityIntegrationTariffSlabModule.supportedApps);

        List<ViewField> utilityTariffSlabViewFields = new ArrayList<>();

        utilityTariffSlabViewFields.add(new ViewField("name","Name"));
        utilityTariffSlabViewFields.add(new ViewField("from","From"));
        utilityTariffSlabViewFields.add(new ViewField("to","To"));
        utilityTariffSlabViewFields.add(new ViewField("price","Price"));
        utilityTariffSlabViewFields.add(new ViewField("tariff","Utility Tariff"));


        utilityTariffSlabView.setFields(utilityTariffSlabViewFields);

        return utilityTariffSlabView;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityIntegrationTariffSlabModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB);

        FacilioForm utilityIntegrationTariffSlabForm = new FacilioForm();
        utilityIntegrationTariffSlabForm.setDisplayName("Utility Integration Tariff Slab");
        utilityIntegrationTariffSlabForm.setName("default_"+FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB+"_web");
        utilityIntegrationTariffSlabForm.setModule(utilityIntegrationTariffSlabModule);
        utilityIntegrationTariffSlabForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        utilityIntegrationTariffSlabForm.setAppLinkNamesForForm(UtilityIntegrationTariffSlabModule.supportedApps);

        List<FormField> utilityIntegrationTariffSlabFormFields = new ArrayList<>();
        int seqNum = 0;
        utilityIntegrationTariffSlabFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED,++seqNum, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("from", FacilioField.FieldDisplayType.NUMBER, "From", FormField.Required.REQUIRED, ++seqNum, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("to", FacilioField.FieldDisplayType.NUMBER, "To", FormField.Required.REQUIRED, ++seqNum, 1));
        utilityIntegrationTariffSlabFormFields.add(new FormField("price", FacilioField.FieldDisplayType.NUMBER, "Price", FormField.Required.REQUIRED, ++seqNum, 1));
       // utilityIntegrationTariffSlabFormFields.add(new FormField("tariff", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Tariff", FormField.Required.REQUIRED, "utilityIntegrationTariff",++seqNum, 1));

        utilityIntegrationTariffSlabForm.setFields(utilityIntegrationTariffSlabFormFields);
        FormSection section = new FormSection("Default", 1, utilityIntegrationTariffSlabFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        utilityIntegrationTariffSlabForm.setSections(Collections.singletonList(section));
        utilityIntegrationTariffSlabForm.setIsSystemForm(true);
        utilityIntegrationTariffSlabForm.setType(FacilioForm.Type.FORM);


        return Collections.singletonList(utilityIntegrationTariffSlabForm);
    }

}

