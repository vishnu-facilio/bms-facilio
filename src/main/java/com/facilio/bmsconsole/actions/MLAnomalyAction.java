package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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
		//MLAPI.getSubMeterDetails(mlAnomalyAlarmId);
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
}
