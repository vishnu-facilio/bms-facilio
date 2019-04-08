package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class MLContext extends ModuleBaseWithCustomFields
{
	private static final long serialVersionUID = 1L;
	
	private MLAssetContext assetContext;
	private Map<Long,HashMap<String,String>> assetVariables;
	private Map<String,String> modelVariables;
	private List<MLVariableContext> mlVariables;
	private List<MLVariableContext> criteriaVariables;
	
	private SortedMap<Long,Hashtable<String,Object>> mlVariablesDataMap;
	private SortedMap<Long,Hashtable<String,Object>> mlCriteriaVariablesDataMap;
	private String modelPath;
	private long minSamplingPeriod;
	private long maxSamplingPeriod;
	private long predictedLogModuleid;
	private long predictedModuleid;
	private long criteriaId;
	private long ruleId;
	private long predictionTime;
	
	private String result;
	
	public void setPredictionTime(long predictionTime)
	{
		this.predictionTime = predictionTime;
	}
	public long getPredictionTime()
	{
		return predictionTime;
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}

	
	public void setPredictedLogModuleid(long predictedLogModuleid)
	{
		this.predictedLogModuleid = predictedLogModuleid;
	}
	public long getPredictedLogModuleid()
	{
		return predictedLogModuleid;
	}
	
	public void setPredictedModuleid(long predictedModuleid)
	{
		this.predictedModuleid = predictedModuleid;
	}
	public long getPredictedModuleid()
	{
		return predictedModuleid;
	}
	
	public String getModelPath()
	{
		return modelPath;
	}
	public void setAssetContext(MLAssetContext context) 
	{
		this.assetContext = context;
	}
	
	public MLAssetContext getAssetContext()
	{
		return assetContext;
	}
	
	public void addMLVariable(MLVariableContext context)
	{
		if(mlVariables==null)
		{
			mlVariables = new ArrayList<MLVariableContext>(10);
		}
		mlVariables.add(context);
	}
	
	public List<MLVariableContext> getMLVariable()
	{
		return mlVariables;
	}
	
	public void addMLCriteriaVariable(MLVariableContext context)
	{
		if(criteriaVariables==null)
		{
			criteriaVariables = new ArrayList<MLVariableContext>(10);
		}
		criteriaVariables.add(context);
	}
	
	public void setMlVariablesDataMap(SortedMap<Long,Hashtable<String,Object>> dataMap)
	{
		this.mlVariablesDataMap = dataMap;
	}
	public SortedMap<Long,Hashtable<String,Object>> getMlVariablesDataMap()
	{
		return mlVariablesDataMap;
	}
	
	public void setMlCriteriaVariablesDataMap(SortedMap<Long,Hashtable<String,Object>> dataMap)
	{
		this.mlCriteriaVariablesDataMap = dataMap;
	}
	public SortedMap<Long,Hashtable<String,Object>> getMlCriteriaVariablesDataMap()
	{
		return mlCriteriaVariablesDataMap;
	}
	
	public List<MLVariableContext> getMLCriteriaVariables()
	{
		return criteriaVariables;
	}
	
	public void setMaxSamplingPeriod(long maxSamplingPeriod)
	{
		this.maxSamplingPeriod = maxSamplingPeriod;
	}
	
	public long getMaxSamplingPeriod()
	{
		return maxSamplingPeriod;
	}

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
}
