package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class FetchPMV2WorkordersToMoveInQueueForPreOpenToOpen extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	
    	Long maxTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
    	
    	FacilioModule module = Constants.getModBean().getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    	
    	SelectRecordsBuilder<V3WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<V3WorkOrderContext>();
		selectRecordsBuilder.select(fields)
				.module(module)
				.beanClass(V3WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(maxTime), NumberOperators.LESS_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmV2"), CommonOperators.IS_NOT_EMPTY))
				.skipModuleCriteria();
		
		List<V3WorkOrderContext> workOrders = selectRecordsBuilder.get();
		
		if (CollectionUtils.isNotEmpty(workOrders)) {
			List<Long> workOrderIds = workOrders.stream().map(V3WorkOrderContext::getId).collect(Collectors.toList());
			
			LOGGER.info("executeCommand() -> workOrderProps size: " + workOrderIds.size() + ". WorkOrder IDs = " + workOrderIds);
		}
		else {
			LOGGER.info("NO WO's available for this endTime -- "+maxTime);
			return true;
		}

		context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
    	
		return false;
	}

}
