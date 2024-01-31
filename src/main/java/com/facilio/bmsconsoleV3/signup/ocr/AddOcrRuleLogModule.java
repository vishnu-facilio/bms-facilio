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
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddOcrRuleLogModule extends BaseModuleConfig {
    public AddOcrRuleLogModule(){
        setModuleName(FacilioConstants.Ocr.OCR_RULE_LOG);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        addOcrRuleLogModule(modBean,orgId);
    }
    private FacilioModule addOcrRuleLogModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule ocrRuleLogModule = new FacilioModule(FacilioConstants.Ocr.OCR_RULE_LOG,"Ocr Rule Log","OCR_RULE_LOG",
                FacilioModule.ModuleType.BASE_ENTITY,true);
        ocrRuleLogModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField billTemplate =  FieldFactory.getDefaultField("billTemplate","Bill Template","TEMPLATE_ID",FieldType.LOOKUP);
        billTemplate.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.BILL_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(billTemplate);

//        LookupField parsedBill =  FieldFactory.getDefaultField("parsedBill","Parsed Bill","PARSE_BILL_ID",FieldType.LOOKUP);
//        parsedBill.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.PARSED_BILL),"Parse Bill module doesn't exists."));
//        fields.add(parsedBill);

        LookupField actualBill =  FieldFactory.getDefaultField("actualBill","Actual Bill","ACTUAL_BILL_ID",FieldType.LOOKUP);
        actualBill.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.ACTUAL_BILL),"Actual Bill module doesn't exists."));
        fields.add(actualBill);

        SystemEnumField billType = (SystemEnumField) FieldFactory.getDefaultField("ocrRuleType", "Rule Type", "RULE_TYPE", FieldType.SYSTEM_ENUM);
        billType.setEnumName("ocrRuleType");
        fields.add(billType);

        SystemEnumField parseStatus = (SystemEnumField) FieldFactory.getDefaultField("parseStatus", "Parse Status", "PARSE_STATUS", FieldType.SYSTEM_ENUM);
        parseStatus.setEnumName("ParseStatus");
        fields.add(parseStatus);

        fields.add(FieldFactory.getDefaultField("mapFieldId","Map Field ID","MAP_FIELD_ID",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("errorMeta","ERROR MESSAGE","ERROR_META",FieldType.STRING));

        fields.add(FieldFactory.getDefaultField("parsingField","Parsing Field","PARSING_FIELD",FieldType.STRING));

        SystemEnumField parsingType = (SystemEnumField) FieldFactory.getDefaultField("parsingType", "Parsing Type", "PARSING_TYPE", FieldType.SYSTEM_ENUM);
        parsingType.setEnumName("parsingType");
        fields.add(parsingType);

        fields.add(FieldFactory.getDefaultField("value","Parsed Value","PARSE_VALUE",FieldType.STRING));


        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

        fields.add(FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        ocrRuleLogModule.setFields(fields);
        modules.add(ocrRuleLogModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        return ocrRuleLogModule;
    }
}
