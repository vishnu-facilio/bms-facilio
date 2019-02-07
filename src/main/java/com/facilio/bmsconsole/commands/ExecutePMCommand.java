package com.facilio.bmsconsole.commands;

import java.util.Collections;
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
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
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
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(ExecutePMCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(pm != null) {
			LOGGER.debug("Executing pm : "+pm.getId());
			Boolean stopExecution = (Boolean) context.get(FacilioConstants.ContextNames.STOP_PM_EXECUTION);
			if (stopExecution == null || !stopExecution) {
				WorkOrderContext wo = null;
				try {
					if (pm.getTriggerTypeEnum() == TriggerType.FLOATING) {
						wo = getPreviousUnclosed(pm);
						if (wo == null) {
							List<WorkOrderContext> wos = executePM(context, pm, (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID));
							wo = wos.get(0);
						}
						else {
							addComment(wo, pm, (String) context.get(FacilioConstants.ContextNames.PM_UNCLOSED_WO_COMMENT));
						}
					}
					else {
						List<WorkOrderContext> wos = executePM(context, pm, (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID));
						wo = wos.get(0);
					}
				}
				catch (Exception e) {
					LOGGER.error("PM Execution failed for PM : "+pm.getId(), e);
					CommonCommandUtil.emailException("ExecutePMCommand", "PM Execution failed for PM : "+pm.getId(), e, "You have to manually add Job entry for next PM Job because exception is thrown to rollback transaction");
					throw e;
				}
				if(wo != null) {
					context.put(FacilioConstants.ContextNames.PM_TO_WO, Collections.singletonMap(pm.getId(), wo));
				}
				
				PMJobsContext currentJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
				if (currentJob != null) {
					PreventiveMaintenanceAPI.updatePMJobStatus(currentJob.getId(), PMJobsStatus.COMPLETED);
				}
			}
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
															.beanClass(WorkOrderContext.class)
															.limit(1)
															;
		
		List<WorkOrderContext> wos = builder.get();
		if (wos != null && !wos.isEmpty()) {
			TicketStatusContext closedStatus = TicketAPI.getStatus("Closed");
			WorkOrderContext wo = wos.get(0);
			if (wo.getStatus() != null && wo.getStatus().getId() != closedStatus.getId()) {
				return wo;
			}
		}
		return null;													
	}
	
	private List<WorkOrderContext> executePM (Context context, PreventiveMaintenance pm, Long templateId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		if (templateId == null) {
			return bean.addWorkOrderFromPM(context, pm);
		}
		else {
			return bean.addWorkOrderFromPM(context, pm, templateId);
		}
	}
}
