package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddWorkOrderCommand implements Command {
	
	private static final Logger LOGGER = Logger.getLogger(AddWorkOrderCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(workOrder != null) {
			if(workOrder.getRequester() == null || workOrder.getRequester().getId() == -1)
			{
				workOrder.setRequester(null);
			}
			
			TicketAPI.validateSiteSpecificData(workOrder);
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			workOrder.setCreatedBy(AccountUtil.getCurrentUser());
			if (workOrder.getScheduledStart() > 0) {
				workOrder.setCreatedTime(workOrder.getScheduledStart());
			} else {
				workOrder.setCreatedTime(System.currentTimeMillis());
			}

			workOrder.setModifiedTime(workOrder.getCreatedTime());
			workOrder.setScheduledStart(workOrder.getCreatedTime());
			workOrder.setEstimatedStart(workOrder.getCreatedTime());
			workOrder.setApprovalState(ApprovalState.YET_TO_BE_REQUESTED);
			
			if (workOrder.getPriority() == null) {
				workOrder.setPriority(TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "Low"));
			}
			
			if(workOrder.getDuration() != -1) {
				workOrder.setDueDate(workOrder.getCreatedTime()+(workOrder.getDuration()*1000));
			}
			workOrder.setEstimatedEnd(workOrder.getDueDate());
			
			TicketAPI.updateTicketAssignedBy(workOrder);
			TicketAPI.updateTicketStatus(workOrder);
			
			InsertRecordBuilder<WorkOrderContext> builder = new InsertRecordBuilder<WorkOrderContext>()
																.moduleName(moduleName)
																.fields(fields)
																.withChangeSet()
																.withLocalId()
																;
			
			Integer insertLevel = (Integer) context.get(FacilioConstants.ContextNames.INSERT_LEVEL);
			if(insertLevel != null) {
				builder.level(insertLevel);
			}
			
			long workOrderId = builder.insert(workOrder);
			workOrder.setId(workOrderId);
			
			if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
				LOGGER.info("Added WO with id : "+workOrderId);
			}
			
			if(context.get(FacilioConstants.ContextNames.EVENT_TYPE) == null) {
				List<EventType> activities = new ArrayList<>();
				activities.add(EventType.CREATE);
				
				//TODO remove single ACTIVITY_TYPE once handled in TicketActivity
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
				
				String status = workOrder.getStatus().getStatus();
				if (status != null && status.equals("Assigned")) {
					activities.add(EventType.ASSIGN_TICKET);
				}
				context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);
			}
			context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, builder.getChangeSet()));
			context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(workOrder)));
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workOrderId));
			JSONObject info = new JSONObject();
			info.put("actype", "add");
			info.put("WorkOrder", workOrder.getSubject());
			JSONObject newinfo = new JSONObject();
			newinfo.put("WorkOrder",info);
			CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD, newinfo, (FacilioContext) context);

		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}
}
