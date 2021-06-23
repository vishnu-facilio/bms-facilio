package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchControlGroupsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	FacilioModule controlGroupModule = ModuleFactory.getControlGroupModule();
		
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		
		fetchCount = fetchCount != null ? fetchCount : Boolean.FALSE;
		
		List<FacilioField> fields = null;
		if (fetchCount != null && fetchCount) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT("+controlGroupModule.getTableName()+".ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = FieldFactory.getControlGroupFields();
		}
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(controlGroupModule.getTableName());
		
		ruleBuilder.select(fields);
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			ruleBuilder.andCriteria(filterCriteria);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			ruleBuilder.andCriteria(view.getCriteria());
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			ruleBuilder.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				ruleBuilder.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(controlGroupModule.getName());
		if(scopeCriteria != null)
		{
			ruleBuilder.andCriteria(scopeCriteria);
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			ruleBuilder.orderBy(orderBy);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			ruleBuilder.offset(offset);
			ruleBuilder.limit(perPage);
		}
		
		ruleBuilder.andCondition(CriteriaAPI.getCondition("IS_DELETED", "isDeleted", Boolean.FALSE.toString(), BooleanOperators.IS));
		
		List<Map<String, Object>> props = ruleBuilder.get();
		
		if(fetchCount == null || !fetchCount) {
			
			List<ControlGroupContext> controlGroupContexts = new ArrayList<ControlGroupContext>();
			for(Map<String, Object> prop :props) {
				ControlGroupContext controlGroupContext = FieldUtil.getAsBeanFromMap(prop, ControlGroupContext.class);
				controlGroupContext.setAssetCategoryContext(AssetsAPI.getCategoryForAsset(controlGroupContext.getAssetCategoryId()));
				controlGroupContext.setField(modBean.getField(controlGroupContext.getFieldId()));
				controlGroupContexts.add(controlGroupContext);
			}
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS, controlGroupContexts);
		}
		else {
			context.put(ControlActionUtil.CONTROL_ACTION_GROUP_COUNT, props.get(0).get("count"));
		}
		
		return false;
	}

}
