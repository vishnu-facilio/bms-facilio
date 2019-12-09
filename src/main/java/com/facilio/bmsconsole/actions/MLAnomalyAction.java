package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateRange;

public class MLAnomalyAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private MLContext mlContext;
	
	public MLContext getMlContext() {
		return mlContext;
	}

	public void setMlContext(MLContext mlContext) {
		this.mlContext = mlContext;
	}

	private long mlAnomalyAlarmId ;

	public long getMlAnomalyAlarmId() {
		return mlAnomalyAlarmId;
	}

	public void setMlAnomalyAlarmId(long mlAnomalyAlarmId) {
		this.mlAnomalyAlarmId = mlAnomalyAlarmId;
	}
	
	public String fetchSubMeterHierarchy() throws Exception {
		MLAPI.getSubMeterDetails(mlAnomalyAlarmId);
		return SUCCESS;
	}
	
	public String fetchRcaAnomaly() throws Exception {
		FacilioChain mlDetailsChain = ReadOnlyChainFactory.fetchMLSummaryDetailsChain();
		FacilioContext context = mlDetailsChain.getContext();
		context.put(FacilioConstants.ContextNames.ALARM_ID, mlAnomalyAlarmId);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		mlDetailsChain.execute();
		setResult(FacilioConstants.ContextNames.ML_RCA_ALARMS, mlDetailsChain.getContext().get(FacilioConstants.ContextNames.ML_RCA_ALARMS));
		return SUCCESS;
	}
	
	public String fetchInsight() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		setResult(FacilioConstants.ContextNames.ALARM, MLAPI.getAlarmInsight(context));
		return SUCCESS;
	}
	
	private int dateOperator = -1;
	public int getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(int dateOperator) {
		this.dateOperator = dateOperator;
	}
	
	private String dateOperatorValue;
	public String getDateOperatorValue() {
		return dateOperatorValue;
	}
	public void setDateOperatorValue(String dateOperatorValue) {
		this.dateOperatorValue = dateOperatorValue;
	}
	
	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	private long alarmId = -1;
	
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	
	private long resourceId = -1;
	
	
	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

private long ruleId = -1;
	
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}


	public String fetchRelatedAssetAlarms() throws Exception {
		FacilioChain rAssetChain = ReadOnlyChainFactory.fetchRelatedAssetAlarms();
		FacilioContext context = rAssetChain.getContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.IS_RCA, false);
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		context.put(FacilioConstants.ContextNames.READING_RULE_ID, ruleId);
		context.put(FacilioConstants.ContextNames.PARENT_ALARM_ID, parentAlarmId);
		context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		rAssetChain.execute();
		List<Long> relatedAsset = (List<Long>) rAssetChain.getContext().get(FacilioConstants.ContextNames.RESOURCE_LIST);
		if (relatedAsset != null && relatedAsset.size() > 0) {
			setResult(ContextNames.ALARM_LIST, rAssetChain.getContext().get(ContextNames.ALARM_LIST));
		} else {
			setResult(ContextNames.ALARM_LIST, rAssetChain.getContext().get(FacilioConstants.ContextNames.RESOURCE_LIST));
		}
//		setResult(FacilioConstants.ContextNames.RESOURCE_LIST, context.get(FacilioConstants.ContextNames.RESOURCE_LIST));
		return SUCCESS;
	}

	public long getParentAlarmId() {
		return parentAlarmId;
	}

	public void setParentAlarmID(long parentAlarmId) {
		this.parentAlarmId = parentAlarmId;
	}

	long parentAlarmId = -1;

	
	long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public String fetchMetrics() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.fetchAnomalyMetricsChain();
		
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.SITE_ID, siteId);
		context.put(FacilioConstants.ContextNames.IS_RCA, isRca());
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		chain.execute();
		
		setResult("metrics", chain.getContext().get(ContextNames.RESULT));
		return SUCCESS;
	}
	
	private Boolean rca;
	public boolean isRca() {
		if (rca == null) {
			return false;
		}
		return rca;
	}
	public void setRca(boolean rca) {
		this.rca = rca;
	}
}
