package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class AddFormulaFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null during addition");
		}
		long formulaId = FormulaFieldAPI.addFormulaField(formula);
		
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE); //From UI, Run history	
		dateRange = getRange(formula,FieldUtil.getAsJSON(dateRange));
		
		Boolean skipFormulaCalculation = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING);
		if(skipFormulaCalculation == null || skipFormulaCalculation.equals(Boolean.FALSE)) {
			if(dateRange != null)
			{
				FacilioChain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
				FacilioContext formulaFieldcontext = historicalCalculation.getContext();
				
				formulaFieldcontext.put(FacilioConstants.ContextNames.FORMULA_FIELD, formulaId);
				formulaFieldcontext.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);	
				historicalCalculation.execute();
			}
		}
		return false;
	}
	
	private DateRange getRange(FormulaFieldContext formula, JSONObject props) throws Exception {
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
					if((range.getStartTime() == -1) || (range.getEndTime() == -1))
					{
						range = null;
					}
				}
				else {
					range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
					if (range.getStartTime() == -1) {
						range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
					}
					if (range.getEndTime() == -1) {
						range.setEndTime(currentTime);
					}
					if((range.getStartTime() == -1) || (range.getEndTime() == -1))
					{
						range = null;
					}
				}
				break;
		}
		return range;
	}

}
