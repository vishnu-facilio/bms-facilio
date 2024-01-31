package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputFieldContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputModuleContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext.TemplateVariableTypeEnum;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetOCRTemplateOutputModuleAfterSaveCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
    	
    	
    	String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<OCRTemplateOutputModuleContext> outputModules = recordMap.get(moduleName);
        ModuleBean modBean = Constants.getModBean();
        
        // delete field of specific type here 
        
        if(CollectionUtils.isNotEmpty(outputModules)){
            for(OCRTemplateOutputModuleContext outputModule : outputModules){
            	
            	OcrUtil.deleteOutputFields(outputModule.getTemplate().getId(), outputModule.getId(), outputModule.getFieldTypeEnum());
            	
            	if(outputModule.getOutputFields() != null) {
            		for(OCRTemplateOutputFieldContext outputField : outputModule.getOutputFields()) {
            			outputField.setOcrOutputModule(new OCRTemplateOutputModuleContext(outputModule.getId()));
            			outputField.setTemplate(outputModule.getTemplate());
            		}
            		V3RecordAPI.addRecord(false, outputModule.getOutputFields(), modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD), modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD));
            	}
            }
            
        }
    	
    	
		// TODO Auto-generated method stub
		return false;
	}

}
