package com.facilio.bmsconsole.commands;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class GenerateMLModelCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GenerateMLModelCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		JSONObject postObj = new JSONObject();
		postObj.put("ml_id",mlContext.getId());
		postObj.put("orgid", mlContext.getOrgId());
		postObj.put("date", getCurrentDate(mlContext));
		postObj.put("predictedtime", mlContext.getPredictionTime());
		
		JSONObject modelVariables = new JSONObject();
		if(mlContext.getMLModelVariable()!=null)
		{
			for(MLModelVariableContext modelVariableContext : mlContext.getMLModelVariable())
			{
				modelVariables.put(modelVariableContext.getVariableKey(), modelVariableContext.getVariableValue());
			}
		}
		postObj.put("modelvariables",modelVariables);
		
		JSONArray assetVariables = new JSONArray();
		
		if(mlContext.getAssetVariables()!=null)
		{
			Set<Long> assetIDList = mlContext.getAssetVariables().keySet();
			for(long assetID:assetIDList)
			{
				JSONObject data = new JSONObject();
				HashMap<String,String> assetVariablesMap= mlContext.getAssetVariables().get(assetID);
				Set<String> keySet = assetVariablesMap.keySet();
				JSONObject variableMap = new JSONObject();
				for(String key:keySet)
				{
					variableMap.put(key, assetVariablesMap.get(key));
				}
				data.put(""+assetID, variableMap);
				assetVariables.put(data);
			}
		}
		postObj.put("assetdetails", assetVariables);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		JSONArray ip = new JSONArray();
		for(MLVariableContext variableContext : mlContext.getMLVariable())
		{
			FacilioField field =modBean.getField(variableContext.getFieldID());
			ip.put(field.getName());
		}
		postObj.put("inputmetrics",ip);
		
		JSONArray op = new JSONArray();
		FacilioModule module = modBean.getModule(mlContext.getPredictionLogModuleID());
		if(module!=null)
		{
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(FacilioField field:fields)
			{
				op.put(field.getName());
			}
		}
		postObj.put("outputmetrics",op);
		postObj.put("data", constructJSONArray(mlContext.getMlVariablesDataMap()));
		
