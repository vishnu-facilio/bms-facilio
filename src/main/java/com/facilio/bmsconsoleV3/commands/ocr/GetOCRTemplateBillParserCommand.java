package com.facilio.bmsconsoleV3.commands.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateTableContext;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ocr.aws.TextractContext;
import com.facilio.ocr.aws.TextractUtil;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class GetOCRTemplateBillParserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        
        OCRTemplateContext ocrTemplateContext = null;
        try {
            ocrTemplateContext = (OCRTemplateContext) context.get(FacilioConstants.Ocr.OCR_TEMPLATE);

            if(ocrTemplateContext == null){
                LOGGER.debug("Bill template not found");
                return false;
            }

            long billFileId = ocrTemplateContext.getSampleBillId();


            Map<String, Object> parsedBill = TextractUtil.getParsedBillContext(billFileId);
            if (parsedBill == null) {
                OcrUtil.updateTemplateStatus(BillTemplateContext.templateStatusEnum.PARSING_FAILED, ocrTemplateContext);
                return false;
            }

            long formFileId =OcrUtil.addParsedFile("form-" + ocrTemplateContext.getId() + ".json", parsedBill.get("forms"), OcrUtil.FILE_CONTEXT_TYPE);
            long rawTextFileId = OcrUtil.addParsedFile("rawtext-" + ocrTemplateContext.getId() + ".json", parsedBill.get("rawText"), OcrUtil.FILE_CONTEXT_TYPE);

            List<TextractContext.TableContext> tables = (List<TextractContext.TableContext>) parsedBill.get("tables");

            List<OCRTemplateTableContext> tableFileIdList = new ArrayList<>();
            for (TextractContext.TableContext tableContext : tables) {
                long tableFileId = OcrUtil.addParsedFile(tableContext.getName() + ocrTemplateContext.getId() + ".json", tableContext, OcrUtil.FILE_CONTEXT_TYPE);
                
                OCRTemplateTableContext tableRelContext = new OCRTemplateTableContext();
                tableRelContext.setTablefileId(tableFileId);
                tableRelContext.setTemplate(ocrTemplateContext);
                tableFileIdList.add(tableRelContext);
            }

            ocrTemplateContext.setParsedFormId(formFileId);
            ocrTemplateContext.setParsedRawTextId(rawTextFileId);

            OcrUtil.updateTemplateStatus(BillTemplateContext.templateStatusEnum.BILL_PARSED, ocrTemplateContext);
            OcrUtil.updateTemplateFiles(ocrTemplateContext);
            addTableFileIdBillTemplateRel(tableFileIdList);

        } catch (Exception e) {
            try {
                OcrUtil.updateTemplateStatus(BillTemplateContext.templateStatusEnum.PARSING_FAILED, ocrTemplateContext);
            } catch (Exception ex) {
                LOGGER.debug("Error in updating status", ex);
            }
            LOGGER.debug("Error in OcrParseBillHandler while parsing", e);
        }

        return false;
    }

    public void addTableFileIdBillTemplateRel(List<OCRTemplateTableContext> tableRelContexts) throws Exception {
        if(CollectionUtils.isNotEmpty(tableRelContexts)) {
        	ModuleBean modBean = Constants.getModBean();
        	V3RecordAPI.addRecord(false, tableRelContexts, modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_TABLES), modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_TABLES));
        }
    }
}
