package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AddClearCommentInWoOnAlarmClearCommand extends FacilioCommand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AddClearCommentInWoOnAlarmClearCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
//		LOGGER.info("Event types in Add clear comment : "+eventTypes);
		if (eventTypes != null && eventTypes.contains(EventType.ALARM_CLEARED)) {
			AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (alarm.getWoId() != -1) {
				NoteContext note = new NoteContext();
				note.setBody("Alarm associated with this automated work order has been cleared at "+alarm.getModifiedTimeString());
				note.setParentId(alarm.getWoId());
				note.setCreatedTime(alarm.getModifiedTime());
				
				FacilioContext noteContext = new FacilioContext();
				noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
				noteContext.put(FacilioConstants.ContextNames.NOTE, note);

				FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
				addNote.execute(noteContext);
			}
		}
		
		return false;
	}

}
