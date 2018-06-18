package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalFormulaFieldCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(HistoricalFormulaFieldCalculatorJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long formulaId = jc.getJobId();
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			logger.log(Level.INFO, "Calculating Formula for "+formula.getName());
			DateRange range = getRange(jc, formula);
			
			if (range == null) {
				logger.log(Level.SEVERE, "Historical Formula calculation not done for formula : "+formulaId+", because no range specified");
				return;
			}
			FormulaFieldAPI.historicalCalculation(formula, range);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("HistoricalFormulaFieldCalculatorJob", "Historical EnPI calculation failed for : "+jc.getJobId(), e);
		}
	}
	
	private DateRange getRange(JobContext jc, FormulaFieldContext formula) throws Exception {
		JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		long currentTime = DateTimeUtil.getCurrenTime();
		DateRange range = null;
		switch (formula.getTriggerTypeEnum()) {
			case LIVE_READING:
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
