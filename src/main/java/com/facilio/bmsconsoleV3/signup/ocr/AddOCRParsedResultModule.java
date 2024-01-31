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

public class AddOCRParsedResultModule extends BaseModuleConfig {

    public AddOCRParsedResultModule(){
        setModuleName(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule parsedResultModule = addOCRParsedResultModule(modBean,orgId);
        addOCRParsedResultTableModule(parsedResultModule,orgId);
//        addTemplateLookupToPreUtilityBill(modBean,billTemplateModule);
    }


    private void addOCRParsedResultTableModule(FacilioModule parsedResultModule,Long orgId) throws Exception {
		
    	List<FacilioModule> modules = new ArrayList<>();
        FacilioModule ocrTemplateTableModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT_Table,"OCR Templates Tables","OCR_PARSED_DOC_RESULT_TABLE",
                FacilioModule.ModuleType.SUB_ENTITY,null,false);
        
        ocrTemplateTableModule.setOrgId(orgId);	

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("tablefile","Table File","FILE_ID",FieldType.FILE));
        
        LookupField template = FieldFactory.getDefaultField("docResult","Doc Result","DOC_RESULT_ID", FieldType.LOOKUP);
        template.setLookupModule(parsedResultModule);
        fields.add(template);

        ocrTemplateTableModule.setFields(fields);
        modules.add(ocrTemplateTableModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
	}
    
    
	private FacilioModule addOCRParsedResultModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule ocrTemplateParsedResultModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT,"OCR Template Parsed Result","OCR_PARSED_DOC_RESULT",
                FacilioModule.ModuleType.BASE_ENTITY,null,false);
        ocrTemplateParsedResultModule.setOrgId(orgId);	

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        
        LookupField ocrTemplate =  FieldFactory.getDefaultField("ocrTemplate","OCR Template","TEMPLATE_ID",FieldType.LOOKUP);
        ocrTemplate.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(ocrTemplate);
        
        fields.add(FieldFactory.getDefaultField("sourceDocId","Source Doc Id","SOURCE_DOC_ID",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("sourceModuleId","Source Module Id","SOURCE_MODULE_ID",FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("document","Document","DOC_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("zipFile","Zip File Id","ZIP_FILE_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("parsedForm","Form File Id","FORM_FILE_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("parsedRawText","Raw Text File Id","RAWTEXT_FILE_ID",FieldType.FILE));

        ocrTemplateParsedResultModule.setFields(fields);
        modules.add(ocrTemplateParsedResultModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return ocrTemplateParsedResultModule;
    }


}
