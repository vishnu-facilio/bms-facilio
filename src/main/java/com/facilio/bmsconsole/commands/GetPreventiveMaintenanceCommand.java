package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class GetPreventiveMaintenanceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Criteria criteria = new Criteria();
		
		String count = (String) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		JSONObject serachQuery = (JSONObject) context.get(FacilioConstants.ContextNames.SEARCH);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		String siteFilterValues = (String) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_SITE_FILTER_VALUES);
		String resourceFilterValues = (String) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCE_FILTER_VALUES);
		
		Boolean isFromView = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.IS_FROM_VIEW, Boolean.FALSE);
		
		if (filterCriteria != null) {
			criteria.andCriteria(filterCriteria);
		}
		if (( filters == null || includeParentCriteria) && view != null) {
			Criteria viewCriteria = view.getCriteria();
			if (viewCriteria != null) {
				criteria.andCriteria(viewCriteria);
			}
		}
		String query = null;
		if (( serachQuery != null)) {
//			Criteria searchCriteria = new Criteria();
			query = (String) serachQuery.get("query");
//			List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
//			FacilioField pmSubjectField = FieldFactory.getAsMap(fields).get("title");
//			searchCriteria.addAndCondition(CriteriaAPI.getCondition(pmSubjectField, query, StringOperators.CONTAINS));
//			if (criteria == null) {
//				criteria = new Criteria();
//			}
//			criteria.andCriteria(searchCriteria);
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("planned");
		if(scopeCriteria != null) {
			//criteria.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("planned","read");
		if(permissionCriteria != null) {
			criteria.andCriteria(permissionCriteria);
		}

		Criteria excludeCriteria = PreventiveMaintenanceAPI.getPMExcludeCriteria();
		criteria.andCriteria(excludeCriteria);
		
		List<Long> idsToSelect = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

		Boolean isUserTrigger = (Boolean) context.get(FacilioConstants.ContextNames.IS_USER_TRIGGER);
		if (isUserTrigger != null && isUserTrigger) {
			Criteria userTriggerCriteria = PreventiveMaintenanceAPI.getUserTriggerCriteria();
			if (userTriggerCriteria != null) {
				criteria.andCriteria(userTriggerCriteria);
			} else {
				List<PreventiveMaintenance> pms = Collections.emptyList();
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT, 0);
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
				return false;
			}
		}

		boolean fetchDependencies = true;
		if(isFromView != null && isFromView) {
			fetchDependencies = false;
		}
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMs(idsToSelect, criteria, query, pagination,null, siteFilterValues,resourceFilterValues, fetchDependencies, false, true);
		if (pms != null) {
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
	
		if (count != null) {
			long pmCount = -1;
			if (pms != null && !pms.isEmpty()) {
				pmCount = pms.size();
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT, pmCount);
			}
			else {
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_COUNT, pmCount);
			}
		}
		else {
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
		return false;
	}

}
