package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.RoleFactory;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyResponseContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class fetchWorkOrderSurveyCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		long workOrderId = (long) context.getOrDefault(FacilioConstants.ContextNames.WORKORDER_ID,-1L);
		FacilioUtil.throwIllegalArgumentException(workOrderId <=0 ,"WorkOrder Id should be greater than zero.");

		ModuleBean bean = Constants.getModBean();
		FacilioModule module = bean.getModule(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
		List<FacilioField> fields  = bean.getAllFields(module.getName());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<WorkOrderSurveyResponseContext> builder = new SelectRecordsBuilder<WorkOrderSurveyResponseContext>()
				.select(fields)
				.moduleName(module.getName())
				.beanClass(WorkOrderSurveyResponseContext.class)
				.andCondition(CriteriaAPI.getCondition(module.getTableName()+".PARENT_ID",module.getTableName()+".parentId",String.valueOf(workOrderId), StringOperators.IS));
				if(!AccountUtil.getCurrentUser().isSuperAdmin()){
					builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assignedTo"),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()),NumberOperators.EQUALS));
				}


		List<WorkOrderSurveyResponseContext> response = builder.get();

		if(CollectionUtils.isNotEmpty(response)){

			long assignedTo = response.get(0).getAssignedTo().getId();
			long currentUser = Objects.requireNonNull(AccountUtil.getCurrentUser()).getPeopleId();

			context.put("isSurveyAvailable", (assignedTo == currentUser));
			context.put("isSuperAdmin", AccountUtil.getCurrentUser().isSuperAdmin());
			context.put("isViewAllSurvey",  response.size() > 1);
			context.put("response", response);
		}

		return false;
	}
}
