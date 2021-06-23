package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetControlActionCommandsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
		
    	
    	ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields = null;
		if (fetchCount != null && fetchCount) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT("+controlActionModule.getTableName()+".ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		}
		
		SelectRecordsBuilder<ControlActionCommandContext> builder = new SelectRecordsBuilder<ControlActionCommandContext>()
				.module(controlActionModule)
				.select(fields)
				.beanClass(ControlActionCommandContext.class)
				.orderBy("EXECUTED_TIME desc");
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				builder.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(controlActionModule.getName());
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		
		Long entityId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ENTITY_ID);
		if(entityId != null && entityId != -1)
		{
			builder.andCustomWhere("Alarms.ENTITY_ID = ?", entityId);
		}
		
		List<ControlActionCommandContext> props = builder.get();
		
		if(fetchCount == null || !fetchCount) {
			for(ControlActionCommandContext prop :props) {
				FacilioField field = modbean.getField(prop.getFieldId());
				prop.setField(field);
				prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
				if(prop.getExecutedBy() != null && prop.getExecutedBy().getId() > 0) {
					prop.setExecutedBy(AccountUtil.getUserBean().getUser(prop.getExecutedBy().getId(), true));
				}
			}
			
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, props);
		}
		else {
			
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT, props.get(0).getData().get("count"));
				
		}
		
		
    	
		return false;
	}

}
