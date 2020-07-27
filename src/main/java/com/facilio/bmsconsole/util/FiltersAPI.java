package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;


public class FiltersAPI {
	
	private static Logger logger = Logger.getLogger(FiltersAPI.class.getName());
	
	public static CustomFilterContext addCustomFilter(Criteria filterCriteria, CustomFilterContext customFilter) throws Exception
	{
		if (customFilter != null && (filterCriteria != null || customFilter.getCriteria() != null)) {
			Long criteriaId = -1l; 
			if (filterCriteria != null) {
				criteriaId = CriteriaAPI.addCriteria(filterCriteria, AccountUtil.getCurrentOrg().getId());
			}
			else if (customFilter.getCriteria() != null) {
				criteriaId = CriteriaAPI.addCriteria(customFilter.getCriteria(), AccountUtil.getCurrentOrg().getId());
			}
			customFilter.setCriteriaId(criteriaId);
			customFilter.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getCustomFiltersModule().getTableName())
					.fields(FieldFactory.getCustomFilterFields());
			
			Map<String, Object> props = FieldUtil.getAsProperties(customFilter);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			customFilter.setId((Long) props.get("id"));
			customFilter.setCriteria(CriteriaAPI.getCriteria(criteriaId));
			
		}
		return customFilter;
	}
	
	public static CustomFilterContext updateCustomFilter(Criteria filterCriteria, CustomFilterContext customFilter) throws Exception
	{
		if (customFilter != null && (filterCriteria != null || customFilter.getCriteria() != null)) {
			long oldCriteriaId = customFilter.getCriteriaId();
			Long criteriaId = -1l; 
			if (customFilter.getCriteria() != null) {
				criteriaId = CriteriaAPI.addCriteria(customFilter.getCriteria(), AccountUtil.getCurrentOrg().getId());
			}
			else if (filterCriteria != null) {
				criteriaId = CriteriaAPI.addCriteria(filterCriteria, AccountUtil.getCurrentOrg().getId());
			}
			customFilter.setCriteriaId(criteriaId);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getCustomFiltersModule().getTableName())
					.fields(FieldFactory.getCustomFilterFields())
					.andCondition(CriteriaAPI.getIdCondition(customFilter.getId(), ModuleFactory.getCustomFiltersModule()));

			Map<String, Object> props = FieldUtil.getAsProperties(customFilter);
			updateBuilder.update(props);
			
			customFilter.setId((Long) props.get("id"));
			customFilter.setCriteria(CriteriaAPI.getCriteria(criteriaId));
			
			CriteriaAPI.deleteCriteria(oldCriteriaId);
			
		}
		return customFilter;
	}
	
	public static CustomFilterContext getCustomFilter(Long filterId) throws Exception
	{
		CustomFilterContext customFilter = new CustomFilterContext();
		if (filterId > 0) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = ModuleFactory.getCustomFiltersModule();
			
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
	                .table(module.getTableName())
	                .select(FieldFactory.getCustomFilterFields())
	                .andCondition(CriteriaAPI.getIdCondition(filterId,module));
	        Map<String, Object> map = selectRecordBuilder.fetchFirst();
	        
	         customFilter = FieldUtil.getAsBeanFromMap(map, CustomFilterContext.class);
	        			
				if (customFilter.getCriteriaId() > 0) {
					Criteria customFilterCriteria = CriteriaAPI.getCriteria(customFilter.getCriteriaId());
					customFilter.setCriteria(customFilterCriteria);
				}
			
		}
		
		return customFilter;
	}
	
	public static List<CustomFilterContext> getCustomFiltersList(Long viewId) throws Exception
	{
		List<CustomFilterContext> customFilters = new ArrayList<>();
		if (viewId > -1) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getCustomFilterFields())
					.table(ModuleFactory.getCustomFiltersModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("VIEWID","viewId",String.valueOf(viewId), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				
				for(Map<String, Object> prop : props) {
					CustomFilterContext customFilter = FieldUtil.getAsBeanFromMap(prop, CustomFilterContext.class);
					customFilters.add(customFilter);
				}
			}
		}
		
		return customFilters;
	}

	
	public static void deleteCustomFilter(CustomFilterContext customFilter) throws Exception
	{
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getCustomFiltersModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(customFilter.getId(), ModuleFactory.getCustomFiltersModule()));
		
		deleteRecordBuilder.delete();
		
		CriteriaAPI.deleteCriteria(customFilter.getCriteriaId());
		
	}
	
	public static void deleteAllCustomFilter(Long viewId) throws Exception
	{
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getCustomFilterFields());
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getCustomFiltersModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("viewId"), String.valueOf(viewId), NumberOperators.EQUALS));
		
		deleteRecordBuilder.delete();
	}
}
