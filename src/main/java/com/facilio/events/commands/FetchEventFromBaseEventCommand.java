package com.facilio.events.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.PreEventContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;

public class FetchEventFromBaseEventCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseEventContext> baseEvents = (List<BaseEventContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        List<EventContext> events = convertToEventContext(baseEvents);
        context.put(EventConstants.EventContextNames.EVENT_LIST, events);
        return false;
    }

    private List<EventContext> convertToEventContext(List<BaseEventContext> baseEvents) {
        List<EventContext> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(baseEvents)) {
            for (BaseEventContext baseEventContext : baseEvents) {
                EventContext event = new EventContext();
                event.setId(baseEventContext.getId());
                event.setDescription(baseEventContext.getDescription());

                event.setEventMessage(baseEventContext.getEventMessage());
                if (baseEventContext.getInternalStateEnum() != null) {
                    event.setInternalState(baseEventContext.getInternalStateEnum());
                }
                event.setEventState(baseEventContext.getEventState());
                if (baseEventContext.getResource() != null) {
                    event.setResourceId(baseEventContext.getResource().getId());
                }
                if (baseEventContext.getAlarmOccurrence() != null) {
                    event.setAlarmId(baseEventContext.getAlarmOccurrence().getId());
                }
                event.setCreatedTime(baseEventContext.getCreatedTime());
                event.setMessageKey(baseEventContext.getMessageKey());
                event.setComment(baseEventContext.getComment());
                event.setAdditionInfo(baseEventContext.getAdditionInfo());
                if (baseEventContext.getSeverity() != null) {
                    event.setSeverity(baseEventContext.getSeverity().getDisplayName());
                }

                event.setOrgId(baseEventContext.getOrgId());

                if (baseEventContext instanceof ReadingEventContext) {
                    ReadingEventContext readingEventContext = (ReadingEventContext) baseEventContext;
                    if (readingEventContext.getRule() != null) {
                        event.setEventRuleId(readingEventContext.getRule().getId());
                    }
                    if (readingEventContext.getSubRule() != null) {
                        event.setSubRuleId(readingEventContext.getSubRule().getId());
                    }
                } else if (baseEventContext instanceof BMSEventContext) {
                    BMSEventContext bmsEvent = (BMSEventContext) baseEventContext;
                    event.setSource(bmsEvent.getSource());
                    event.setCondition(bmsEvent.getCondition());
                }
                else if (baseEventContext instanceof PreEventContext) {
                    PreEventContext preEventContext = (PreEventContext) baseEventContext;
                    if (preEventContext.getRule() != null) {
                        event.setEventRuleId(preEventContext.getRule().getId());
                    }
                    if (preEventContext.getSubRule() != null) {
                        event.setSubRuleId(preEventContext.getSubRule().getId());
                    }
                }

                list.add(event);
            }
        }
        return list;
    }


}
