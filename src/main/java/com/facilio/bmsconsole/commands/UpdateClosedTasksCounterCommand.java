package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

@Log4j
public class UpdateClosedTasksCounterCommand extends FacilioCommand {

    private static Logger log = LogManager.getLogger(UpdateClosedTasksCounterCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			if(task.getStatusNew() != -1) {
				long parentTicketId = task.getParentTicketId();
				
				TicketContext ticket = new TicketContext();
				int noOfClosedTasks = getClosedTasksCount(parentTicketId);
				ticket.setNoOfClosedTasks(noOfClosedTasks);
				LOGGER.error("noOfClosedTasks: " + noOfClosedTasks);
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField("noOfClosedTasks", FacilioConstants.ContextNames.WORK_ORDER);
				List<FacilioField> fields = new ArrayList<>();
				fields.add(field);
				
				UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
																		.moduleName(FacilioConstants.ContextNames.TICKET)
																		.table("Tickets")
																		.fields(fields)
																		.andCustomWhere("ID = ?", parentTicketId);
				
				int updated = updateBuilder.update(ticket);
				LOGGER.error("No. of rows updated in Tickets table for noOfClosedTasks: " + updated);
			}
			else{
				LOGGER.error("Status of Task is -1.");
			}
		}else {
			LOGGER.error("Task is null.");
		}
		return false;
	}
	
	private int getClosedTasksCount(long parentTicketId) throws Exception {
		FacilioField field = new FacilioField();
		field.setName("closedTasks");
		field.setColumnName("COUNT(*)");
		field.setDataType(FieldType.NUMBER);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(field);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(fields)
														.table("Tasks")
														.andCustomWhere("Tasks.ORGID = ? AND Tasks.PARENT_TICKET_ID = ? AND Tasks.STATUS = 2", orgId, parentTicketId);
			
			List<Map<String, Object>> rs = builder.get();
			if(rs != null && !rs.isEmpty()) {
				Map<String, Object> count = rs.get(0);
				if(count.get("closedTasks") != null) {
					Long counter = ((Number) count.get("closedTasks")).longValue(); 
					return counter.intValue();
				}
			}else{
				LOGGER.info("Query to fetch closed task count is null/empty");
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		
		return 0;
	}
}
