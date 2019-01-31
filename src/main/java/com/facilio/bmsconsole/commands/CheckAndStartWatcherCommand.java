package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;

public class CheckAndStartWatcherCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CheckAndStartWatcherCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
		Boolean adjustTime = (Boolean) context.get(FacilioConstants.ContextNames.ADJUST_READING_TTIME);
		if (adjustTime == null) {
			adjustTime = true;
		}
		if (controller != null && adjustTime) {
			long time = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
			int dataInterval = ControllerAPI.getDataIntervalInMin(controller);
			Integer level = (Integer) context.get(FacilioConstants.ContextNames.CONTROLLER_LEVEL);
			if (level == null) { //Starting watcher only for level 0 because for subsequent levels, watcher will started by prev level watcher
				level = 0;
				
				try {
					ControllerActivityWatcherContext watcher = ControllerAPI.getActivityWatcher(time, dataInterval, level);
					if (watcher == null) {
						LOGGER.debug("Starting watcher for interval : "+dataInterval+" at time : "+time);
						watcher = ControllerAPI.addActivityWatcher(time, dataInterval, level);
						ControllerAPI.scheduleControllerActivityJob(watcher, ControllerAPI.getActtiveControllers(controller.getDataInterval()));
					}
				}
				catch (Exception e) {
					LOGGER.error("Error occurred during addition of watcher for interval : "+dataInterval+" at time : "+time, e);
				}
			}
		}
		return false;
	}

}
