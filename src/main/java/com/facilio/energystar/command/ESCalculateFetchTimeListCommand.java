package com.facilio.energystar.command;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class ESCalculateFetchTimeListCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(ESCalculateFetchTimeListCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		long lastScoreAvailableDate = lastScoreAvailableDate();
		
		if(endTime > lastScoreAvailableDate) {
			endTime = lastScoreAvailableDate;
		}
		
		ScheduleInfo schedule = FormulaFieldAPI.getSchedule(FacilioFrequency.MONTHLY);
		List<DateRange> intervals = schedule.getTimeIntervals(startTime, endTime);
		
		List<Long> fetchTimeList = new ArrayList<Long>();
		for(DateRange interval :intervals) {
			fetchTimeList.add(interval.getStartTime());
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_FETCH_TIME_LIST, fetchTimeList);
		
		return false;
	}
	
	private long lastScoreAvailableDate() {
		int scoreAvailableOn = 15;
		ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
		int currentDate = zdt.getDayOfMonth();
		
		if(currentDate > scoreAvailableOn) {
			return DateTimeUtil.getMonthStartTime();
		}
		else {
			return DateTimeUtil.addMonths(DateTimeUtil.getMonthStartTime(), -1);
		}
		
	}
}
