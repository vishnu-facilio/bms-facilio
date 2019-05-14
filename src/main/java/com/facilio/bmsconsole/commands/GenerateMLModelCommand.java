package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenerateMLModelCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(GenerateMLModelCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		JSONObject postObj = new JSONObject();
		postObj.put("ml_id",mlContext.getId());
		postObj.put("orgid", mlContext.getOrgId());
		
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
		LOGGER.info("DATA OBJECT FOR ML IS "+postObj.get("data"));
		
		String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/"+mlContext.getModelPath();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		LOGGER.info("[JAVEED =>To be Removed] Sending request to ML Server "+postURL+"::"+mlContext.getId());
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		mlContext.setResult(result);
		return false;
	}
	
	private JSONArray constructJSONArray(Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> assetDataMap) throws JSONException
	{
		JSONArray dataObject = new JSONArray();
		Set<Long> assetSet = assetDataMap.keySet();
		for(long assetID: assetSet)
		{
			JSONArray atrributeArray = new JSONArray();
			Hashtable<String,SortedMap<Long,Object>> attributeData = assetDataMap.get(assetID);
			Set<String> attributeNameSet = attributeData.keySet();
			for(String attributeName:attributeNameSet)
			{
				SortedMap<Long,Object> attributeDataMap = attributeData.get(attributeName);
				Set<Long> timeSet = attributeDataMap.keySet();
				for(long time: timeSet)
				{
					JSONObject object = new JSONObject();
					object.put("ttime", time);
					object.put(attributeName, attributeDataMap.get(time));
					object.put("assetID", assetID);
					
					atrributeArray.put(object);
				}
			}
			dataObject.put(atrributeArray);
		}
		return dataObject;
		
	}
	

}
