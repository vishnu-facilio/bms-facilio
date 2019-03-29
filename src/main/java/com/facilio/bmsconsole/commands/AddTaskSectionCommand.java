package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddTaskSectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
