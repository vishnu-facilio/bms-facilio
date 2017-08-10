package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExecuteAllWorkflowsCommand implements Command 
{
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.connection(conn)
				.table("Workflow_Rule")
				.beanClass(CampusContext.class)
				.select(fields)
				.orderBy("ID");
		
		return false;
	}

}
