package com.facilio.bmsconsoleV3.signup.ocr;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.SystemEnumField;

public class AddOCRTemplateModule extends BaseModuleConfig {

    public AddOCRTemplateModule(){
        setModuleName(FacilioConstants.Ocr.OCR_TEMPLATE);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule templateModule = addocrTemplateModule(modBean,orgId);
        addTemplateTableRel(templateModule,orgId);
    }


    private void addTemplateTableRel(FacilioModule templateModule,Long orgId) throws Exception {
		
    	List<FacilioModule> modules = new ArrayList<>();
        FacilioModule ocrTemplateTableModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_TABLES,"OCR Templates Tables","OCR_TEMPLATE_TABLES",
                FacilioModule.ModuleType.SUB_ENTITY,null,false);
        
        ocrTemplateTableModule.setOrgId(orgId);	

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("tablefile","Table File","FILE_ID",FieldType.FILE));
        
        LookupField template = FieldFactory.getDefaultField("template","Template","TEMPLATE_ID", FieldType.LOOKUP);
        template.setLookupModule(templateModule);
        fields.add(template);

        ocrTemplateTableModule.setFields(fields);
        modules.add(ocrTemplateTableModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
	}
    
    
	private FacilioModule addocrTemplateModule(ModuleBean moduleBean, long orgId) throws Exception{
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule ocrTemplateModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE,"OCR Templates","OCR_TEMPLATE",
                FacilioModule.ModuleType.BASE_ENTITY,null,true);
        ocrTemplateModule.setOrgId(orgId);	

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTBOX));
        fields.add(FieldFactory.getDefaultField("pageBreak","Page Break","PAGE_BREAK",FieldType.NUMBER));
        


        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("ocrTemplateStatusEnum");
        fields.add(status);


        fields.add(FieldFactory.getDefaultField("sampleBill","Sample Bill","SAMPLE_BILL_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("zipFileId","Zip File Id","ZIP_FILE_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("parsedForm","Form File Id","FORM_FILE_ID",FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("parsedRawText","Raw Text File Id","RAW_TEXT_FILE_ID",FieldType.FILE));

        ocrTemplateModule.setFields(fields);
        modules.add(ocrTemplateModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        return ocrTemplateModule;
    }


}
