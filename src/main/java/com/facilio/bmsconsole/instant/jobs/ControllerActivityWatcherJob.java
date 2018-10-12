package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.InstantJob;

public class ControllerActivityWatcherJob extends InstantJob {

	private static final Logger LOGGER = LogManager.getLogger(ControllerActivityWatcherJob.class.getName());
	private static final long THREAD_SLEEP_BUFFER = 5000;
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		try {
			ControllerActivityWatcherContext watcher = (ControllerActivityWatcherContext) context.get(FacilioConstants.ContextNames.CONTROLLER_ACTIVITY_WATCHER);
			List<ControllerContext> inCompleteControllers = (List<ControllerContext>) context.get(FacilioConstants.ContextNames.CONTROLLER_LIST);
			List<ControllerContext> completedControllers = new ArrayList<>();
			Map<String, Long> activityIds = new HashMap<>();
			
			while (!inCompleteControllers.isEmpty()) {
				Iterator<ControllerContext> itr = inCompleteControllers.iterator();
				while (itr.hasNext()) {
					ControllerContext controller = itr.next();
					Map<String, Object> activity = ControllerAPI.getControllerActivity(controller, watcher.getRecordTime());
					if (activity != null) {
						itr.remove();
						completedControllers.add(controller);
						activityIds.put(controller.getMacAddr(), (Long) activity.get("id"));
					}
				}
				
				Thread.sleep(THREAD_SLEEP_BUFFER);
			}
			
			ControllerAPI.markWatcherAsComplete(watcher.getId());
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in Controller Watcher Job", e);
			CommonCommandUtil.emailException("ControllerActivityWatcherJob", "Error occurred in Controller Watcher Job", e);
		}
	}

}
