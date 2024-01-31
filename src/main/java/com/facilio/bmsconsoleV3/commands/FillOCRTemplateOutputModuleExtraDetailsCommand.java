package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputFieldContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputModuleContext;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class FillOCRTemplateOutputModuleExtraDetailsCommand extends FacilioCommand {
   

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<OCRTemplateOutputModuleContext> outputModules =  Constants.getRecordList((FacilioContext) context);
		
		for(OCRTemplateOutputModuleContext outputModule : outputModules) {
			
			List<OCRTemplateOutputFieldContext> fields = OcrUtil.getTemplateOututFields(outputModule.getTemplate().getId(), outputModule.getId());
			
			outputModule.setOutputFields(fields);
		}
		
		return false;
	}

}
