package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class ConstructReadingForMLModelCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ConstructReadingForMLModelCommand.class.getName());

	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		try {
			
			MLServiceContext mlJobContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
			
			Long assetId = (Long) mlJobContext.getAssetDetails().get(FacilioConstants.ContextNames.ASSET_ID);
			Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TTIME);
			Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TTIME);
			
			List<String> readingVariable = mlJobContext.getReadingVariables();
			LOGGER.info("ML MODEL assetId :: "+assetId);
			LOGGER.info("ML MODEL readingVariable :: "+readingVariable);

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
			FacilioModule fieldsModule = ModuleFactory.getFieldsModule();

			List<FacilioField> fields = FieldFactory.getMinimalFieldsFields();
			fields.addAll(FieldFactory.getReadingDataMetaFields());

			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			List<FacilioField> selectFields = new ArrayList<>(5);
			selectFields.add(fieldMap.get("fieldId"));
			selectFields.add(fieldMap.get("name"));
			selectFields.add(fieldMap.get("moduleId"));
			selectFields.add(fieldMap.get("resourceId"));
			selectFields.add(fieldMap.get("value"));

			GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
					.table(readingDataMeta.getTableName())
					.select(selectFields)
					.innerJoin(fieldsModule.getTableName())
					.on(fieldsModule.getTableName() +".FIELDID = "+ readingDataMeta.getTableName()+".FIELD_ID")
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), assetId +"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(readingVariable, ","), StringOperators.IS));

			LOGGER.info("ML MODEL no of variables :: "+genericSelectBuilder.get().size());

//			String namespace = "machineLearning";
//			String function = "printFunc";
//			
//			WorkflowContext workflowContext = UserFunctionAPI.getWorkflowFunction(namespace, function);
//			
//			LOGGER.info("machinelearning-printFunc id ::"+workflowContext.getId());
//			LOGGER.info("machinelearning-printrow id ::"+UserFunctionAPI.getWorkflowFunction(namespace, "printrow").getId());
			JSONArray dataObject = new JSONArray();
			
			
			for(Map<String, Object> map : genericSelectBuilder.get()) {
				LOGGER.info("before script row ::"+map);
				
//				Map<String, Object> sample = new HashMap<>();
//				sample.put("rowsss", map);
//				sample.put("row", "seeni");
//				Object resMap = WorkflowUtil.getResult(workflowContext.getId(), sample);
//				LOGGER.info("after script row ::"+resMap);
				
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
			
			LOGGER.info("DATAAAAAAA : "+dataObject);
			mlJobContext.setDataObject(dataObject);
			mlJobContext.setOrgDetails(getOrgInfo());
			LOGGER.info("end of GetReadingsForMLModelCommand");
			return false;
		}
		catch(Exception e) {
			LOGGER.error("Error while adding Energy Prediction Job", e);
			throw e;
		}

	}

	private Map<String, Object> getOrgInfo() {
		Map<String, Object> orgInfo = new TreeMap<>();
		Organization org = AccountUtil.getCurrentOrg();
		orgInfo.put(FacilioConstants.ContextNames.ORGID, org.getOrgId());
		orgInfo.put(FacilioConstants.ContextNames.TIME_ZONE, org.getTimezone());
		return orgInfo;
	}
}
