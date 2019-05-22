package com.facilio.bmsconsole.commands;

import com.facilio.modules.BaseLineContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ValidateBaseLineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		BaseLineContext baseLine = (BaseLineContext) context.get(FacilioConstants.ContextNames.BASE_LINE);
		if (baseLine != null) {
			checkForNull(baseLine);
			switch (baseLine.getRangeTypeEnum()) {
				case ANY_HOUR:
					if (!DateTimeUtil.isSameHour(baseLine.getStartTime(), baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be in same hour for range type "+baseLine.getRangeTypeEnum().toString());
					}
					break;
				case ANY_DAY:
					if (!DateTimeUtil.isSameDay(baseLine.getStartTime(), baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be on same day for range type "+baseLine.getRangeTypeEnum().toString());
					}
					break;
				case ANY_WEEK:
					if (!DateTimeUtil.isSameWeek(baseLine.getStartTime(), baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be on same week for range type "+baseLine.getRangeTypeEnum().toString());
					}
					break;
				case ANY_MONTH:
					if (!DateTimeUtil.isSameMonth(baseLine.getStartTime(), baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be on same month for range type "+baseLine.getRangeTypeEnum().toString());
					}
					break;
				case ANY_QUARTER:
					if (!DateTimeUtil.isSameQuarter(baseLine.getStartTime(),  baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be on same quarter for range type "+baseLine.getRangeTypeEnum().toString());
					}
				case ANY_YEAR:
					checkForNull(baseLine);
					if (!DateTimeUtil.isSameYear(baseLine.getStartTime(), baseLine.getEndTime())) {
						throw new IllegalArgumentException("Start and End time should be on same year for range type "+baseLine.getRangeTypeEnum().toString());
					}
					break;
				case CUSTOM:
					break;
				default:
					break;
			}
		}
		return false;
	}
	
	private void checkForNull(BaseLineContext baseLine) {
		if (baseLine.getRangeTypeEnum() == null) {
			throw new IllegalArgumentException("Range Type cannot be null for a base line");
		}
		
		if (baseLine.getStartTime() == -1 || baseLine.getEndTime() == -1) {
			throw new IllegalArgumentException("Start Time/ End Time cannot be null for base line of type "+baseLine.getRangeTypeEnum().toString());
		}
	}

}
