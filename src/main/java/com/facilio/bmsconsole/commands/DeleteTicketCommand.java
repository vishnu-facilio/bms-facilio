package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, getRecords(module, ids));
			int rowsUpdated = 0;
			if(recordIds != null && !recordIds.isEmpty()) {
				rowsUpdated += TicketAPI.deleteTickets(module, recordIds);
			}
			if (dependentIds != null && !dependentIds.isEmpty()) {
				rowsUpdated += TicketAPI.deleteTickets(module, dependentIds, 2);
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
}
