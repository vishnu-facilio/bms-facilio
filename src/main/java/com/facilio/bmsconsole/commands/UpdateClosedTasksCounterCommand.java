package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class UpdateClosedTasksCounterCommand implements Command {

    private static Logger log = LogManager.getLogger(UpdateClosedTasksCounterCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			long parentTicketId = task.getParentTicketId();
			if(parentTicketId != -1 && task.getStatus() != null) {
				TicketContext ticket = new TicketContext();
				ticket.setNoOfClosedTasks(getClosedTasksCount(parentTicketId));
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField("noOfClosedTasks", FacilioConstants.ContextNames.TASK);
				List<FacilioField> fields = new ArrayList<>();
				fields.add(field);
				
				try {
					UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
																			.moduleName(FacilioConstants.ContextNames.TICKET)
																			.table("Tickets")
																			.fields(fields)
																			.andCustomWhere("ID = ?", parentTicketId);
					
					updateBuilder.update(ticket);
				}
				catch(Exception e) {
					log.info("Exception occurred ", e);
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
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(fields)
														.table("Tasks")
														.innerJoin("Tickets")
														.on("Tasks.ID = Tickets.ID")
														.innerJoin("TicketStatus")
														.on("Tickets.STATUS_ID = TicketStatus.ID")
														.andCustomWhere("TicketStatus.ORGID = ? AND Tickets.ORGID = ? AND Tasks.PARENT_TICKET_ID = ? AND TicketStatus.STATUS_TYPE = 2", orgId, orgId, parentTicketId);
			
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
			log.info("Exception occurred ", e);
			throw e;
		}
		
		return 0;
	}
}
