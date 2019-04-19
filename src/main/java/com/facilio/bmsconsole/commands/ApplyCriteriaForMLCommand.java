package com.facilio.bmsconsole.commands;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.constants.FacilioConstants;

public class ApplyCriteriaForMLCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		if(mlContext.getMLCriteriaVariables()!=null && mlContext.getMLCriteriaVariables().size()>0)
		{
			Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> variableDataMap = mlContext.getMlVariablesDataMap();
			SortedMap<Long, Hashtable<String, Object>> criteriaVariableDataMap = mlContext.getMlCriteriaVariablesDataMap();
			
			Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> criteriaSatisfiedDataMap = new Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>>();
			
			Set<Long> keySet = criteriaVariableDataMap.keySet();
			Criteria criteria = CriteriaAPI.getCriteria(mlContext.getOrgId(), mlContext.getCriteriaId());
			
			long previousTime = -1;
			for(long ttime:keySet)
			{
				Map<String,Object> prop = criteriaVariableDataMap.get(ttime);
				if(applyCriteria(prop,criteria))
				{
					if(previousTime==-1)
					{
						Set<Long> assetList = variableDataMap.keySet();
						for(long assetID:assetList)
						{
							Hashtable<String,SortedMap<Long,Object>> attributeDataMap = variableDataMap.get(assetID);
							Set<String> attributeDataSet = attributeDataMap.keySet();
							for(String attributeName:attributeDataSet)
							{
								SortedMap<Long,Object> satisfiedData =  attributeDataMap.get(attributeName).headMap(ttime+1);
								if(!criteriaSatisfiedDataMap.containsKey(assetID))
								{
									criteriaSatisfiedDataMap.put(assetID, new Hashtable<String,SortedMap<Long,Object>>());
								}
								Hashtable<String,SortedMap<Long,Object>> dataset = criteriaSatisfiedDataMap.get(assetID);
								if(dataset.containsKey(attributeName))
								{
									dataset.get(attributeName).putAll(satisfiedData);
								}
								else
								{
									dataset.put(attributeName, satisfiedData);
								}
							}
						}
					}
					else
					{
						Set<Long> assetList = variableDataMap.keySet();
						for(long assetID:assetList)
						{
							Hashtable<String,SortedMap<Long,Object>> attributeDataMap = variableDataMap.get(assetID);
							Set<String> attributeDataSet = attributeDataMap.keySet();
							for(String attributeName:attributeDataSet)
							{
								SortedMap<Long,Object> satisfiedData =  attributeDataMap.get(attributeName).subMap(previousTime, ttime+1);
								if(!criteriaSatisfiedDataMap.containsKey(assetID))
								{
									criteriaSatisfiedDataMap.put(assetID, new Hashtable<String,SortedMap<Long,Object>>());
								}
								Hashtable<String,SortedMap<Long,Object>> dataset = criteriaSatisfiedDataMap.get(assetID);
								if(dataset.containsKey(attributeName))
								{
									dataset.get(attributeName).putAll(satisfiedData);
								}
								else
								{
									dataset.put(attributeName, satisfiedData);
								}
							}
						}
					}
				}
				previousTime = ttime;
			}
			mlContext.setMlVariablesDataMap(criteriaSatisfiedDataMap);
		}
		
		return false;
	}

	private boolean applyCriteria(Map<String,Object> prop,Criteria criteria)
	{
		boolean result = false;
		Map<String,Condition> conditions = criteria.getConditions();
        for(Condition condition: conditions.values())
        {
        	if(prop.containsKey(condition.getFieldName()))
        	{
        		Operator<?> operator = condition.getOperator();
        		if(operator instanceof BooleanOperators)
        		{
        			result = condition.computePredicate().evaluate(prop);
        		}
            }
        }
        return result;
	}
}
