package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class CheckAndStartWatcherCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER);
		if (controller != null) {
			long time = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
			int dataInterval = ControllerAPI.getDataIntervalInMin(controller);
			ControllerActivityWatcherContext watcher = ControllerAPI.getActivityWatcher(time, dataInterval);
			
			if (watcher == null) {
				watcher = ControllerAPI.addActivityWatcher(time, dataInterval);
				FacilioContext jobContext = new FacilioContext();
				jobContext.put(FacilioConstants.ContextNames.CONTROLLER_ACTIVITY_WATCHER, watcher);
				jobContext.put(FacilioConstants.ContextNames.CONTROLLER_LIST, ControllerAPI.getControllers(controller.getDataInterval()));
				
				FacilioTimer.scheduleInstantJob("ControllerActivityWatcher", jobContext);
			}
		}
		return false;
	}

}
