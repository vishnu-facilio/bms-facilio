package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteTicketCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ids = new ArrayList<>();
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			ids.addAll(recordIds);
		}
		
		Set<Long> dependentIds = (Set<Long>) context.get(FacilioConstants.ContextNames.DEPENDENT_TICKET_IDS);
		if (dependentIds != null && !dependentIds.isEmpty()) {
			ids.addAll(dependentIds);
		}
		
		if (!ids.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, getRecords(module, recordIds));
			int rowsUpdated = 0;
			if(recordIds != null && !recordIds.isEmpty()) {
				FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
				rowsUpdated += deleteTickets(ticketModule, recordIds);
			}
			if (dependentIds != null && !dependentIds.isEmpty()) {
				rowsUpdated += deleteTickets(module, recordIds);
			}
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		}
		return false;
	}
	
	private List<? extends TicketContext> getRecords(FacilioModule module, List<Long> recordIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<? extends TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
																				.select(modBean.getAllFields(module.getName()))
																				.module(module)
																				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
																				.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
																				;
		return builder.get();
	}
	
	private int deleteTickets(FacilioModule module, List<Long> recordIds) throws SQLException {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(recordIds, module));

		return builder.delete();
	}

}
