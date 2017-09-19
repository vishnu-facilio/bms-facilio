package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class UpdateClosedTasksCounterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			long parentTicketId = task.getParentTicketId();
			if(parentTicketId != -1 && task.getTicket() != null && task.getTicket().getStatus() != null) {
				TicketContext ticket = new TicketContext();
				ticket.setNoOfClosedTasks(getClosedTasksCount(parentTicketId));
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField("noOfClosedTasks", FacilioConstants.ContextNames.TICKET);
				List<FacilioField> fields = new ArrayList<>();
				fields.add(field);
				
				try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
					UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
																			.connection(conn)
																			.moduleName(FacilioConstants.ContextNames.TICKET)
																			.table("Tickets")
																			.fields(fields)
																			.andCustomWhere("ID = ?", parentTicketId);
					
					updateBuilder.update(ticket);
				}
				catch(Exception e) {
					e.printStackTrace();
					throw e;
				}
			}
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
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(fields)
														.table("Tasks")
														.innerJoin("Tickets")
														.on("Tasks.TICKET_ID = Tickets.ID")
														.innerJoin("TicketStatus")
														.on("Tickets.STATUS_ID = TicketStatus.ID")
														.andCustomWhere("Tasks.PARENT_TICKET_ID = ? AND TicketStatus.STATUS_TYPE = 2", parentTicketId);
			
			List<Map<String, Object>> rs = builder.get();
			if(rs != null && !rs.isEmpty()) {
				Map<String, Object> count = rs.get(0);
				if(count.get("closedTasks") != null) {
					Long counter = (Long) count.get("closedTasks"); 
					return counter.intValue();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return 0;
	}
}
