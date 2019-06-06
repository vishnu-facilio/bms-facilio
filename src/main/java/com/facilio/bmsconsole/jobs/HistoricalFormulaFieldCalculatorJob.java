package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoricalFormulaFieldCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(HistoricalFormulaFieldCalculatorJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long formulaId = jc.getJobId();
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			DateRange range = getRange(jc, formula, props);
			boolean historicalAlarm = false;
			if (props != null && props.get("historicalAlarm") != null) {
				historicalAlarm = (boolean) props.get("historicalAlarm");
			}
			
			if (range == null) {
				logger.log(Level.SEVERE, "Historical Formula calculation not done for formula : "+formulaId+", because no range specified");
				return;
			}
			FormulaFieldAPI.historicalCalculation(formula, range, historicalAlarm);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("HistoricalFormulaFieldCalculatorJob", "Historical EnPI calculation failed for : "+jc.getJobId(), e);
		}
	}
	
	private DateRange getRange(JobContext jc, FormulaFieldContext formula, JSONObject props) throws Exception {
		long currentTime = DateTimeUtil.getCurrenTime();
		DateRange range = null;
		switch (formula.getTriggerTypeEnum()) {
			case PRE_LIVE_READING:
				return null;
			case POST_LIVE_READING:
				if (props == null || props.isEmpty()) {
					return null;
				}
				range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
				if (range.getStartTime() == -1) {
					return null;
				}
				if (range.getEndTime() == -1) {
					range.setEndTime(currentTime);
				}
				break;
			case SCHEDULE:
				if (props == null || props.isEmpty()) {
					range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
				}
				else {
					range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
					if (range.getStartTime() == -1) {
						range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
					}
					if (range.getEndTime() == -1) {
						range.setEndTime(currentTime);
					}
				}
				break;
		}
		return range;
	}
}
