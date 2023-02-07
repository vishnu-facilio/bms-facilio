package com.facilio.qa.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.RoleFactory;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.ResponseContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplySurveyCriteriaCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		Map<String, List<Object>> queryParameters = (Map<String, List<Object>>) context.get(Constants.QUERY_PARAMS);

		Criteria criteria = new Criteria();
		ModuleBean modBean = Constants.getModBean();
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(modBean.getAllFields(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);

		List<Object> params = queryParameters.getOrDefault("workOrderId",null);

		if(CollectionUtils.isNotEmpty(params)) {
			Long workOrderId = Long.parseLong((String) params.get(0));
			if(workOrderId != null && workOrderId > 0L){
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(workOrderId), NumberOperators.EQUALS));
			}
		}

		List<Object> viewSurveyParams = queryParameters.getOrDefault("isViewSurveyResult",null);

		if(CollectionUtils.isNotEmpty(viewSurveyParams)) {
			boolean isViewResultSurvey = Boolean.parseBoolean((String) viewSurveyParams.get(0));
			if(isViewResultSurvey){
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("responseStatus"), String.valueOf(ResponseContext.ResponseStatus.COMPLETED.getIndex()), NumberOperators.EQUALS));
			}
		}

		List<Object> viewAllSurveyParams = queryParameters.getOrDefault("isViewAllSurvey",null);

		if(CollectionUtils.isNotEmpty(viewAllSurveyParams)) {
			boolean isViewAllSurvey = Boolean.parseBoolean((String) viewAllSurveyParams.get(0));
			if(isViewAllSurvey){
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("responseStatus"), String.valueOf(ResponseContext.ResponseStatus.DISABLED.getIndex()), NumberOperators.NOT_EQUALS));
				if(!AccountUtil.getCurrentUser().isSuperAdmin()){
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("assignedTo"),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()),NumberOperators.EQUALS));
				}
			}
		}
		if(!criteria.isEmpty()) {
			context.put(Constants.BEFORE_FETCH_CRITERIA, criteria);
		}
		return false;
	}
}
