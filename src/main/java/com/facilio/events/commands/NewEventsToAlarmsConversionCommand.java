package com.facilio.events.commands;

import com.facilio.activity.AlarmActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class NewEventsToAlarmsConversionCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = Logger.getLogger(NewEventsToAlarmsConversionCommand.class.getName());

	public interface PostTransactionEventListener {
		List<BaseEventContext> getPostTransactionEvents() throws Exception;
	}

	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = new HashMap<>();
	private Map<String, BaseAlarmContext> alarmMap = new HashMap<>();
	private List<BaseEventContext> baseEvents;
	private AlarmSeverityContext clearSeverity;

	private List<PostTransactionEventListener> postTransactionEventListeners;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			baseEvents = new ArrayList<>((List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST));
			context.put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);

			List<BaseEventContext> events = baseEvents;
			while (true) {
				iterateEventToProcess(context, events);
				Collection<BaseAlarmContext> values = alarmMap.values();
				if (CollectionUtils.isEmpty(values)) {
					break;
				}

				events = new ArrayList<>();
				for (BaseAlarmContext baseAlarm : values) {
					List<PostTransactionEventListener> posList = baseAlarm.removePosList();
					if (CollectionUtils.isNotEmpty(posList)) {
						if (postTransactionEventListeners == null) {
							postTransactionEventListeners = new ArrayList<>();
						}
						postTransactionEventListeners.addAll(posList);
					}

					List<BaseEventContext> baseEventContexts = baseAlarm.removeAdditionalEvents();
					if (CollectionUtils.isNotEmpty(baseEventContexts)) {
						events.addAll(baseEventContexts);
					}
				}
				if (CollectionUtils.isEmpty(events)) {
					break;
				} else {
					baseEvents.addAll(events);
				}
			}
			context.put("alarmOccurrenceMap", alarmOccurrenceMap);
			context.put("alarmMap", alarmMap);
		}
		return false;
	}

	private void iterateEventToProcess(Context context, List<BaseEventContext> baseEvents) throws Exception {
		Set<String> eventKeys = new HashSet<>();
		for (BaseEventContext baseEvent : baseEvents) {
			String messageKey = baseEvent.getMessageKey();
			eventKeys.add(messageKey);
		}

		List<AlarmOccurrenceContext> latestAlarmOccurrence = NewAlarmAPI.getLatestAlarmOccurance(new ArrayList<>(eventKeys));
		
		Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT); //For Historical Reading rule
		isHistorical = (isHistorical == null) ? false : isHistorical;
		
		if(isHistorical)
		{ 
			HashMap<String,AlarmOccurrenceContext> latestAlarmOccuranceMap = new HashMap<String,AlarmOccurrenceContext>();
			for(AlarmOccurrenceContext latestAlarmOccurrenceContext:latestAlarmOccurrence) {
				latestAlarmOccuranceMap.put(latestAlarmOccurrenceContext.getAlarm().getKey(), latestAlarmOccurrenceContext);
			}
			
			HashMap<String,AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) context.get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
			if(lastOccurrenceOfPreviousBatchMap != null && MapUtils.isNotEmpty(lastOccurrenceOfPreviousBatchMap)) { 
				for (String key : lastOccurrenceOfPreviousBatchMap.keySet()) {
					AlarmOccurrenceContext lastOccurrenceOfPreviousBatch = lastOccurrenceOfPreviousBatchMap.get(key);
					AlarmOccurrenceContext latestAlarmOccurrenceContext = latestAlarmOccuranceMap.get(key);
					
					if(latestAlarmOccurrenceContext != null && latestAlarmOccurrenceContext.getId() == lastOccurrenceOfPreviousBatch.getId()) {
						continue;
					}
					
					PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
					if (pointedList == null) { // adding previousBatch lastOccurrence as the first element in the list, preceding latestAlarmOccurrence
						pointedList = new PointedList<>();
						alarmOccurrenceMap.put(key, pointedList);
					}
					pointedList.add(lastOccurrenceOfPreviousBatch);
				}
			}
		}
		
		clearSeverity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY);
		for (AlarmOccurrenceContext alarmOccurrenceContext : latestAlarmOccurrence) {
			String key = alarmOccurrenceContext.getAlarm().getKey();
			PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
			if (pointedList == null) {
				// expecting records should come here
				pointedList = new PointedList<>();
				alarmOccurrenceMap.put(key, pointedList);
			}
			pointedList.add(alarmOccurrenceContext);
		}
		
		List<BaseEventContext> additionEventsCreated = new ArrayList<>();
		for (BaseEventContext baseEvent : baseEvents) {
			processEventToAlarm(baseEvent, context, additionEventsCreated);
		}
		baseEvents.addAll(additionEventsCreated);
		
		Boolean constructHistoricalAutoClearEvent = (Boolean) context.get(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT);
		constructHistoricalAutoClearEvent = (constructHistoricalAutoClearEvent == null) ? true : constructHistoricalAutoClearEvent;

		if(constructHistoricalAutoClearEvent) {

			for (String key : eventKeys) {
				PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(key);
				if (CollectionUtils.isEmpty(pointedList)) {
					continue;
				}
				
				if (pointedList.size() > 1 && pointedList.getLastRecord().getId() > 0) {	// fetching only if latest occurrence is not last new occurrence
					AlarmOccurrenceContext lastOccurrence = pointedList.get(pointedList.size()-2);
					AlarmOccurrenceContext nextOccurrence = NewAlarmAPI.getNextAlarmOccurrence(lastOccurrence.getAlarm().getId(), lastOccurrence.getCreatedTime());
					if (nextOccurrence != null) {
						lastOccurrence.setTimeBetweeenOccurrence((nextOccurrence.getCreatedTime() - lastOccurrence.getClearedTime()) / 1000);
					}
				}

				List<AlarmOccurrenceContext> list = new ArrayList<>(pointedList);
				for (AlarmOccurrenceContext alarmOccurrence : list) {
					if (!alarmOccurrence.equals(pointedList.getLastRecord()) && !alarmOccurrence.getSeverity().equals(clearSeverity)) {
						LOGGER.debug(alarmOccurrence.getId() + " : " + pointedList.getLastRecord().getId());
						BaseAlarmContext alarm = alarmOccurrence.getAlarm();
						alarm = NewAlarmAPI.getAlarm(alarm.getId());

						if (alarm.getTypeEnum() == BaseAlarmContext.Type.BMS_ALARM) {
							LOGGER.error("Event trying to auto clear for bms alarm " + alarm.getId() + ", occurrence - " + alarmOccurrence.getId() + ". last record - " + pointedList.getLastRecord().getId());
							continue;
						}

						BaseEventContext createdEvent = BaseEventContext.createNewEvent(alarm.getTypeEnum(), alarm.getResource(),
								clearSeverity, "Automated Clear Event", alarm.getKey(), alarmOccurrence.getLastOccurredTime() + 1000);
						JSONObject info = new JSONObject();
						info.put("field", "Severity");
						info.put("newValue", clearSeverity.getDisplayName());
						if (alarmOccurrence.getPreviousSeverity() != null){
							info.put("oldValue", AlarmAPI.getAlarmSeverity(alarmOccurrence.getPreviousSeverity().getId()).getDisplayName());
						}
						if (alarmOccurrence.getAlarm() != null && alarmOccurrence.getAlarm().getId() > 0) {
							CommonCommandUtil.addAlarmActivityToContext(alarmOccurrence.getAlarm().getId(),-1, AlarmActivityType.CLEAR_ALARM, info, (FacilioContext) context, alarmOccurrence.getId());
						}
						baseEvents.add(createdEvent);
						processEventToAlarm(createdEvent, context, additionEventsCreated);
					}
				}
			}	
		}
		
	}

	private void processEventToAlarm(BaseEventContext baseEvent, Context context, List<BaseEventContext> additionEventsCreated) throws Exception {
		if (baseEvent.getEventStateEnum() != EventState.IGNORED) {
			if(baseEvent.getSeverity().getSeverity().equals(FacilioConstants.Alarm.INFO_SEVERITY)) {
				baseEvent.setEventState(EventState.IGNORED);
			}
			else {
				addOrUpdateAlarm(baseEvent, context, additionEventsCreated);
			}
			baseEvent.setInternalState(EventInternalState.COMPLETED);
		}
	}

	private void addOrUpdateAlarm(BaseEventContext baseEvent, Context context, List<BaseEventContext> additionEventsCreated) throws Exception {
		PointedList<AlarmOccurrenceContext> pointedList = alarmOccurrenceMap.get(baseEvent.getMessageKey());
		if (pointedList == null) {
			pointedList = new PointedList<>();
			alarmOccurrenceMap.put(baseEvent.getMessageKey(), pointedList);
		}
		AlarmOccurrenceContext alarmOccurrence = pointedList.isEmpty() ? null : pointedList.getCurrentRecord();
		boolean mostRecent = pointedList.isCurrentLast();

		try {
			List<Long> alarmIds = Arrays.asList(152666L, 286707L, 286708L, 286709L, 152662L);
			if(alarmOccurrence != null) {
				if (alarmIds.contains(alarmOccurrence.getAlarm().getId())) {
					printPointedList(pointedList);
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}

		if (alarmOccurrence == null) {
			// Only for newly creating alarm
			alarmOccurrence = NewAlarmAPI.createAlarm(baseEvent, context);
			pointedList.add(alarmOccurrence);
			baseEvent.setEventState(EventState.ALARM_CREATED);
			CommonCommandUtil.addEventType(EventType.CREATE_OR_UPDATE_ALARM_SEVERITY,(FacilioContext)context);
		}
		else if (alarmOccurrence.getSeverity().equals(clearSeverity)) {
			if (baseEvent.getSeverity().equals(clearSeverity)) {
				baseEvent.setEventState(EventState.IGNORED);
				return;
			}
			// create alarm occurrence
			mostRecent = baseEvent.getCreatedTime() > pointedList.getLastRecord().getCreatedTime();

			try {
				List<Long> alarmIds = Arrays.asList(152666L, 286707L, 286708L, 286709L, 152662L);
				if (alarmIds.contains(alarmOccurrence.getAlarm().getId())) {
					LOGGER.info("mostRecent for Alarm id: "+ alarmOccurrence.getAlarm().getId() + " in alarm occurrence creation: "+mostRecent);
				}
			}catch (Exception ex) {}
			
			int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
			if (alarmOccurrence.getCreatedTime() < baseEvent.getCreatedTime()) {
				oldObjectIndex ++;
			}
			alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
			AlarmOccurrenceContext prevOccurrence = null;
			if (mostRecent) {
				pointedList.add(alarmOccurrence);
				pointedList.moveNext();
			} else {
				if (oldObjectIndex == 0) {
					prevOccurrence = NewAlarmAPI.getLatestAlarmOccurance(alarmOccurrence.getAlarm().getId(), alarmOccurrence.getCreatedTime());
					if (prevOccurrence != null) {
						pointedList.add(oldObjectIndex, prevOccurrence);
						oldObjectIndex ++;	// increase the index
					}
				}
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}
			if (prevOccurrence == null && pointedList.getPosition() >= 1) { // if there is no previous occurrence we need not get it
				prevOccurrence = pointedList.get(pointedList.getPosition() - 1);
			}
			if (prevOccurrence != null) {
				prevOccurrence.setTimeBetweeenOccurrence((alarmOccurrence.getCreatedTime() - prevOccurrence.getClearedTime()) / 1000);
			}
			baseEvent.setEventState(EventState.ALARM_CREATED);
		}
		else { // if alarm is not cleared, only update in local object.

			// for first historical event
			if (baseEvent.getCreatedTime() < alarmOccurrence.getCreatedTime()) {
				mostRecent = false;
				int oldObjectIndex = pointedList.indexOf(alarmOccurrence);
				alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
				pointedList.add(oldObjectIndex, alarmOccurrence);
				pointedList.setPosition(oldObjectIndex);
			}

			BaseEventContext additionClearEvent = baseEvent.createAdditionClearEvent(alarmOccurrence);
			if (additionClearEvent != null) {
				additionClearEvent.setSeverity(clearSeverity);
				NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, additionClearEvent, mostRecent, context);
				additionClearEvent.setAlarmOccurrence(alarmOccurrence);
				additionClearEvent.setEventState(EventState.ALARM_UPDATED);
				additionEventsCreated.add(additionClearEvent);

				alarmOccurrence = NewAlarmAPI.createAlarmOccurrence(alarmOccurrence.getAlarm(), baseEvent, mostRecent, context);
				pointedList.add(alarmOccurrence);
				pointedList.moveNext();
			}
			else {
				NewAlarmAPI.updateAlarmOccurrence(alarmOccurrence, baseEvent, mostRecent, context);
			}
			baseEvent.setEventState(EventState.ALARM_UPDATED);
		}
		
		alarmMap.put(baseEvent.getMessageKey(), alarmOccurrence.getAlarm());
		baseEvent.setAlarmOccurrence(alarmOccurrence);
		baseEvent.setBaseAlarm(alarmOccurrence.getAlarm());
		try {
			List<Long> alarmIds = Arrays.asList(152666L, 286707L, 286708L, 286709L, 152662L, 286708L);
			if (alarmIds.contains(baseEvent.getBaseAlarm().getId())) {
				JSONObject baseEventFields = FieldUtil.getAsJSON(baseEvent);
				LOGGER.info("BaseEvent Context for Alarm id: " + baseEvent.getBaseAlarm().getId() + " after updating Alarm : " + baseEventFields);
			}
		}catch (Exception ex) {}
	}

	private void printPointedList(PointedList<AlarmOccurrenceContext> pointedList)throws Exception{
		List<JSONObject> jsonObjectList = new ArrayList<>();
		for (AlarmOccurrenceContext alarmOccurrenceContext : pointedList) {
			JSONObject jsonObject = FieldUtil.getAsJSON(alarmOccurrenceContext);
			jsonObjectList.add(jsonObject);
		}
		LOGGER.info("Pointed List: "+jsonObjectList);
	}
	public static class PointedList<E> extends ArrayList<E> {
		private static final long serialVersionUID = 1L;
		private int position = 0;
		
		public PointedList() {
		}
		
		public void moveNext() {
			position ++;
			if (position >= size()) {
				position = size() -1;
			}
		}
		
		public void movePrevious() {
			position --;
			if (position < 0) {
				position = 0;
			}
		}
		
		public void setPosition(int position) {
			if (position < 0) {
				position = 0;
			}
			else if (position >= size()) {
				position = size() - 1;
			}
			this.position = position;
		}
		
		public int getPosition() {
			return this.position;
		}

		public boolean isCurrentLast() {
			return position == (size() - 1);
		}
		
		public E getLastRecord() {
			return get(size() - 1);
		}
		
		public E getCurrentRecord() {
			if (CollectionUtils.isEmpty(this)) {
				return null;
			}
			return get(position);
		}
	}

	@Override
	public boolean postExecute() throws Exception {
		if (CollectionUtils.isNotEmpty(postTransactionEventListeners)) {
			List<BaseEventContext> baseEventContexts = new ArrayList<>();
			for (PostTransactionEventListener ptel : postTransactionEventListeners) {
				baseEventContexts.addAll(ptel.getPostTransactionEvents());
			}
			if (CollectionUtils.isNotEmpty(baseEventContexts)) {
				FacilioChain chain = TransactionChainFactory.getV2AddEventChain(false);
				Context context = chain.getContext();
				context.put(EventConstants.EventContextNames.EVENT_LIST, baseEventContexts);
				chain.execute();
			}
		}
		return false;
	}
}
