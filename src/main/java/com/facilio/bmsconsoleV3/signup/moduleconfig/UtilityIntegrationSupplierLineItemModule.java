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

public class UtilityIntegrationSupplierLineItemModule extends BaseModuleConfig {

    public UtilityIntegrationSupplierLineItemModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_SUPPLIER_LINE_ITEMS);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationSupplierLineItems = addUtilityIntegrationSupplierLineItems();
        modules.add(utilityIntegrationSupplierLineItems);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    public FacilioModule addUtilityIntegrationSupplierLineItems() throws Exception{

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationSupplierLineItems", "Utility Integration Supplier Line Items", "Utility_Integration_Line_Items",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField utilityIntegrationBills = FieldFactory.getDefaultField("utilityIntegrationBill", "Utility Integration Bill ", "UTILITY_INTEGRATION_BILL", FieldType.LOOKUP);
        utilityIntegrationBills.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
        fields.add(utilityIntegrationBills);

        StringField name = (StringField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING);
        fields.add(name);

        DateField start =  FieldFactory.getDefaultField("start", "Start", "START", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(start);

        DateField end =  FieldFactory.getDefaultField("end", "End", "END", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(end);

        StringField unit = (StringField) FieldFactory.getDefaultField("unit", "Unit", "UNIT", FieldType.STRING);
        fields.add(unit);

        NumberField cost = FieldFactory.getDefaultField("cost","Cost","COST", FieldType.DECIMAL);
        fields.add(cost);

        NumberField volume = FieldFactory.getDefaultField("volume","volume","VOLUME", FieldType.DECIMAL);
        fields.add(volume);

        NumberField rate = FieldFactory.getDefaultField("rate","rate","RATE", FieldType.DECIMAL);
        fields.add(rate);

        StringField chargeKind = (StringField) FieldFactory.getDefaultField("chargeKind", "Kind", "CHARGE_KIND", FieldType.STRING);
        fields.add(chargeKind);

        module.setFields(fields);
        return module;
    }
}
