package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.OCRParsedDocResultContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRParsedResultTableContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateContext;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ocr.aws.TextractContext;
import com.facilio.ocr.aws.TextractUtil;
import com.facilio.v3.context.Constants;

public class OCRConstructDocResultCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	OCRTemplateContext ocrTemplate = (OCRTemplateContext) context.get(FacilioConstants.Ocr.OCR_TEMPLATE);
    	Long docId = (Long) context.get(FacilioConstants.Ocr.DOC_ID);
    	Long parentRecordId = (Long) context.get(FacilioConstants.Ocr.PARENT_RECORD_ID);
    	Long parentModuleId = (Long) context.get(FacilioConstants.Ocr.PARENT_MODULE_ID);
    	
    	ModuleBean modBean = Constants.getModBean();

    	
    	if(ocrTemplate.getPageBreak() == null || ocrTemplate.getPageBreak() <= 1) {
    		
    		Map<String, Object> parsedResult = TextractUtil.getParsedBillContext(docId);
    		
    		context.put(FacilioConstants.Ocr.TEXTRACT_RESULT, parsedResult);
    		
    		long formFileId = OcrUtil.addParsedFile("form-" + docId + ".json", parsedResult.get("forms"), OcrUtil.FILE_CONTEXT_TYPE);
            long rawTextFileId = OcrUtil.addParsedFile("rawtext-" + docId + ".json", parsedResult.get("rawText"), OcrUtil.FILE_CONTEXT_TYPE);
            
            OCRParsedDocResultContext parsedDocResult = new OCRParsedDocResultContext();
            
            parsedDocResult.setOcrTemplate(ocrTemplate);
            parsedDocResult.setDocumentId(docId);
            parsedDocResult.setParsedFormId(formFileId);
            parsedDocResult.setParsedRawTextId(rawTextFileId);
            
            parsedDocResult.setSourceDocId(parentRecordId);
            parsedDocResult.setSourceModuleId(parentModuleId);
    		
            V3RecordAPI.addRecord(false, Collections.singletonList(parsedDocResult), modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT), modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT));
            
            List<TextractContext.TableContext> tables = (List<TextractContext.TableContext>) parsedResult.get("tables");
            
            if(tables != null ) {
            	
            	List<OCRParsedResultTableContext> parsedDocTableList = new ArrayList<OCRParsedResultTableContext>();
            	
            	for (TextractContext.TableContext tableContext : tables) {
                    long tableFileId = OcrUtil.addParsedFile(tableContext.getName() + docId + ".json", tableContext, OcrUtil.FILE_CONTEXT_TYPE);
                   
                    OCRParsedResultTableContext tableRelContext = new OCRParsedResultTableContext();
                    
                    tableRelContext.setDocResult(parsedDocResult);
                    tableRelContext.setTablefileId(tableFileId);
                    
                    parsedDocTableList.add(tableRelContext);
                }
            	
            	V3RecordAPI.addRecord(false, parsedDocTableList, modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT_Table), modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_PARSED_RESULT_Table));
            }

    	}
    	
		return false;
	}

}
