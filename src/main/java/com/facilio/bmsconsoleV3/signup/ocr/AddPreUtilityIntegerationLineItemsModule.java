package com.facilio.bmsconsoleV3.signup.ocr;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
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

public class AddPreUtilityIntegerationLineItemsModule extends BaseModuleConfig {
    public AddPreUtilityIntegerationLineItemsModule(){
        setModuleName(FacilioConstants.Ocr.PRE_UTILITY_LINE_ITEMS);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule preUtilityIntegrationLineItems = addPreUtilityIntegrationLineItems();
        modules.add(preUtilityIntegrationLineItems);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    public FacilioModule addPreUtilityIntegrationLineItems() throws Exception{

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.Ocr.PRE_UTILITY_LINE_ITEMS, "Pre Utility Integration Line Items", "Parsed_Integration_Line_Items",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parsedBill = FieldFactory.getDefaultField("parsedBill", "Parsed Bill", "PARSED_BILL", FieldType.LOOKUP);
        parsedBill.setLookupModule(moduleBean.getModule(FacilioConstants.Ocr.PARSED_BILL));
        fields.add(parsedBill);

        SystemEnumField recordType = (SystemEnumField) FieldFactory.getDefaultField("recordType", "Record Type", "RECORD_TYPE", FieldType.SYSTEM_ENUM);
        recordType.setEnumName("OCROutputRecordType");
        fields.add(recordType);

//        LookupField utilityIntegrationBills = FieldFactory.getDefaultField("utilityIntegrationBills", "Utility Integration Bills", "UTILITY_INTEGRATION_BILLS_ID", FieldType.LOOKUP);
//        utilityIntegrationBills.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
//        fields.add(utilityIntegrationBills);

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

        NumberField volume = FieldFactory.getDefaultField("volume","Volume","VOLUME", FieldType.DECIMAL);
        fields.add(volume);

        NumberField rate = FieldFactory.getDefaultField("rate","Rate","RATE", FieldType.DECIMAL);
        fields.add(rate);

        StringField chargeKind = (StringField) FieldFactory.getDefaultField("chargeKind", "Kind", "CHARGE_KIND", FieldType.STRING);
        fields.add(chargeKind);

//        EnumField chargeKind = FieldFactory.getDefaultField("chargeKind","Kind","CHARGE_KIND", FieldType.ENUM);
//        List<String> kinds = Arrays.asList(
//                "delivery", "supply", "other" );
//
//        List<EnumFieldValue<Integer>> kindValues = kinds.stream().map( val -> {
//            int index = kinds.indexOf(val)+1;
//            return new EnumFieldValue<>(index, val, index, true);
//        }).collect(Collectors.toList());
//        chargeKind.setValues(kindValues);
//
//
//        fields.add(chargeKind);

//        StringField chargeKind = (StringField) FieldFactory.getDefaultField("chargeKind", "Kind", "CHARGE_KIND", FieldType.STRING);
//        fields.add(chargeKind);

        module.setFields(fields);
        return module;
    }
}
