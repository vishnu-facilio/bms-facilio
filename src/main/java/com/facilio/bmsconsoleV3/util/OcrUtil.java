package com.facilio.bmsconsoleV3.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.ActualBillContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputFieldContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputFieldContext.TemplateOutputFieldExtractTypeEnum;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputModuleContext;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateVariableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.message.WebMessage;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.gson.Gson;

public class OcrUtil {

	public static final String FILE_CONTEXT_TYPE = "application/json";
	
	public static void updateTemplateStatus(OCRTemplateContext.templateStatusEnum status, OCRTemplateContext ocrTemplateContext) throws Exception {
        
		ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ocrTemplateModule = moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE);

        FacilioField statusField = moduleBean.getField("status",ocrTemplateModule.getName());
        List<FacilioField> fields = Arrays.asList(statusField);

        ocrTemplateContext.setStatusEnum(status);
        V3RecordAPI.updateRecord(ocrTemplateContext , ocrTemplateModule, fields);

//        if(!skipClientMessage) {
//            long templateId = billTemplateContext.getId();
//            JSONObject json = new JSONObject();
//            json.put("status", status);
//            json.put("templateId",templateId);
//
//            sendMessageToClient(Topics.OcrParseBill.ocrTemplateBill +"/"+templateId+"/update", json);
//        }
    }
	
	public static void updateTemplateFiles( OCRTemplateContext ocrTemplateContext) throws Exception {
        
		ModuleBean moduleBean = Constants.getModBean();
        FacilioModule ocrTemplateModule = moduleBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE);

        FacilioField formFileField = moduleBean.getField("parsedForm",ocrTemplateModule.getName());
        FacilioField rawFileField = moduleBean.getField("parsedRawText", ocrTemplateModule.getName());
        List<FacilioField> fields = Arrays.asList(formFileField, rawFileField);

        V3RecordAPI.updateRecord(ocrTemplateContext , ocrTemplateModule, fields);

//        if(!skipClientMessage) {
//            long templateId = billTemplateContext.getId();
//            JSONObject json = new JSONObject();
//            json.put("status", status);
//            json.put("templateId",templateId);
//
//            sendMessageToClient(Topics.OcrParseBill.ocrTemplateBill +"/"+templateId+"/update", json);
//        }
    }

//    public static void updateBillTemplateRecord(int status, BillTemplateContext billTemplateContext, String moduleName) throws Exception {
//        updateBillTemplateRecord(status, billTemplateContext, moduleName, false);
//    }
//
//    public static void updateBillTemplateRecord(int status, BillTemplateContext billTemplateContext, String moduleName, boolean skipClientMessage) throws Exception{
//        ModuleBean moduleBean = Constants.getModBean();
//        FacilioModule billTemplateModule = moduleBean.getModule(moduleName);
//
//        FacilioField formFileField = moduleBean.getField("parsedForm",billTemplateModule.getName());
//        FacilioField rawFileField = moduleBean.getField("parsedRawText", billTemplateModule.getName());
//        FacilioField statusField = moduleBean.getField("status",billTemplateModule.getName());
//        List<FacilioField> fields = Arrays.asList(formFileField, rawFileField, statusField);
//
//        billTemplateContext.setStatus(status);
//        V3RecordAPI.updateRecord(billTemplateContext , billTemplateModule, fields);
//
//        if(!skipClientMessage) {
//            long templateId = billTemplateContext.getId();
//            JSONObject json = new JSONObject();
//            json.put("status", status);
//            json.put("templateId",templateId);
//
//            sendMessageToClient(Topics.OcrParseBill.ocrTemplateBill +"/"+templateId+"/update", json);
//        }
//    }

    public static void sendMessageToClient( String topic, JSONObject jsonMessage) throws Exception {
        long ouid = AccountUtil.getCurrentUser().getOuid();
        User user = AccountUtil.getUserBean().getUser(ouid,false);


        WebMessage message = new WebMessage();
        message.setTopic(topic);
        message.setContent(jsonMessage);
        message.setTo(ouid);
        message.setOrgId(user.getOrgId());
        message.setSessionType(LiveSession.LiveSessionType.APP);
        Broadcaster.getBroadcaster().sendMessage(message);
    }
    
    public static long addParsedFile(String fileName, Object content, String contentType) throws Exception {
        FileStore fs = FacilioFactory.getFileStore();

        Gson gson = new Gson();
        String jsonString = gson.toJson(content);

        return fs.addFile("default",fileName, jsonString, contentType);
    }

    public static void updateActualBillStatus(int status, long id) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        ActualBillContext billContext = new ActualBillContext();
        billContext.setStatus(status);
        billContext.setId(id);

        FacilioField field = moduleBean.getField("status", FacilioConstants.Ocr.ACTUAL_BILL);
        V3RecordAPI.updateRecord(billContext, moduleBean.getModule(FacilioConstants.Ocr.ACTUAL_BILL), Collections.singletonList(field), false, true);
    }

