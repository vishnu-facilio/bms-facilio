package com.facilio.bmsconsole.context;

import java.util.List;

import org.json.JSONArray;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;

public class MlForecastingContext extends ModuleBaseWithCustomFields 
{
	private static final long serialVersionUID = 1L;
	
	private  long assetid;
	private long criteriaId;
	private long sourceModuleId;
	private long predictedModuleId;
	private int predictionInterval;
	private long lastExecutionTime;
	private int dataInterval;
	private int modelSamplingInterval;

	public long getAssetid() 
	{
		return assetid;
	}

	public void setAssetid(long assetid) 
	{
		this.assetid = assetid;
	}
	
	
	public long getCriteriaid() 
	{
		return criteriaId;
	}

	public void setCriteriaid(long criteriaid) 
	{
		this.criteriaId = criteriaid;
	}
	
	public long getSourcemoduleid() 
	{
		return sourceModuleId;
	}

	public void setSourcemoduleid(long sourcemoduleid)
	{
		this.sourceModuleId = sourcemoduleid;
	}

	public long getPredictedfieldid()
	{
		return predictedModuleId;
	}

	public void setPredictedfieldid(long predictedmoduleid) 
	{
		this.predictedModuleId = predictedmoduleid;
	}

	public int getPredictioninterval() 
	{
		return predictionInterval;
	}

	public void setPredictioninterval(int predictioninterval) 
	{
		this.predictionInterval = predictioninterval;
	}

	public long getLastExecutionTime() 
	{
		return lastExecutionTime;
	}

	public void setLastExecutionTime(long lastexecutiontime) 
	{
		this.lastExecutionTime = lastexecutiontime;
	}

	public int getModelsamplinginterval() 
	{
		return modelSamplingInterval;
	}

	public void setModelsamplinginterval(int modelsamplinginterval) 
	{
		this.modelSamplingInterval = modelsamplinginterval;
	}

	
	
	private List<FacilioField> fields;
	public void setFields(List<FacilioField> fields)
	{
		this.fields = fields;
	}
	public List<FacilioField> getFields()
	{
		return fields;
	}
	
	private SelectRecordsBuilder<ReadingContext> reading;
	public void setFieldsData(SelectRecordsBuilder<ReadingContext> reading)
	{
		this.reading = reading;
	}
	
	public SelectRecordsBuilder<ReadingContext> getReading()
	{
		return reading;
	}
	
	private JSONArray pyData;
	public void setPyData(JSONArray pyData)
	{
		this.pyData = pyData;
	}
	public JSONArray getPyData()
	{
		return pyData;
	}
	
	private String result;
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}

	public int getDatainterval() {
		return dataInterval;
	}

	public void setDatainterval(int dataInterval) {
		this.dataInterval = dataInterval;
	}
	
}
