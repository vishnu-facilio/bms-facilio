package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.UserFunctionAPI;

public class ConstructReadingForMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ConstructReadingForMLServiceCommand.class.getName());

	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		try {
			
			MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
			
			updateMLServiceInfo(mlServiceContext);
			
			Long assetId = (Long) mlServiceContext.getAssetDetails().get(FacilioConstants.ContextNames.ASSET_ID);
			Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TTIME);
			Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TTIME);
			
			List<String> readingVariable = mlServiceContext.getReadingVariables();
			LOGGER.info("ML Service assetId :: "+assetId);
			LOGGER.info("ML Service readingVariable :: "+readingVariable);

			List<Map<String, Object>> readingFieldsDetails = MLAPI.getReadingFields(assetId, readingVariable);

			LOGGER.info("ML MODEL no of variables :: "+readingFieldsDetails.size());

			JSONArray dataObject = new JSONArray();
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> map : readingFieldsDetails) {
				
	            long moduleId = (long)map.get("moduleId");
	            long fieldId = (long)map.get("fieldId");
	            long resourceId = (long)map.get("resourceId");
	            
	            FacilioModule module = modBean.getModule(moduleId);
	            FacilioField variableField = modBean.getField(fieldId);
	            FacilioField ttimeField = modBean.getField("ttime", module.getName());
				FacilioField parentField = modBean.getField("parentId", module.getName());
				
				List<FacilioField> variableFields = new ArrayList<FacilioField>(2);
				variableFields.add(variableField);
				variableFields.add(ttimeField);
				
				SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
						.select(variableFields)
						.module(module)
						.beanClass(ReadingContext.class)
						.orderBy("TTIME ASC")
						.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
						.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL))
						.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(resourceId), NumberOperators.EQUALS));

				
				List<Map<String, Object>> props = selectBuilder.getAsProps();
				SortedMap<Long,Object> data = new TreeMap<Long,Object>();
				for(Map<String,Object> prop : props) {
					
						long ttime = (long) prop.get(ttimeField.getName());
						
						if(data.containsKey(ttime) && variableField.getDataTypeEnum().equals(FieldType.DECIMAL)){
							Double previousValue = (Double) FacilioUtil.castOrParseValueAsPerType(variableField, data.get(ttime));
							Double currentValue = (Double) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName()));
							Double newValue = previousValue == null && currentValue == null ? null : previousValue == null ? currentValue : currentValue == null ? previousValue : previousValue + currentValue;
							data.put(ttime, newValue);
						}else if(data.containsKey(ttime) && variableField.getDataTypeEnum().equals(FieldType.NUMBER)){
							Long previousValue = (Long) FacilioUtil.castOrParseValueAsPerType(variableField, data.get(ttime));
							Long currentValue = (Long) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName()));
							Long newValue = previousValue == null && currentValue == null ? null : previousValue == null ? currentValue : currentValue == null ? previousValue : previousValue + currentValue;
							data.put(ttime, newValue);
						}else if(data.containsKey(ttime) && variableField.getDataTypeEnum().equals(FieldType.BOOLEAN)){
							Boolean previousValue = (Boolean) data.get(ttime);
							Boolean currentValue = (Boolean) prop.get(variableField.getName());
							Boolean newValue = previousValue == null && currentValue == null ? null : previousValue == null ? currentValue : currentValue == null ? previousValue : currentValue;
							data.put(ttime, newValue);
						}else{
							if(variableField.getDataTypeEnum().equals(FieldType.DECIMAL)){
								data.put(ttime,(Double) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName())));
							}else if(variableField.getDataTypeEnum().equals(FieldType.NUMBER)){
								data.put(ttime, (Long) FacilioUtil.castOrParseValueAsPerType(variableField, prop.get(variableField.getName())));
							}else{
								data.put(ttime, prop.get(variableField.getName()));
							}
						}
				}
				LOGGER.info("props:: " + props);
				
				String attributeName = variableField.getName();
				JSONArray attributeArray = new JSONArray();
				for(long time: data.keySet()) {
					JSONObject object = new JSONObject();
					object.put("ttime", time);
					if(data.get(time)==null) {
						object.put(attributeName, JSONObject.NULL);
					}
					else {
						object.put(attributeName, data.get(time));
					}
					object.put("assetID", resourceId);
					
					attributeArray.put(object);
				}
				
				dataObject.put(attributeArray);
			}
			
			mlServiceContext.setDataObject(dataObject);
			mlServiceContext.setOrgDetails(getOrgInfo());
			LOGGER.info("end of GetReadingsForMLModelCommand");
			return false;
		}
		catch(Exception e) {
			LOGGER.error("Error while adding Energy Prediction Job", e);
			throw e;
		}
	}

	private void updateMLServiceInfo(MLServiceContext mlServiceContext) throws Exception {
		Map<String, Object> mlServiceRow = new HashMap<>();
		mlServiceRow.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		mlServiceRow.put("status", "Initiated..");
		mlServiceRow.put("workflowId", getWorkFlowId(mlServiceContext.getWorkflowInfo()));
		mlServiceRow.put("modelName", mlServiceContext.getModelName());
		mlServiceRow.put("mlModelMeta", mlServiceContext.getReqJson().toString());
		
		long useCaseId = MLAPI.addMLServiceInfo(mlServiceRow);
		mlServiceContext.setUseCaseId(useCaseId);
	}

	private Object getWorkFlowId(Map<String, Object> workflowInfo) {
		String namespace = (String) workflowInfo.get("namespace");
		String function = (String) workflowInfo.get("function");
		try {
			WorkflowContext workflowContext = UserFunctionAPI.getWorkflowFunction(namespace, function);
			return workflowContext.getId();
		} catch (Exception e) {
			LOGGER.error("Error while getting flow id for given namespace <"+namespace+"> and function <"+function+">");
			e.printStackTrace();
		}
//		Map<String, Object> sample = new HashMap<>();
//		sample.put("rowsss", map);
//		sample.put("row", "seeni");
//		Object resMap = WorkflowUtil.getResult(workflowContext.getId(), sample);
//		LOGGER.info("after script row ::"+resMap);
		return null;
	}
	
	private Map<String, Object> getOrgInfo() {
		Map<String, Object> orgInfo = new TreeMap<>();
		Organization org = AccountUtil.getCurrentOrg();
		orgInfo.put(FacilioConstants.ContextNames.ORGID, org.getOrgId());
		orgInfo.put(FacilioConstants.ContextNames.TIME_ZONE, org.getTimezone());
		return orgInfo;
	}
}
