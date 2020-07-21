package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddViewFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		ViewFilterContext viewFilter = (ViewFilterContext) context.get(FacilioConstants.ContextNames.VIEW_FILTER);
		
		long id = CriteriaAPI.addCriteria(filterCriteria, AccountUtil.getCurrentOrg().getId());
		viewFilter.setCriteriaId(id);
		viewFilter.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getViewFiltersModule().getTableName())
				.fields(FieldFactory.getViewFiltersFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(viewFilter);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		viewFilter.setId((Long) props.get("id"));
		System.out.println("123" + viewFilter);
		
		context.put(FacilioConstants.ContextNames.VIEW_FILTER_CONTEXT, viewFilter);
		return false;
	}

	

}
