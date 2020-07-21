package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class updateViewFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		ViewFilterContext viewFilter = (ViewFilterContext) context.get(FacilioConstants.ContextNames.VIEW_FILTER);
		
		
		long oldCriteriaId = viewFilter.getCriteriaId();
		long criteriaId = CriteriaAPI.addCriteria(filterCriteria, AccountUtil.getCurrentOrg().getId());
		viewFilter.setCriteriaId(criteriaId);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getViewFiltersModule().getTableName())
				.fields(FieldFactory.getViewFiltersFields())
				.andCondition(CriteriaAPI.getIdCondition(viewFilter.getId(), ModuleFactory.getViewFiltersModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(viewFilter);
		updateBuilder.update(props);
		
		viewFilter.setId((Long) props.get("id"));
		
		
		CriteriaAPI.deleteCriteria(oldCriteriaId);
		
		context.put(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT, viewFilter);
		
		return false;
	}

}
