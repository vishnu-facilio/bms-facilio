package com.facilio.bmsconsoleV3.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;

import lombok.extern.log4j.Log4j;

@Log4j
public class ScheduleWorkordersToMoveInQueueForPreOpenToOpen extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	List<V3WorkOrderContext> workOrders = (List<V3WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
    	
    	if (CollectionUtils.isNotEmpty(workOrders)) {
			for (V3WorkOrderContext workOrder: workOrders) {
				try {
					FacilioTimer.deleteJob(workOrder.getId(), "OpenScheduleWOV2");
					FacilioTimer.scheduleOneTimeJobWithTimestampInSec(workOrder.getId(), "OpenScheduleWOV2", workOrder.getCreatedTime() / 1000, "priority");
					
				} catch (Exception e) {
					CommonCommandUtil.emailException("ScheduleWOStatusChange", "handlePMV2Scheduling() | workOrder= " + workOrder, e);
					LOGGER.error("PM Execution failed in handlePMV2Scheduling():", e);
				}
			}
		}
    	
    	updateV3JobStatus(workOrders);
    	
		return false;
	}
    
    private void updateV3JobStatus(List<V3WorkOrderContext> wos) throws Exception {
		LOGGER.info("updateV3JobStatus():");
		if (wos == null || wos.isEmpty()) {
			return;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<Long> woIds = wos.stream().map(V3WorkOrderContext::getId).collect(Collectors.toList());
		V3WorkOrderContext wo = new V3WorkOrderContext();
		wo.setJobStatus(V3WorkOrderContext.JobsStatus.SCHEDULED.getValue());
		
		UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<V3WorkOrderContext>()
				.fields(Arrays.asList(fieldMap.get("jobStatus")))
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(woIds, module))
				;
		
		updateRecordBuilder.update(wo);
	}

}
