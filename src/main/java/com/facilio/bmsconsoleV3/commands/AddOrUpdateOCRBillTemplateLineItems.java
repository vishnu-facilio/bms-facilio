package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputModuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class AddOrUpdateOCRBillTemplateLineItems extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	List<OCRTemplateOutputModuleContext> lineItems = (List<OCRTemplateOutputModuleContext>) context.get(FacilioConstants.Ocr.LINE_ITEMS);
    	
    	FacilioModule module = Constants.getModBean().getModule(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_MODULE);
    	
    	List<Long> ids = new ArrayList<Long>();
    	for(OCRTemplateOutputModuleContext lineItem : lineItems) {
    		
    		if(lineItem.getId() <= 0) {
    			FacilioContext resContext = V3Util.createRecord(module, FieldUtil.getAsProperties(lineItem));
    			Long recordId = Constants.getRecordList(resContext).get(0).getId();
    			ids.add(recordId);
    		}
    		else {
    			V3Util.updateBulkRecords(module.getName(), FieldUtil.getAsProperties(lineItem),Collections.singletonList(lineItem.getId()), false,true);
    			ids.add(lineItem.getId());
    		}
    		
    	}
    	
    	FacilioContext rescontext = V3Util.getSummary(module.getName(), ids);
    	
    	List<ModuleBaseWithCustomFields> resultList = Constants.getRecordList(rescontext);
    	
    	context.put(FacilioConstants.Ocr.LINE_ITEM_RES, resultList);
		return false;
	}

}
