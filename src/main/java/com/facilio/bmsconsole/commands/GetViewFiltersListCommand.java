package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetViewFiltersListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		if (viewId > -1) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getViewFiltersFields())
					.table(ModuleFactory.getViewFiltersModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("VIEWID","viewId",String.valueOf(viewId), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				
				List<ViewFilterContext> viewFilters = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					ViewFilterContext viewFilter = FieldUtil.getAsBeanFromMap(prop, ViewFilterContext.class);
					viewFilters.add(viewFilter);
				}
				
				List<Long> criteriaIds = viewFilters.stream().map(a -> a.getCriteriaId()).collect(Collectors.toList());
				Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
				context.put(FacilioConstants.ContextNames.CRITERIA_MAP, criteriaMap);
				context.put(FacilioConstants.ContextNames.VIEW_FILTERS_LIST, viewFilters);
			}
		}
		return false;
	}

	

}
