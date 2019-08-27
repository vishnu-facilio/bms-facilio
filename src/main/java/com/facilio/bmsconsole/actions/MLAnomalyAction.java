package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateRange;

public class MLAnomalyAction extends FacilioAction {

	
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
		FacilioContext context = new FacilioContext();
		MLAPI.getSubMeterDetails(mlAnomalyAlarmId);
		return SUCCESS;
	}
	
	public String fetchRcaAnomaly() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM_ID, mlAnomalyAlarmId);
		Chain mlDetailsChain = ReadOnlyChainFactory.fetchMLSummaryDetailsChain();
		mlDetailsChain.execute(context);
		setResult(FacilioConstants.ContextNames.ML_RCA_ALARMS, context.get(FacilioConstants.ContextNames.ML_RCA_ALARMS));
		return SUCCESS;
	}
	
	public String fetchInsight() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		MLAPI.getAlarmInsight(context);
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
}
