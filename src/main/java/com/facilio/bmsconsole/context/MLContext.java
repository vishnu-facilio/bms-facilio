package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.*;

import org.apache.struts2.json.annotations.JSON;

public class MLContext extends ModuleBaseWithCustomFields
{
	private static final long serialVersionUID = 1L;
	
	private Hashtable<Long,Map<String,Object>> assetDetails=new Hashtable<Long,Map<String,Object>>(10);
	private Map<Long,HashMap<String,String>> assetVariables;
	private List<MLModelVariableContext> mlModelVariables;
	private List<MLVariableContext> mlVariables;
	private List<MLVariableContext> criteriaVariables;
	
	private Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap; // AssetID => Attribute Name => ttime,Attribute Value
	private SortedMap<Long,Hashtable<String,Object>> mlCriteriaVariablesDataMap;
	
	private String modelPath;
	private long predictionLogModuleID;
	private long predictionModuleID;
	private long criteriaId;
	private long ruleID;
	private long predictionTime;
	private String sequence;
	
	private String result;
		
	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}
	public String getSequence()
	{
		return sequence;
	}
	
	@JSON(serialize=false)
	public void setPredictionTime(long predictionTime)
	{
		this.predictionTime = predictionTime;
	}
	@JSON(serialize=false)
	public long getPredictionTime()
	{
		return predictionTime;
	}
	
	@JSON(serialize=false)
	public void setResult(String result)
	{
		this.result = result;
	}
	@JSON(serialize=false)
	public String getResult()
	{
		return result;
	}

	
	public void setPredictionLogModuleID(long predictionLogModuleID)
	{
		this.predictionLogModuleID = predictionLogModuleID;
	}
	public long getPredictionLogModuleID()
	{
		return predictionLogModuleID;
	}
	
	public void setPredictionModuleID(long predictionModuleID)
	{
		this.predictionModuleID = predictionModuleID;
	}
	public long getPredictionModuleID()
	{
		return predictionModuleID;
	}
	
	public void setModelPath(String modelPath)
	{
		this.modelPath = modelPath;
	}
	public String getModelPath()
	{
		return modelPath;
	}
	
	@JSON(serialize=false)
	public void setAssetDetails(long assetID,Map<String,Object> data)
	{
		assetDetails.put(assetID, data);
	}
	
	@JSON(serialize=false)
	public void setAssetVariables(long assetID,MLAssetVariableContext assetVariableContext)
	{
		if(assetVariables==null)
		{
			assetVariables = new HashMap<Long,HashMap<String,String>>(10);
		}
		if(assetVariables.containsKey(assetID))
		{
			assetVariables.get(assetID).put(assetVariableContext.getVariableKey(), assetVariableContext.getVariableValue());
		}
		else
		{
			HashMap<String,String> variableValues = new HashMap<String,String>(5);
			variableValues.put(assetVariableContext.getVariableKey(), assetVariableContext.getVariableValue());
			assetVariables.put(assetID, variableValues);
		}
	}
	
	@JSON(serialize=false)
	public Map<Long,HashMap<String,String>> getAssetVariables()
	{
		return assetVariables;
	}
	
	@JSON(serialize=false)
	public Hashtable<Long,Map<String,Object>> getAssetDetails()
	{
		return assetDetails;
	}
	
	@JSON(serialize=false)
	public void addMLVariable(MLVariableContext context)
	{
		if(mlVariables==null)
		{
			mlVariables = new ArrayList<MLVariableContext>(10);
		}
		mlVariables.add(context);
	}
	
	@JSON(serialize=false)
	public List<MLVariableContext> getMLVariable()
	{
		return mlVariables;
	}
	
	@JSON(serialize=false)
	public void addMLModelVariable(MLModelVariableContext context)
	{
		if(mlModelVariables==null)
		{
			mlModelVariables = new ArrayList<MLModelVariableContext>(10);
		}
		mlModelVariables.add(context);
	}
	
	@JSON(serialize=false)
	public List<MLModelVariableContext> getMLModelVariable()
	{
		return mlModelVariables;
	}
	
	@JSON(serialize=false)
	public String getMLModelVariable(String key)
	{
		if(mlModelVariables.size()>0)
		{
			for(MLModelVariableContext context:mlModelVariables)
			{
				if(context.getVariableKey().equals(key))
				{
					return context.getVariableValue();
				}
			}
		}
		return null;
	}
	
	@JSON(serialize=false)
	public void addMLCriteriaVariable(MLVariableContext context)
	{
		if(criteriaVariables==null)
		{
			criteriaVariables = new ArrayList<MLVariableContext>(10);
		}
		criteriaVariables.add(context);
	}
	
	@JSON(serialize=false)
	public void setMlVariablesDataMap(long assetID,String attributeName ,SortedMap<Long,Object> data)
	{
		if(mlVariablesDataMap==null)
		{
			mlVariablesDataMap = new Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>>(10);
		}
		if(mlVariablesDataMap.containsKey(assetID))
		{
			mlVariablesDataMap.get(assetID).put(attributeName, data);
		}
		else
		{
			Hashtable<String, SortedMap<Long, Object>> assetData = new Hashtable<String,SortedMap<Long,Object>>(5);
			assetData.put(attributeName, data);
			mlVariablesDataMap.put(assetID, assetData);
		}
	}
	
	@JSON(serialize=false)
	public Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> getMlVariablesDataMap()
	{
		return mlVariablesDataMap;
	}
	
	@JSON(serialize=false)
	public void setMlCriteriaVariablesDataMap(SortedMap<Long,Hashtable<String,Object>> dataMap)
	{
		this.mlCriteriaVariablesDataMap = dataMap;
	}
	@JSON(serialize=false)
	public SortedMap<Long,Hashtable<String,Object>> getMlCriteriaVariablesDataMap()
	{
		return mlCriteriaVariablesDataMap;
	}
	
	@JSON(serialize=false)
	public List<MLVariableContext> getMLCriteriaVariables()
	{
		return criteriaVariables;
	}
	
	public long getCriteriaID() {
		return criteriaId;
	}

	public void setCriteriaID(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public long getRuleID() {
		return ruleID;
	}
	public void setRuleID(long ruleID) {
		this.ruleID = ruleID;
	}
	
	@JSON(serialize=false)
	public void setMlVariablesDataMap(Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> criteriaSatisfiedDataMap) 
	{
		this.mlVariablesDataMap = criteriaSatisfiedDataMap;
	}
	
	@JSON(serialize=false)
	public long getSourceID()
	{
		if(mlVariables!=null)
		{
			for(MLVariableContext context: mlVariables)
			{
				if(context.getIsSource())
				{
					return context.getParentID();
				}
			}
		}
		return -1;
	}
	
	@JSON(serialize=false)
    public MLVariableContext getMLVariableContext(long assetID)
    {
		for(MLVariableContext context: mlVariables)
		{
			if(context.getParentID()== assetID)
			{
				return context;
			}
        }
		return null;
    }
}
