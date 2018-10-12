package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
			List<ControllerContext> controllers = (List<ControllerContext>) context.get(FacilioConstants.ContextNames.CONTROLLER_LIST);
			Map<String, ControllerContext> inCompleteControllers = controllers.stream().collect(Collectors.toMap(ControllerContext::getMacAddr, Function.identity()));
			LOGGER.info("Controllers for watcher : "+watcher+" is "+inCompleteControllers);
			List<ControllerContext> completedControllers = new ArrayList<>();
			Map<String, Long> activityIds = new HashMap<>();
			
			while (!inCompleteControllers.isEmpty()) {
				List<Map<String, Object>> activites = ControllerAPI.getControllerActivities(inCompleteControllers.values(), watcher.getRecordTime());
				if (activites != null && !activites.isEmpty()) {
					LOGGER.info("Activities for time : "+watcher.getRecordTime()+" and interval : "+watcher.getDataInterval()+" : "+activites);
					for (Map<String, Object> activity : activites) {
						ControllerContext controller = inCompleteControllers.get(activity.get("controllerMacAddr"));
						activityIds.put(controller.getMacAddr(), (Long) activity.get("id"));
						inCompleteControllers.remove(controller.getMacAddr());
						completedControllers.add(controller);
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
