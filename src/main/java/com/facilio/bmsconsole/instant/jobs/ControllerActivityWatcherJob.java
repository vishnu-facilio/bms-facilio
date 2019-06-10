package com.facilio.bmsconsole.instant.jobs;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.job.InstantJob;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ControllerActivityWatcherJob extends InstantJob {

	private static final Logger LOGGER = LogManager.getLogger(ControllerActivityWatcherJob.class.getName());
	private static final long THREAD_SLEEP_BUFFER = 5000;
	private static final long TIME_OUT = 15 * 60 * 1000;
	private boolean timedOut = false;
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		try {
			ControllerActivityWatcherContext watcher = (ControllerActivityWatcherContext) context.get(FacilioConstants.ContextNames.CONTROLLER_ACTIVITY_WATCHER);
			long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
			long jobStartTime = System.currentTimeMillis();
			List<ControllerContext> controllers = (List<ControllerContext>) context.get(FacilioConstants.ContextNames.CONTROLLER_LIST);
			if (jobStartTime > startTime + TIME_OUT) { //Make controllers inactive and return
				LOGGER.info("Ending "+watcher+" because it timed out");
				markInCompleteControllersAsInActive(watcher, controllers);
				return;
			}
			Set<Long> activityIds = keepCheckingAndGetActivityIds(controllers, watcher);
			LOGGER.debug("Completed listening : "+activityIds);
			Map<Long, JSONObject> activityRecords = ControllerAPI.getControllerActivityRecords(activityIds);
			List<ControllerContext> formulaControllers = startPostFormulaCalculation(activityRecords, watcher);
			if (formulaControllers != null && !formulaControllers.isEmpty()) {
				ControllerActivityWatcherContext nextLevelWatcher = ControllerAPI.addActivityWatcher(watcher.getRecordTime(), watcher.getDataInterval(), watcher.getLevel() + 1);
				ControllerAPI.scheduleControllerActivityJob(nextLevelWatcher, formulaControllers);
			}
			LOGGER.debug("Gonna mark as complete for "+watcher);
			ControllerAPI.markWatcherAsComplete(watcher.getId());
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in Controller Watcher Job", e);
			
			if ( !(e instanceof NullPointerException && ExceptionUtils.getStackTrace(e).contains("com.mysql.jdbc")) && !(e instanceof SQLException && (e.getMessage().contains("No operations allowed after statement closed") || e.getMessage().contains("Operation not allowed after ResultSet closed"))) ) { //Not sending email for transaction timeout
				CommonCommandUtil.emailException("ControllerActivityWatcherJob", "Error occurred in Controller Watcher Job", e);
			}
		}
	}
	
	private void markInCompleteControllersAsInActive(ControllerActivityWatcherContext watcher, List<ControllerContext> controllers) throws Exception {
		Map<String, ControllerContext> inCompleteControllers = controllers.stream()
																			.filter(c -> c.getId() > 0)
																			.collect(Collectors.toMap(ControllerContext::getMacAddr, Function.identity()));
		
		if (inCompleteControllers != null && !inCompleteControllers.isEmpty()) {
			List<Map<String, Object>> activities = ControllerAPI.getControllerActivities(inCompleteControllers.values(), watcher.getRecordTime());
			if (activities != null && !activities.isEmpty()) {
				for (Map<String, Object> activity : activities) {
					inCompleteControllers.remove(activity.get("controllerMacAddr"));
				}
			}
			
			if (!inCompleteControllers.isEmpty()) {
				ControllerAPI.makeControllerInActive(inCompleteControllers.values()
																			.stream()
																			.map(ControllerContext::getId)
																			.collect(Collectors.toList())
																			);
				
				StringBuilder msg = new StringBuilder()
										.append("The following Controller have been made inactive because we didn't receive data for time : ")
										.append(DateTimeUtil.getFormattedTime(watcher.getRecordTime()))
										.append("\n")
										.append(inCompleteControllers.values().stream().map(ControllerContext::getMacAddr).collect(Collectors.joining(", ")))
										;
				
				CommonCommandUtil.emailAlert("Controller made inactive", msg.toString());
			}
		}
	}
	
	private Set<Long> keepCheckingAndGetActivityIds(List<ControllerContext> controllers, ControllerActivityWatcherContext watcher) throws Exception {
		Map<String, ControllerContext> inCompleteControllers = controllers.stream().collect(Collectors.toMap(ControllerContext::getMacAddr, Function.identity()));
		LOGGER.info("Controllers for watcher : "+watcher+" is "+inCompleteControllers);
//		List<ControllerContext> completedControllers = new ArrayList<>();
		Map<String, Set<Long>> activityMap = new HashMap<>();
		Set<Long> activityIds = new HashSet<>();
		
		while (!inCompleteControllers.isEmpty() && !timedOut) {
			List<Map<String, Object>> activities = ControllerAPI.getControllerActivities(inCompleteControllers.values(), watcher.getRecordTime());
			if (activities != null && !activities.isEmpty()) {
				LOGGER.debug("Activities for time : "+watcher.getRecordTime()+" and interval : "+watcher.getDataInterval()+" : "+activities);
				for (Map<String, Object> activity : activities) {
					ControllerContext controller = inCompleteControllers.get(activity.get("controllerMacAddr"));
					if (controller != null) {
						int batches = controller.getBatchesPerCycle() == -1 ? 1 : controller.getBatchesPerCycle();
						Set<Long> currentActivities = addAndGet(activityMap, controller.getMacAddr(), (long) activity.get("id"));
						if (currentActivities.size() == batches) {
							inCompleteControllers.remove(controller.getMacAddr());
//							completedControllers.add(controller);
							activityIds.addAll(currentActivities);
						}
					}
				}
			}
			
			Thread.sleep(THREAD_SLEEP_BUFFER);
		}
		
		return activityIds;
	}
	
	private List<ControllerContext> startPostFormulaCalculation(Map<Long, JSONObject> activityRecords, ControllerActivityWatcherContext watcher) throws Exception {
		if (activityRecords != null && !activityRecords.isEmpty()) {
			Map<String, List<ReadingContext>> readingMap = new HashMap<>();
			Map<String, ReadingDataMeta> readingDataMeta = new HashMap<>();
			for (JSONObject json : activityRecords.values()) {
				MultiModuleReadingData currentData = FieldUtil.getAsBeanFromJson(json, MultiModuleReadingData.class);
				if (currentData.getReadingMap() != null && !currentData.getReadingMap().isEmpty()) {
					for (Map.Entry<String, List<ReadingContext>> entry : currentData.getReadingMap().entrySet()) {
						List<ReadingContext> readings = readingMap.get(entry.getKey());
						if (readings == null) {
							readingMap.put(entry.getKey(), entry.getValue());
						}
						else {
							readings.addAll(entry.getValue());
						}
					}
					readingDataMeta.putAll(currentData.getReadingDataMeta());
				}
			}
			
			FacilioContext formulaJC = new FacilioContext();
			formulaJC.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
			formulaJC.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, readingDataMeta);
			formulaJC.put(FacilioConstants.ContextNames.CONTROLLER_LEVEL, watcher.getLevel() + 1);
			formulaJC.put(FacilioConstants.ContextNames.CONTROLLER_TIME, watcher.getRecordTime());
			
			Chain formulaChain = ReadOnlyChainFactory.calculateFormulaChain();
			formulaChain.execute(formulaJC);
			
			return (List<ControllerContext>) formulaJC.get(FacilioConstants.ContextNames.CONTROLLER_LIST);
		}
		return null;
	}
	
	private Set<Long> addAndGet(Map<String, Set<Long>> activityIds, String macAddr, long activityId) {
		Set<Long> activities = activityIds.get(macAddr);
		if (activities == null) {
			activities = new HashSet<>();
			activityIds.put(macAddr, activities);
		}
		activities.add(activityId);
		return activities;
	}
	
	@Override
	public void handleTimeOut() {
		LOGGER.info("Controller Acitivty timed out!!");
		timedOut = true;
	}
}
