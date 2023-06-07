package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class UtilityIntegrationTierItemModule extends BaseModuleConfig {

    public UtilityIntegrationTierItemModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_TIER_ITEMS);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationTierItems = addUtilityIntegrationTierItems();
        modules.add(utilityIntegrationTierItems);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    public FacilioModule addUtilityIntegrationTierItems() throws Exception{

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationTierItems", "Utility Integration Tier Items", "Utility_Integration_Tier_Items",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField utilityIntegrationBills = FieldFactory.getDefaultField("utilityIntegrationBill", "Utility Integration Bill ", "UTILITY_INTEGRATION_BILL", FieldType.LOOKUP);
        utilityIntegrationBills.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
        fields.add(utilityIntegrationBills);



        StringField name = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING);
        fields.add(name);

        NumberField level = FieldFactory.getDefaultField("level","Level","LEVEL", FieldType.NUMBER);
        fields.add(level);

        StringField unit = (StringField) FieldFactory.getDefaultField("unit", "Unit", "UNIT", FieldType.STRING);
        fields.add(unit);

        NumberField cost = FieldFactory.getDefaultField("cost","Cost","COST", FieldType.DECIMAL);
        fields.add(cost);

        NumberField volume = FieldFactory.getDefaultField("volume","volume","VOLUME", FieldType.DECIMAL);
        fields.add(volume);


        module.setFields(fields);
        return module;
    }
}
