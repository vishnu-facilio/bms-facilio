package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateMLModelCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		JSONObject postObj = new JSONObject();
		postObj.put("mlID",mlContext.getId());
		postObj.put("orgID", mlContext.getOrgId());
		postObj.put("assetID", mlContext.getAssetContext().getAssetID());
		postObj.put("modelVariables",new ObjectMapper().writeValueAsString(mlContext.getData()));
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		JSONArray ip = new JSONArray();
		for(MLVariableContext variableContext : mlContext.getMLVariable())
		{
			FacilioField field =modBean.getField(variableContext.getFieldid());
			ip.put(field.getName());
		}
		postObj.put("inputMetrics",ip);
		
		JSONArray op = new JSONArray();
		FacilioModule module = modBean.getModule(mlContext.getPredictedLogModuleid());
		List<FacilioField> fields = module.getFields();
		for(FacilioField field:fields)
		{
			op.put(field.getName());
		}
		postObj.put("outputMetrics",op);
		postObj.put("data", constructJSONArray(mlContext.getMlVariablesDataMap()));
		
		String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/"+mlContext.getModelPath();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		mlContext.setResult(result);
		return false;
	}
	
	private JSONArray constructJSONArray(SortedMap<Long,Hashtable<String,Object>> map) throws JSONException
	{
		JSONArray array = new JSONArray();
		Set<Long> keySet = map.keySet();
		for(Long ttime:keySet)
		{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ttime", ttime);
			Hashtable<String,Object> dataMap = map.get(ttime);
			for(String key:dataMap.keySet())
			{
				jsonObject.put(key, dataMap.get(key));
			}
			
			array.put(jsonObject);
		}
		return array;
	}
	

}
