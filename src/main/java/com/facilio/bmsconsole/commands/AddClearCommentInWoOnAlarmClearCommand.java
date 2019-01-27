package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.serializable.SerializableCommand;

public class AddClearCommentInWoOnAlarmClearCommand implements SerializableCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType activity = (ActivityType) context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ALARM_CLEARED);
		
		if (activity == ActivityType.ALARM_CLEARED) {
			AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (alarm.getWoId() != -1) {
				NoteContext note = new NoteContext();
				note.setBody("Alarm associated with this automated work order has been cleared at "+alarm.getModifiedTimeString());
				note.setParentId(alarm.getWoId());
				
				FacilioContext noteContext = new FacilioContext();
				noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
				noteContext.put(FacilioConstants.ContextNames.TICKET_MODULE, FacilioConstants.ContextNames.WORK_ORDER);
				noteContext.put(FacilioConstants.ContextNames.NOTE, note);

				Chain addNote = TransactionChainFactory.getAddNotesChain();
				addNote.execute(noteContext);
			}
		}
		
		return false;
	}

}
