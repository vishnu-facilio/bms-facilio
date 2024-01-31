package com.facilio.bmsconsoleV3.signup.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

public class AddOCROutputModule extends BaseModuleConfig {

    public AddOCROutputModule(){
        setModuleName(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_MODULE);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule OutputModule = addOcrOutputModule(modBean,orgId);
        
        addOcrOutputField(modBean,OutputModule,orgId);
    }

    private FacilioModule addOcrOutputField(ModuleBean modBean, FacilioModule OutputModule, long orgId) throws Exception{
		
    	List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule ocrOutputField = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD,"Ocr Output Field","OCR_OUTPUT_FIELDS",
                FacilioModule.ModuleType.BASE_ENTITY,false);
        
        ocrOutputField.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField ocrTemplate =  FieldFactory.getDefaultField("template","Template","TEMPLATE_ID",FieldType.LOOKUP);
        ocrTemplate.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(ocrTemplate);
        
        LookupField ocrOutputModule =  FieldFactory.getDefaultField("ocrOutputModule","Output Module","OCR_OUTPUT_MODULES",FieldType.LOOKUP);
        ocrOutputModule.setLookupModule(OutputModule);
        fields.add(ocrOutputModule);
        
        fields.add(FieldFactory.getDefaultField("outputFieldId", "Output Field Id", "FIELD_ID", FieldType.NUMBER));
        

        SystemEnumField OCROutputFieldType = (SystemEnumField) FieldFactory.getDefaultField("type", "Rule Type", "TYPE", FieldType.SYSTEM_ENUM);
        OCROutputFieldType.setEnumName("OCRTemplateOutputFieldType"); // need to be impl
        fields.add(OCROutputFieldType);
        
        fields.add(FieldFactory.getDefaultField("regex","Regex","REGEX",FieldType.STRING));
        
        fields.add(FieldFactory.getDefaultField("pageNumber","Page Number","PAGE_NUMBER",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("fieldKey","Field Key","FIELD_KEY",FieldType.STRING));
        
        fields.add(FieldFactory.getDefaultField("tableName","Table Name","TABLE_NAME",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("row","Row","ROW_INDEX",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("column","Column","COLUMN_INDEX",FieldType.NUMBER));
        
        fields.add(FieldFactory.getDefaultField("uniqueKey","Unique Key","UNIQUE_KEY",FieldType.STRING));
        
        fields.add(FieldFactory.getDefaultField("dateFormat","Date Format","DATE_FORMAT",FieldType.STRING));
        
        LookupField variableField =  FieldFactory.getDefaultField("variable","Variable","VARIABLE_ID",FieldType.LOOKUP);
        variableField.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_VARIABLES),"OCR_TEMPLATE_VARIABLES module doesn't exists."));
        fields.add(variableField);
        
        
        ocrOutputField.setFields(fields);
        modules.add(ocrOutputField);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        return ocrOutputField;
		
	}
	private FacilioModule addOcrOutputModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule ocrOutputModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_MODULE,"Ocr Output Module","OCR_OUTPUT_MODULES",
                FacilioModule.ModuleType.BASE_ENTITY,false);
        
        ocrOutputModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField ocrTemplate =  FieldFactory.getDefaultField("template","Template","TEMPLATE_ID",FieldType.LOOKUP);
        ocrTemplate.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(ocrTemplate);
        
        fields.add(FieldFactory.getDefaultField("outputModuleId", "Output Module Id", "OUTPUT_MODULE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("parentRecordId", "Parent Record Id", "PARENT_RECORD_ID", FieldType.NUMBER));
        

        SystemEnumField lineItemRuleType = (SystemEnumField) FieldFactory.getDefaultField("type", "Rule Type", "TYPE", FieldType.SYSTEM_ENUM);
        lineItemRuleType.setEnumName("OCROutputModuleType"); // need to be impl
        fields.add(lineItemRuleType);
        
        SystemEnumField recordType = (SystemEnumField) FieldFactory.getDefaultField("recordType", "Record Type", "RECORD_TYPE", FieldType.SYSTEM_ENUM);
        recordType.setEnumName("OCROutputRecordType");	 // need to be impl
        fields.add(recordType);
        
        
        ocrOutputModule.setFields(fields);
        modules.add(ocrOutputModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        return ocrOutputModule;
    }

}
