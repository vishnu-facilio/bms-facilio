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

public class AddOCRTemplateVariableModule extends BaseModuleConfig {

    public AddOCRTemplateVariableModule(){
        setModuleName(FacilioConstants.Ocr.OCR_TEMPLATE_VARIABLES);
    }
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();

        FacilioModule OutputModule = addTemplateVariable(modBean,orgId);
        
    }

    private FacilioModule addTemplateVariable(ModuleBean modBean, long orgId) throws Exception{
		
    	List<FacilioModule> modules = new ArrayList<>();
        
        FacilioModule templateVariableModule = new FacilioModule(FacilioConstants.Ocr.OCR_TEMPLATE_VARIABLES,"Ocr Template Variables","OCR_TEMPLATE_VARIABLES",
                FacilioModule.ModuleType.SUB_ENTITY,false);
        
        templateVariableModule.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();

        LookupField ocrTemplate =  FieldFactory.getDefaultField("template","Template","TEMPLATE_ID",FieldType.LOOKUP);
        ocrTemplate.setLookupModule(Objects.requireNonNull(modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE),"Bill Template module doesn't exists."));
        fields.add(ocrTemplate);
        
        SystemEnumField type = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "TYPE", FieldType.SYSTEM_ENUM);
        type.setEnumName("OCRTemplateVariableType"); // need to be impl
        fields.add(type);
        
        fields.add(FieldFactory.getDefaultField("name","Name","NAME",FieldType.STRING));
        
        fields.add(FieldFactory.getDefaultField("constantValue","Field Key","CONSTANT_VALUE",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("workflowId","Workflow Id","WORKFLOW_ID",FieldType.NUMBER));
        
        
        templateVariableModule.setFields(fields);
        modules.add(templateVariableModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        return templateVariableModule;
		
	}

}