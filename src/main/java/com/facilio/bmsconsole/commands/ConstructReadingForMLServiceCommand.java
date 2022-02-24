package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;

public class ConstructReadingForMLServiceCommand extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(ConstructReadingForMLServiceCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3MLServiceContext mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);

		long startTime = mlServiceContext.getStartTime();
		long endTime = mlServiceContext.getEndTime();

		try {
			
			constructTrainingSamplingJson(mlServiceContext);

			LOGGER.info("MLService startTime = " + startTime+", endTime = "+endTime);
			LOGGER.info("ML dry hit data construction started for usecase id "+mlServiceContext.getId());

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<JSONArray> dataObjectList = new ArrayList<>();

			List<List<Map<String, Object>>> models = mlServiceContext.getModelReadings();
			LOGGER.info("No of models in given api : "+models.size());

			for(List<Map<String, Object>> eachModel : models) {

				JSONArray dataObject = new JSONArray();

				for(Map<String, Object> map : eachModel) {

					long fieldId = (long)map.get("fieldId");
					long resourceId = (long)map.get("parentId");

					FacilioField variableField = modBean.getField(fieldId);
					FacilioModule module = variableField.getModule();
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

					if(variableField.getName().equals("totalEnergyConsumptionDelta")) {
						FacilioField markedField = modBean.getField("marked", module.getName());
						List<Long> markedValue = new ArrayList<>();
						markedValue.add((long) 1);
						selectBuilder = selectBuilder.andCondition(CriteriaAPI.getCondition(markedField, markedValue, NumberOperators.NOT_EQUALS));
					}
					selectBuilder = selectBuilder.limit(10);
					//					LOGGER.info("Latest query :: "+selectBuilder.constructQueryString());

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
					//					LOGGER.info("props:: " + props);

					String attributeName = variableField.getName();
					JSONArray attributeArray = new JSONArray();
					for(long time: data.keySet()) {
						JSONObject object = new JSONObject();
						object.put("ttime", time);
						if(data.get(time)==null) {
							object.put(attributeName, null);
						} else {
							object.put(attributeName, data.get(time));
						}
						object.put("assetID", resourceId);

						attributeArray.add(object);
					}

					dataObject.add(attributeArray);
				}
				dataObjectList.add(dataObject);
			}
			mlServiceContext.setDataObjectList(dataObjectList);
			MLServiceUtil.updateMLStatus(mlServiceContext, "ML dry hit data construction done");
		}
		catch(Exception e) {
			e.printStackTrace();
			String errMsg = "Error while constructing data to dry hit ML api";
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.RESOURCE_NOT_FOUND, errMsg);
		}
		return false;
	}

	private void constructTrainingSamplingJson(V3MLServiceContext mlServiceContext) throws Exception {
		//	"mlVariables" : {"totalEnergyConsumptionDelta" : {"maxSamplingPeriod" : 777600000 }}}
		if(mlServiceContext.getTrainingSamplingJson() == null) {
			JSONObject samplingJson = new JSONObject();
			List<String> readingVariables = mlServiceContext.getReadingVariables();
			if(org.apache.commons.collections4.CollectionUtils.isEmpty(readingVariables)) {
				MLServiceUtil.extractAndValidateAssetFields(mlServiceContext);
			}
			for(String reading : mlServiceContext.getReadingVariables()) {
				JSONObject readingJson = new JSONObject();
				readingJson.put("maxSamplingPeriod", mlServiceContext.getTrainingSamplingPeriod());
				samplingJson.put(reading, readingJson);
			}
			mlServiceContext.setTrainingSamplingJson(samplingJson);
		}

	}

}