//		String postURL= FacilioProperties.getAnomalyPredictAPIURL() + "/"+mlContext.getModelPath();
		String postURL= FacilioProperties.getAnomalyPredictAPIURL() + "/"+"predictEnergyWithRQ";
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		LOGGER.info(" Sending request to ML Server "+postURL+"::"+mlContext.getId());
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
        result = "{\"ml_id\": 231, \"orgid\": 2,\"data\": [{\"ttime\": 1574793000000, \"errorCode\": 1}, {\"ttime\": 1574796600000, \"errorCode\": 1}, {\"ttime\": 1574800200000, \"errorCode\": 1}, {\"ttime\": 1574803800000, \"errorCode\": 1}, {\"ttime\": 1574807400000, \"errorCode\": 1}, {\"ttime\": 1574811000000, \"errorCode\": 1}, {\"ttime\": 1574814600000, \"errorCode\": 1}, {\"ttime\": 1574818200000, \"errorCode\": 1}, {\"ttime\": 1574821800000, \"errorCode\": 1}, {\"ttime\": 1574825400000, \"errorCode\": 1}, {\"ttime\": 1574829000000, \"errorCode\": 1}, {\"ttime\": 1574832600000, \"errorCode\": 1}, {\"ttime\": 1574836200000, \"errorCode\": 1}, {\"ttime\": 1574839800000, \"errorCode\": 1}, {\"ttime\": 1574843400000, \"errorCode\": 1}, {\"ttime\": 1574847000000, \"errorCode\": 1}, {\"ttime\": 1574850600000, \"errorCode\": 1}, {\"ttime\": 1574854200000, \"errorCode\": 1}, {\"ttime\": 1574857800000, \"errorCode\": 1}, {\"ttime\": 1574861400000, \"errorCode\": 1}, {\"ttime\": 1574865000000, \"errorCode\": 1}, {\"ttime\": 1574868600000, \"errorCode\": 1}, {\"ttime\": 1574872200000, \"errorCode\": 1}, {\"ttime\": 1574875800000, \"errorCode\": 1}, {\"ttime\": 1574879400000, \"errorCode\": 1}, {\"ttime\": 1574883000000, \"errorCode\": 1}, {\"ttime\": 1574886600000, \"errorCode\": 1}, {\"ttime\": 1574890200000, \"errorCode\": 1}, {\"ttime\": 1574893800000, \"errorCode\": 1}, {\"ttime\": 1574897400000, \"errorCode\": 1}, {\"ttime\": 1574901000000, \"errorCode\": 1}, {\"ttime\": 1574904600000, \"errorCode\": 1}, {\"ttime\": 1574908200000, \"errorCode\": 1}, {\"ttime\": 1574911800000, \"errorCode\": 1}, {\"ttime\": 1574915400000, \"errorCode\": 1}, {\"ttime\": 1574919000000, \"errorCode\": 1}, {\"ttime\": 1574922600000, \"errorCode\": 1}, {\"ttime\": 1574926200000, \"errorCode\": 1}, {\"ttime\": 1574929800000, \"errorCode\": 1}, {\"ttime\": 1574933400000, \"errorCode\": 1}, {\"ttime\": 1574937000000, \"errorCode\": 1}, {\"ttime\": 1574940600000, \"errorCode\": 1}, {\"ttime\": 1574944200000, \"errorCode\": 1}, {\"ttime\": 1574947800000, \"errorCode\": 1}, {\"ttime\": 1574951400000, \"errorCode\": 1}, {\"ttime\": 1574955000000, \"errorCode\": 1}, {\"ttime\": 1574958600000, \"errorCode\": 1}, {\"ttime\": 1574962200000, \"errorCode\": 1}, {\"ttime\": 1574965800000, \"errorCode\": 1}, {\"ttime\": 1574969400000, \"errorCode\": 1}, {\"ttime\": 1574973000000, \"errorCode\": 1}, {\"ttime\": 1574976600000, \"errorCode\": 1}, {\"ttime\": 1574980200000, \"errorCode\": 1}, {\"ttime\": 1574983800000, \"errorCode\": 1}, {\"ttime\": 1574987400000, \"errorCode\": 1}, {\"ttime\": 1574991000000, \"errorCode\": 1}, {\"ttime\": 1574994600000, \"errorCode\": 1}, {\"ttime\": 1574998200000, \"errorCode\": 1}, {\"ttime\": 1575001800000, \"errorCode\": 1}, {\"ttime\": 1575005400000, \"errorCode\": 1}, {\"ttime\": 1575009000000, \"errorCode\": 1}, {\"ttime\": 1575012600000, \"errorCode\": 1}, {\"ttime\": 1575016200000, \"errorCode\": 1}, {\"ttime\": 1575019800000, \"errorCode\": 1}, {\"ttime\": 1575023400000, \"errorCode\": 1}, {\"ttime\": 1575027000000, \"errorCode\": 1}, {\"ttime\": 1575030600000, \"errorCode\": 1}, {\"ttime\": 1575034200000, \"errorCode\": 1}, {\"ttime\": 1575037800000, \"errorCode\": 1}, {\"ttime\": 1575041400000, \"errorCode\": 1}, {\"ttime\": 1575045000000, \"errorCode\": 1}, {\"ttime\": 1575048600000, \"errorCode\": 1}, {\"ttime\": 1575052200000, \"errorCode\": 1}, {\"ttime\": 1575055800000, \"errorCode\": 1}, {\"ttime\": 1575059400000, \"errorCode\": 1}, {\"ttime\": 1575063000000, \"errorCode\": 1}, {\"ttime\": 1575066600000, \"errorCode\": 1}, {\"ttime\": 1575070200000, \"errorCode\": 1}, {\"ttime\": 1575073800000, \"errorCode\": 1}, {\"ttime\": 1575077400000, \"errorCode\": 1}, {\"ttime\": 1575081000000, \"errorCode\": 1}, {\"ttime\": 1575084600000, \"errorCode\": 1}, {\"ttime\": 1575088200000, \"errorCode\": 1}, {\"ttime\": 1575091800000, \"errorCode\": 1}, {\"ttime\": 1575095400000, \"errorCode\": 1}, {\"ttime\": 1575099000000, \"errorCode\": 1}, {\"ttime\": 1575102600000, \"errorCode\": 1}, {\"ttime\": 1575106200000, \"errorCode\": 1}, {\"ttime\": 1575109800000, \"errorCode\": 1}, {\"ttime\": 1575113400000, \"errorCode\": 1}, {\"ttime\": 1575117000000, \"errorCode\": 1}, {\"ttime\": 1575120600000, \"errorCode\": 1}, {\"ttime\": 1575124200000, \"errorCode\": 1}, {\"ttime\": 1575127800000, \"errorCode\": 1}, {\"ttime\": 1575131400000, \"errorCode\": 1}, {\"ttime\": 1575135000000, \"errorCode\": 1}]}";
		mlContext.setResult(result);
		return false;
 	}
	
	private LocalDate getCurrentDate(MLContext mlContext) {
		if(mlContext.isHistoric()) {
			return Instant.ofEpochMilli(mlContext.getExecutionEndTime()).atZone(TimeZone.getTimeZone(AccountUtil.getCurrentAccount().getTimeZone()).toZoneId()).toLocalDate();
		}
		else {
			return LocalDate.now(TimeZone.getTimeZone(AccountUtil.getCurrentAccount().getTimeZone()).toZoneId());
		}
	}

	private JSONArray constructJSONArray(Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> assetDataMap) throws JSONException
	{
		JSONArray dataObject = new JSONArray();
		Set<Long> assetSet = assetDataMap.keySet();
		for(long assetID: assetSet)
		{
			//JSONArray assetArray = new JSONArray();
			Hashtable<String,SortedMap<Long,Object>> attributeData = assetDataMap.get(assetID);
			Set<String> attributeNameSet = attributeData.keySet();
			for(String attributeName:attributeNameSet)
			{
				JSONArray attributeArray = new JSONArray();
				SortedMap<Long,Object> attributeDataMap = attributeData.get(attributeName);
				Set<Long> timeSet = attributeDataMap.keySet();
				for(long time: timeSet)
				{
					JSONObject object = new JSONObject();
					object.put("ttime", time);
					if(attributeDataMap.get(time)==null)
					{
						object.put(attributeName, JSONObject.NULL);
					}
					else
					{
						object.put(attributeName, attributeDataMap.get(time));
					}
					object.put("assetID", assetID);
					
					attributeArray.put(object);
				}
				dataObject.put(attributeArray);
			}
			//dataObject.put(assetArray);
		}
		return dataObject;
		
	}
	

}
