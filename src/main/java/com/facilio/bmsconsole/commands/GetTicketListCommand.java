package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetTicketListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
														.connection(conn)
														.dataTableName(dataTableName)
														.moduleName(moduleName)
														.beanClass(TicketContext.class)
														.select(fields)
														.orderBy("ID");

		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.where(criteria.computeWhereClause(), criteria.getComputedValues());
		}
		
		List<TicketContext> tickets = builder.getAsBean();
		context.put(FacilioConstants.ContextNames.TICKET_LIST, tickets);
		
		return false;
	}

}
