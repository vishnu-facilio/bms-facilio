package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.TaskAPI;

public class AddTaskNoteCommand implements Command {

	public static final String TASK_ID = "taskId";
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(TASK_ID);
		if(taskId <= 0) {
			throw new IllegalArgumentException("Invalid Task Id");
		}
		
		NoteContext noteContext = (NoteContext) context;
		long taskNoteId = TaskAPI.addTaskNotes(taskId, noteContext.getNoteId());
		
		return false;
	}
	
}
