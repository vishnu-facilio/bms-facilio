package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.agentv2.AgentConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.kafka.common.protocol.types.Field;
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
import com.facilio.modules.fields.FacilioField;
import org.mockito.internal.matchers.Null;

public class GetControlActionCommandsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

		boolean fetchCount = context.containsKey(FacilioConstants.ContextNames.FETCH_COUNT);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule commandModule = moduleBean.getModule(moduleName);
		List<FacilioField>fields = moduleBean.getAllFields(moduleName);

		SelectRecordsBuilder builder = new SelectRecordsBuilder<>();
		builder.beanClass(ControlActionCommandContext.class)
				.module(commandModule)
				.select(fields);

		if (filterCriteria != null && !filterCriteria.isEmpty()){
			builder.andCriteria(filterCriteria);
		}

		if (fetchCount){

			builder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(commandModule))
					.select(new ArrayList<>());
			List<Map<String, Object>> result = builder.getAsProps();
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS_COUNT,(long) result.get(0).get(AgentConstants.ID));
			return false;
		}
		else {
			String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING);
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

			List<ControlActionCommandContext>props = builder.get();

			if(!fetchCount) {
				for(ControlActionCommandContext prop :props) {
					FacilioField field = moduleBean.getField(prop.getFieldId());
					prop.setField(field);
					prop.setResource(ResourceAPI.getResource(prop.getResource().getId()));
					if(prop.getExecutedBy() != null && prop.getExecutedBy().getId() > 0) {
						prop.setExecutedBy(AccountUtil.getUserBean().getUser(prop.getExecutedBy().getId(), true));
					}
				}

				context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, props);
			}
		}
    	
		return false;
	}

}
