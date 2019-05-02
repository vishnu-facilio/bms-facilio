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
	
	private Hashtable<Long,Map<String,Object>> assetDetails=new Hashtable<Long,Map<String,Object>>(10);
	private Map<Long,HashMap<String,String>> assetVariables;
	private List<MLModelVariableContext> mlModelVariables;
	private List<MLVariableContext> mlVariables;
	private List<MLVariableContext> criteriaVariables;
	
	private Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap; // AssetID => Attribute Name => ttime,Attribute Value
	private SortedMap<Long,Hashtable<String,Object>> mlCriteriaVariablesDataMap;
	private List<Integer> sequenceList;
	private String modelPath;
	private long predictionLogModuleID;
	private long predictionModuleID;
	private long criteriaId;
	private long ruleID;
	private long predictionTime;
	private int sequence;
	
	private String result;
		
	public void addSequence(int sequence)
	{
		if(sequenceList==null)
		{
			sequenceList = new ArrayList<Integer>(10);
		}
		sequenceList.add(sequence);
	}
	public List<Integer> getSequenceList()
	{
		return sequenceList;
	}
	
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
	
	public String getModelPath()
	{
		return modelPath;
	}
	
	public void setAssetDetails(long assetID,Map<String,Object> data)
	{
		assetDetails.put(assetID, data);
	}
	
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
	public Map<Long,HashMap<String,String>> getAssetVariables()
	{
		return assetVariables;
	}
	public Hashtable<Long,Map<String,Object>> getAssetDetails()
	{
		return assetDetails;
	}
	
	
	public void addMLVariable(MLVariableContext context)
	{
		if(mlVariables==null)
		{
			mlVariables = new ArrayList<MLVariableContext>(10);
		}
		if(context.getSequence()>0)
		{
			sequenceList.add(context.getSequence());
		}
		mlVariables.add(context);
	}
	
	public List<MLVariableContext> getMLVariable()
	{
		return mlVariables;
	}
	
	public void addMLModelVariable(MLModelVariableContext context)
	{
		if(mlModelVariables==null)
		{
			mlModelVariables = new ArrayList<MLModelVariableContext>(10);
		}
		mlModelVariables.add(context);
	}
	
	public List<MLModelVariableContext> getMLModelVariable()
	{
		return mlModelVariables;
	}
	
	public void addMLCriteriaVariable(MLVariableContext context)
	{
		if(criteriaVariables==null)
		{
			criteriaVariables = new ArrayList<MLVariableContext>(10);
		}
		criteriaVariables.add(context);
	}
	
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
	public Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> getMlVariablesDataMap()
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
	
	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public long getRuleID() {
		return ruleID;
	}
	public void setRuleID(long ruleID) {
		this.ruleID = ruleID;
	}
	public void setMlVariablesDataMap(Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> criteriaSatisfiedDataMap) 
	{
		this.mlVariablesDataMap = criteriaSatisfiedDataMap;
	}
	
	public long getSourceID()
	{
		for(MLVariableContext context: mlVariables)
		{
			if(context.getIsSource())
			{
				return context.getParentID();
			}
		}
		return -1;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
}
