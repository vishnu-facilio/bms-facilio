package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.time.DateTimeUtil;

public class EnergyStarFetchDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarFetchDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		
		List<Long> fetchTimeList = (List<Long>) context.get(EnergyStarUtil.ENERGY_STAR_FETCH_TIME_LIST);
		
		EnergyStarPropertyContext property = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		List<ReadingContext> readings = new ArrayList<>();
		for(Long fetchTime :fetchTimeList) {
			
			ReadingContext readingContext = EnergyStarSDK.fetchMetrics(property, DateTimeUtil.getFormattedTime(fetchTime, "yyyy"), DateTimeUtil.getFormattedTime(fetchTime, "MM"));
			
			if(readingContext == null) {
				
				LOGGER.severe("NO data present for this property "+property.getId()+" time -- "+fetchTime);
			}
			else {
				
				readingContext.setParent(property);
				readingContext.setParentId(property.getId());
				readingContext.setTtime(fetchTime);
				
				readings.add(readingContext);
			}
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_CONTEXTS, readings);
		
		return false;
	}

}
