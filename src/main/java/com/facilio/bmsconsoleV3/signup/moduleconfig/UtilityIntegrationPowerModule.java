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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityIntegrationPowerModule extends BaseModuleConfig {

    public UtilityIntegrationPowerModule() {
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_POWER);}


    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationPowerModule = addUtilityIntegrationPowerModule();
        modules.add(utilityIntegrationPowerModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    public FacilioModule addUtilityIntegrationPowerModule() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationPower", "Utility Integration Power", "Utility_Integration_Power",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField utilityIntegrationBills = FieldFactory.getDefaultField("utilityIntegrationBill", "Utility Integration Bill ", "UTILITY_INTEGRATION_BILL", FieldType.LOOKUP);
        utilityIntegrationBills.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
        fields.add(utilityIntegrationBills);

        StringField name = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING);
        fields.add(name);

        StringField unit = (StringField) FieldFactory.getDefaultField("unit", "Unit", "UNIT", FieldType.STRING);
        fields.add(unit);

        NumberField volume = FieldFactory.getDefaultField("volume","volume","VOLUME", FieldType.DECIMAL);
        fields.add(volume);


        StringField power = (StringField) FieldFactory.getDefaultField("powerType", "Power Type", "POWER_TYPE", FieldType.STRING);
        fields.add(power);


        module.setFields(fields);
        return module;
    }
}
