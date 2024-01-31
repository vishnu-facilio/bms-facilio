package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.ocr.*;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.ocr.aws.TextractContext;
import com.facilio.ocr.aws.TextractContext.FormContext;
import com.facilio.ocr.aws.TextractUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class OCRConstructOutputModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		OCRTemplateContext ocrTemplate = (OCRTemplateContext) context.get(FacilioConstants.Ocr.OCR_TEMPLATE);
		Long billMonth = (Long) context.get(FacilioConstants.Ocr.BILLING_YEAR);
		
		Map<Long, Object> variableOpMap = executeVariablesAndGetResult(ocrTemplate);
		
		Map<String, Object> parsedResult = (Map<String, Object>) context.get(FacilioConstants.Ocr.TEXTRACT_RESULT);
		
		Map<Integer, Map<String, String>> keyValueSearchMap = TextractUtil.getKeyValueSearchMap((List<FormContext>) parsedResult.get("forms"));
		parsedResult.put("formsSearchMap", keyValueSearchMap);
		
		Map<String, Map<Integer, Map<Integer, String>>> tableSearchMap = TextractUtil.geTableValueSearchMap((List<TextractContext.TableContext>) parsedResult.get("tables"));
		parsedResult.put("tablesSearchMap", tableSearchMap);
		
		parsedResult.put("variables", variableOpMap);
		
		populateRecords(ocrTemplate,parsedResult, billMonth);
		
		return false;
	}

	private Map<Long, Object> executeVariablesAndGetResult(OCRTemplateContext ocrTemplate) throws Exception {
		
		Map<Long, Object> opMap = new HashMap<Long, Object>();
		
		if(ocrTemplate.getVariables() != null) {
			
			for(OCRTemplateVariableContext variable : ocrTemplate.getVariables()) {
				
				Object result = variable.getTypeEnum().execute(variable);
				opMap.put(variable.getId(), result);
			}
		}
		
		return opMap;
		
	}

	private void populateRecords(OCRTemplateContext ocrTemplate,Map<String, Object> parsedResult, Long billMonth) throws Exception {
		
		
		List<OCRTemplateOutputModuleContext> opModules = OcrUtil.getTemplateOututModules(ocrTemplate.getId());
		
		Map<Long,List<OCRTemplateOutputModuleContext>> opModulesParentMap = new HashMap<Long, List<OCRTemplateOutputModuleContext>>();
		
		Map<Long,OCRTemplateOutputModuleContext> opModulesMap = new HashMap<Long, OCRTemplateOutputModuleContext>();
		
		for(OCRTemplateOutputModuleContext opModule: opModules) {
			
			Long parentRecordId = opModule.getParentRecordId();
			
			List<OCRTemplateOutputModuleContext> localList = opModulesParentMap.getOrDefault(parentRecordId, new ArrayList<OCRTemplateOutputModuleContext>());
			localList.add(opModule);
			
			opModulesParentMap.put(parentRecordId, localList);
			opModulesMap.put(opModule.getId(), opModule);
			
		}
			
		
		List<OCRTemplateOutputModuleContext> rootModules = opModulesParentMap.get(null);
		
		if(rootModules == null) {
			throw new IllegalArgumentException("rootModule should Not be empty");
		}
		
		Map<Long,Long> opModuleVsRecordIdMap = new HashMap<Long, Long>();
		
		for (int i = 0; i < rootModules.size(); i++) {
			
			OCRTemplateOutputModuleContext opModule = rootModules.get(i);
			
			Long parentRecordResultId = null, parentModuleId = null;
			
			if(opModule.getParentRecordId() != null && opModule.getParentRecordId() > 0) {
				
				parentRecordResultId = opModuleVsRecordIdMap.get(opModule.getParentRecordId());
				
				parentModuleId = opModulesMap.get(opModule.getParentRecordId()).getOutputModuleId();
				
			}
			
			Long currentRecordId = constructAndAddModuleRecord(ocrTemplate,opModule,parsedResult,parentRecordResultId,parentModuleId, billMonth);
			
			opModuleVsRecordIdMap.put(opModule.getId(), currentRecordId);
			
			List<OCRTemplateOutputModuleContext> childModules = opModulesParentMap.get(opModule.getId());
			
			if(childModules != null) {
				rootModules.addAll(childModules);
			}
		}
			
	}

	private Long constructAndAddModuleRecord(OCRTemplateContext ocrTemplate, OCRTemplateOutputModuleContext opModule,Map<String, Object> parsedResult,Long parentRecordResultId,Long parentModuleId, Long billMonth) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = Constants.getModBean();
		
		FacilioModule module = modBean.getModule(opModule.getOutputModuleId());
		
		Map<String,Object> recordMap = new HashMap<String, Object>();
		
		for(OCRTemplateOutputFieldContext opField : opModule.getOutputFields()) {
			
			Object obj = opField.getTypeEnum().execute(opModule,opField,parsedResult);
			
			recordMap.put(modBean.getField(opField.getOutputFieldId()).getName(), obj);
		}
		
		addParentField(recordMap,module,parentRecordResultId,parentModuleId);

		recordMap.put("status", ParsedBillContext.ParsedBillStatus.PENDING.getIndex());
		recordMap.put("billMonth", billMonth);
		recordMap.put("billUid", module.getName() + "_" + parentRecordResultId);
		recordMap.put("recordType", opModule.getRecordType().intValue());

		FacilioContext context = V3Util.createRecord(module, recordMap);
		
		Long recordId = Constants.getRecordList(context).get(0).getId();
		
		return recordId;
	}

	private void addParentField(Map<String, Object> recordMap, FacilioModule module, Long parentRecordResultId,Long parentModuleId) throws Exception {
		
		ModuleBean modBean = Constants.getModBean();
		if(parentRecordResultId != null && parentModuleId != null) {
			
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			
			for(FacilioField field : fields) {
				
				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
					
					LookupField lookupField = (LookupField) field;
					
					if(parentModuleId.equals(lookupField.getLookupModuleId())) {
						
						recordMap.put(lookupField.getName(), parentRecordResultId);
						break;
					}
				}
			}
		}
		
	}
	
	
	

}
