package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddTaskSectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskSectionContext section = (TaskSectionContext) context.get(FacilioConstants.ContextNames.TASK_SECTION);
		if(section != null) {
			Map<String, Object> sectionProp = FieldUtil.getAsProperties(section);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getTaskSectionModule().getTableName())
					.fields(FieldFactory.getTaskSectionFields())
					.addRecord(sectionProp)
					;
			insertBuilder.save();
			section.setId((long) sectionProp.get("id"));
		}
		return false;
	}
	
}
