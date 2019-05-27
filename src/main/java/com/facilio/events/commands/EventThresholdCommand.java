package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class EventThresholdCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule rule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(rule != null) {
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			if(event.getEventStateEnum() != EventState.IGNORED) {
				if (rule.getThresholdCriteriaId() != -1) {
                    Criteria thresholdCriteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), rule.getThresholdCriteriaId());
                    boolean isThresholdMatched = thresholdCriteria.computePredicate().evaluate(FieldUtil.getAsProperties(event));
                    if (isThresholdMatched) {
                    	long lastEventTime = (long) context.get(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP);
                        long currentEventTime = event.getCreatedTime();
                        boolean skipEvent = lastEventTime != -1 && (currentEventTime - lastEventTime) > (rule.getThresholdOverSeconds()*1000);
                        context.put(EventConstants.EventContextNames.EVENT_LAST_TIMESTAMP, event.getCreatedTime());
                        if (skipEvent) {
                            event.setEventState(EventContext.EventState.IGNORED);
                        } else {
                        	Map<String, Integer> eventCountMap = (Map<String, Integer>) context.get(EventConstants.EventContextNames.EVENT_COUNT_MAP);
                            int thresholdOccurs = rule.getThresholdOccurs();
                            int numberOfEvents = eventCountMap.getOrDefault(event.getMessageKey(), 0);
                            int numberOfEventsOccurred = numberOfEvents + 1;
                            eventCountMap.put(event.getMessageKey(), numberOfEventsOccurred);
                            if (thresholdOccurs <= (numberOfEventsOccurred)) {
                                eventCountMap.put(event.getMessageKey(), 0);
                            } else {
                                event.setEventState(EventContext.EventState.IGNORED);
                            }
                        }
                    }
                }
				event.setInternalState(EventInternalState.THRESHOLD_DONE);
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		return false;
	}

}
