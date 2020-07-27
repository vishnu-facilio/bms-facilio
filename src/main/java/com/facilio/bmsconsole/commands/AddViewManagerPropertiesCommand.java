package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.context.QuickFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddViewManagerPropertiesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
//		List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.FIELD_IDS);
		List<QuickFilterContext> quickFilters = (List<QuickFilterContext>) context.get(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT);
		List<CustomFilterContext> customFilters = (List<CustomFilterContext>) context.get(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST);
		if (viewId > 0) {
	
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getQuickFilterModule().getTableName())
					.fields(FieldFactory.getQuickFilterFields());
			
			List<Map<String, Object>> propList = new ArrayList<Map<String,Object>>();
			
			for (QuickFilterContext quickFilter : quickFilters) {
				if (quickFilter.getFieldId() == -1 && StringUtils.isEmpty(quickFilter.getFieldName())) {
					throw new IllegalArgumentException("column field is required");
				}
				Map<String, Object> props = FieldUtil.getAsProperties(quickFilter);
				propList.add(props);
			}
			
//			for (long fieldId : fieldIds) {
//				QuickFilterContext quickFilter = new QuickFilterContext();
//				quickFilter.setFieldId(fieldId);
//				quickFilter.setViewId(viewId);
//				quickFilter.setOrgId(AccountUtil.getCurrentOrg().getId());
//				
//				Map<String, Object> props = FieldUtil.getAsProperties(quickFilter);
//				
//				propList.add(props);
//			}
			
			if(!propList.isEmpty()) {
				insertRecordBuilder.addRecords(propList);
				insertRecordBuilder.save();
			}
			
			
			GenericInsertRecordBuilder customFiltersInsertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getCustomFiltersModule().getTableName())
					.fields(FieldFactory.getCustomFilterFields());
			
			List<Map<String, Object>> customFiltersPropList = new ArrayList<Map<String,Object>>();
			
			for (CustomFilterContext customFilter : customFilters) {
				Long criteriaId = CriteriaAPI.addCriteria(customFilter.getCriteria(), AccountUtil.getCurrentOrg().getId());
				customFilter.setCriteriaId(criteriaId);
				Map<String, Object> props = FieldUtil.getAsProperties(customFilter);
				customFiltersPropList.add(props);
			}
			
			if(!customFiltersPropList.isEmpty()) {
				customFiltersInsertRecordBuilder.addRecords(customFiltersPropList);
				customFiltersInsertRecordBuilder.save();
			}
			
		}
		
		return false;
	}

	

}
