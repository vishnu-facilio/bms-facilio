package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMCommand implements Command {

	private static Logger log = LogManager.getLogger(ExecutePMCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(pmId != null && pmId != -1) {
			
			PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(pmId);
			Boolean stopExecution = (Boolean) context.get(FacilioConstants.ContextNames.STOP_PM_EXECUTION);
			if (stopExecution == null || !stopExecution) {
				WorkOrderContext wo = null;
				try {
					if (pm.getTriggerTypeEnum() != TriggerType.FLOATING) {
						wo = getPreviousUnclosed(pm);
						if (wo == null) {
							wo = executePM(pm, (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID));
						}
						else {
							addComment(wo, pm, (String) context.get(FacilioConstants.ContextNames.PM_UNCLOSED_WO_COMMENT));
						}
					}
					else {
						wo = executePM(pm, (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID));
					}
				}
				catch (Exception e) {
					log.info("Exception occurred ", e);
					CommonCommandUtil.emailException("ExecutePMCommand", "PM Execution failed for PM : "+pmId, e, "You have to manually add Job entry for next PM Job because exception is thrown to rollback transaction");
					throw e;
				}
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			}
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		}
		return false;
	}
	
	private void addComment(WorkOrderContext wo, PreventiveMaintenance pm, String msg) throws Exception {
		NoteContext note = new NoteContext();
		note.setParentId(wo.getId());
		
		if (msg != null && !msg.isEmpty()) {
			note.setBody(msg);
		}
		else {
			note.setBody("Floating Planned Maintenance associated with this WorkOrder is triggered and since this isn't closed yet, new WorkOrder isn't created");
		}
		
		FacilioContext noteContext = new FacilioContext();
		noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
		noteContext.put(FacilioConstants.ContextNames.TICKET_MODULE, FacilioConstants.ContextNames.WORK_ORDER);
		noteContext.put(FacilioConstants.ContextNames.NOTE, note);

		Chain addNote = TransactionChainFactory.getAddNotesChain();
		addNote.execute(noteContext);
	}
	
	private WorkOrderContext getPreviousUnclosed (PreventiveMaintenance pm) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioField pmField = FieldFactory.getAsMap(fields).get("pm");
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
															.select(fields)
															.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
															.andCondition(CriteriaAPI.getCondition(pmField, String.valueOf(pm.getId()), PickListOperators.IS))
															.orderBy("CREATED_TIME desc")
															.limit(1)
															;
		
		List<WorkOrderContext> wos = builder.get();
		if (wos != null && !wos.isEmpty()) {
			TicketStatusContext closedStatus = TicketAPI.getStatus("Closed");
			WorkOrderContext wo = wos.get(1);
			if (wo.getStatus().getId() != closedStatus.getId()) {
				return wo;
			}
		}
		return null;													
	}
	
	private WorkOrderContext executePM (PreventiveMaintenance pm, Long templateId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		if (templateId == null) {
			return bean.addWorkOrderFromPM(pm);
		}
		else {
			return bean.addWorkOrderFromPM(pm, templateId);
		}
	}
}
