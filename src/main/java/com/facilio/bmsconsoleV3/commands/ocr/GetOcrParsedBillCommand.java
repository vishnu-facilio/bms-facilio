package com.facilio.bmsconsoleV3.commands.ocr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateTableContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.ocr.aws.TextractContext;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import com.google.gson.Gson;

public class GetOcrParsedBillCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long templateId = (long) context.get(FacilioConstants.Ocr.TEMPLATE_ID);
        Map<String, Object> result = new HashMap<>();

       BillTemplateContext billTemplateContext = V3RecordAPI.getRecord(FacilioConstants.Ocr.BILL_TEMPLATE, templateId, BillTemplateContext.class);
       if(billTemplateContext == null){
           throw new IllegalArgumentException("Invalid Template id");
       }

        FileStore fs = FacilioFactory.getFileStore();

        InputStream formFis = fs.readFile(billTemplateContext.getParsedFormId());
        InputStream rawTextFis = fs.readFile(billTemplateContext.getParsedRawTextId());

        Gson gson = new Gson();
        List<TextractContext.FormContext> forms = (List<TextractContext.FormContext>) gson.fromJson(getParsedBillFromFile(formFis) , Object.class);
        Object rawText =  gson.fromJson(getParsedBillFromFile(rawTextFis) , Object.class);

        List<Long> tableFileIds = getTemplateTableFileIds(templateId);
        List<TextractContext.TableContext> tableResult = new ArrayList<>();

        for (Long fileId: tableFileIds) {
            InputStream tableFis =  fs.readFile(fileId);
            TextractContext.TableContext table = gson.fromJson(getParsedBillFromFile(tableFis) , TextractContext.TableContext.class);
            tableResult.add(table);
        }

        result.put("forms", forms);
        result.put("rawText", rawText);
        result.put("tables", tableResult);

        context.put(FacilioConstants.ContextNames.RESULT, result);

        return false;
    }

    public static String getParsedBillFromFile(InputStream fis) throws Exception{
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();
        return new String(data);
    }

    public static List<Long> getTemplateTableFileIds(long templateId) throws Exception{
        List<Long> fileIds = new ArrayList<>();
        
        ModuleBean modBean = Constants.getModBean();
        
        SelectRecordsBuilder<OCRTemplateTableContext> select = new SelectRecordsBuilder<OCRTemplateTableContext>()
        		.moduleName(FacilioConstants.Ocr.OCR_TEMPLATE_TABLES)
        		.select(modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_TABLES))
        		.beanClass(OCRTemplateTableContext.class)
        		.andCondition(CriteriaAPI.getCondition("TEMPLATE_ID","templateId",String.valueOf(templateId), NumberOperators.EQUALS));
        		
        List<OCRTemplateTableContext> tableRelContexts = select.get();
        if(CollectionUtils.isNotEmpty(tableRelContexts)) {
        	fileIds = tableRelContexts.stream().map(OCRTemplateTableContext::getTablefileId).collect(Collectors.toList());
        }

        return fileIds;
    }
}