//    public static OcrFieldRuleLogContext getOcrRuleLogContext(BillTemplateContext billTemplate, long mapFieldId, ParsedBillContext parsedBill,OcrFieldRuleLogContext.OcrErrorOccuredAtEnum type, OcrFieldRuleLogContext.ParseStatusEnum status, Object value, TemplateFieldRuleContext fieldRule, LineItemFieldRuleContext subformFieldRules) throws Exception {
//        OcrFieldRuleLogContext logContext = new OcrFieldRuleLogContext();
//        logContext.setMapFieldId(mapFieldId);
//        logContext.setBillTemplate(billTemplate);
//        logContext.setParsedBill(parsedBill);
//        logContext.setActualBill(parsedBill.getActualBill());
//        logContext.setType(type.getVal());
//        logContext.setParseStatus(status.getVal());
//        logContext.setValue(value);
//
//
//        if(fieldRule != null) {
//            logContext.setParsingField(fieldRule.getTypeEnum().getParsingFieldRule(fieldRule));
//            logContext.setParsingType(fieldRule.getTypeEnum().getVal());
//        }
//
//        if(subformFieldRules != null){
//            logContext.setParsingField(subformFieldRules.getTypeEnum().getParsingLineItemFieldRule(subformFieldRules));
//            logContext.setParsingType(subformFieldRules.getTypeEnum().getVal());
//        }
//
//        return logContext;
//    }

    public static String getOcrErrorMessage(int code, String message, int fieldRuleType){
        JSONObject errorMessage = new JSONObject();
        errorMessage.put("code", code);
        errorMessage.put("message", message);
        errorMessage.put("fieldRuletype", fieldRuleType);

        return errorMessage.toJSONString();
    }

    public static List<OCRTemplateOutputModuleContext> getTemplateOututModules(Long templateId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_MODULE);
        

        SelectRecordsBuilder<OCRTemplateOutputModuleContext> builder = new SelectRecordsBuilder<OCRTemplateOutputModuleContext>()
                .moduleName(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_MODULE)
                .select(fields)
                .beanClass(OCRTemplateOutputModuleContext.class)
                .andCondition(CriteriaAPI.getCondition("TEMPLATE_ID", "template", String.valueOf(templateId), NumberOperators.EQUALS));

        List<OCRTemplateOutputModuleContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
        	for(OCRTemplateOutputModuleContext opModule : list) {
        		List<OCRTemplateOutputFieldContext> opFields = getTemplateOututFields(templateId, opModule.getId());
        		opModule.setOutputFields(opFields);
        	}
        }
        return list;
    }

    public static List<OCRTemplateOutputFieldContext> getTemplateOututFields(Long templateId, Long outputModuleId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD);

        SelectRecordsBuilder<OCRTemplateOutputFieldContext> builder = new SelectRecordsBuilder<OCRTemplateOutputFieldContext>()
                .moduleName(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD)
                .select(fields)
                .beanClass(OCRTemplateOutputFieldContext.class)
                .andCondition(CriteriaAPI.getCondition("TEMPLATE_ID", "template", String.valueOf(templateId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("OCR_OUTPUT_MODULES","ocrOutputModule",String.valueOf(outputModuleId),NumberOperators.EQUALS));

        List<OCRTemplateOutputFieldContext> list = builder.get();
        return list;
    }
    
    public static List<OCRTemplateVariableContext> getTemplateVariables(Long templateId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Ocr.OCR_TEMPLATE_VARIABLES);

        SelectRecordsBuilder<OCRTemplateVariableContext> builder = new SelectRecordsBuilder<OCRTemplateVariableContext>()
                .moduleName(FacilioConstants.Ocr.OCR_TEMPLATE_VARIABLES)
                .select(fields)
                .beanClass(OCRTemplateVariableContext.class)
                .andCondition(CriteriaAPI.getCondition("TEMPLATE_ID", "template", String.valueOf(templateId), NumberOperators.EQUALS));

        List<OCRTemplateVariableContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list)){
        	
        	for(OCRTemplateVariableContext variable : list) {
        		
        		if(variable.getWorkflowId() > 0) {
        			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(variable.getWorkflowId());
        			
        			variable.setWorkflow(workflowContext);
        		}
        	}
        }
        return list;
    }
    
    public static void deleteOutputFields(Long templateId,long outputModuleId, TemplateOutputFieldExtractTypeEnum fieldType) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Ocr.OCR_TEMPLATE_OUTPUT_FIELD);
        DeleteRecordBuilder<OCRTemplateOutputFieldContext> deleteBuilder = new DeleteRecordBuilder<OCRTemplateOutputFieldContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("TEMPLATE_ID", "template", String.valueOf(templateId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("OCR_OUTPUT_MODULES", "ocrOutputModule", String.valueOf(outputModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(fieldType.getVal()), NumberOperators.EQUALS))
                ;

        deleteBuilder.delete();
    }
}
