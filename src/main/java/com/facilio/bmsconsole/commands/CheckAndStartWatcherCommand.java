package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class CheckAndStartWatcherCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CheckAndStartWatcherCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
		if (controller != null) {
			long time = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
			int dataInterval = ControllerAPI.getDataIntervalInMin(controller);
			Integer level = (Integer) context.get(FacilioConstants.ContextNames.CONTROLLER_LEVEL);
			if (level == null) {
				level = 0;
			}
			
			try {
				ControllerActivityWatcherContext watcher = ControllerAPI.getActivityWatcher(time, dataInterval, level);
				if (watcher == null) {
					LOGGER.info("Starting watcher for interval : "+dataInterval+" at time : "+time);
					watcher = ControllerAPI.addActivityWatcher(time, dataInterval, level);
					FacilioContext jobContext = new FacilioContext();
					jobContext.put(FacilioConstants.ContextNames.CONTROLLER_ACTIVITY_WATCHER, watcher);
					jobContext.put(FacilioConstants.ContextNames.CONTROLLER_LIST, ControllerAPI.getActtiveControllers(controller.getDataInterval()));
					jobContext.put(FacilioConstants.ContextNames.START_TIME, System.currentTimeMillis());
					
					FacilioTimer.scheduleInstantJob("ControllerActivityWatcher", jobContext);
				}
			}
			catch (Exception e) {
				LOGGER.error("Error occurred during addition of watcher for interval : "+dataInterval+" at time : "+time, e);
			}
		}
		return false;
	}

}
