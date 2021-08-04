package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.commands.Criteria;
import com.facilio.bmsconsole.commands.FacilioField;
import com.facilio.bmsconsole.commands.ModuleBean;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.Condition;
import com.facilio.bmsconsole.util.GenericSelectRecordBuilder;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext.FloorPlanType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.report.context.FacilioModule;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.commands.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.db.criteria.operators.StringOperators;



public class FloorplanDetailsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = context.get(FacilioConstants.ContextNames.MODULE);
		String search = context.get(FacilioConstants.ContextNames.SEARCH);
		long floorId =  (long) context.get(FacilioConstants.ContextNames.FLOOR);
		Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
		List<String> modules = new ArrayList<>();
		ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		if(moduleName != null && StringUtils.isNotEmpty((String) moduleName)) {
			modules.add(moduleName);
		} else {
			modules.add(FacilioConstants.ContextNames.EMPLOYEE);
			modules.add(FacilioConstants.ContextNames.Floorplan.DESKS);
			modules.add(FacilioConstants.ContextNames.SPACE);
			modules.add(FacilioConstants.ContextNames.LOCKERS);
			modules.add(FacilioConstants.ContextNames.PARKING_STALL);
		}
		
		for(String module : modules) {
			FacilioModule moduleObj = modBean.getModule(module);
			Map<String, FacilioField> fields = FieldFactory.getAsMap(modBean.getAllFields(module));
			Criteria searchCriteria = constructMainFieldSearchCriteria(moduleName, (String) search);
			Criteria criteria = new Criteria();
			List<SupplementRecord> supplementFields = new ArrayList<>();
			if(!module.equalsIgnoreCase(FacilioConstants.ContextNames.EMPLOYEE) && floorId > 0) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("floor"), String.valueOf(floorId) , NumberOperators.EQUALS));
				supplementFields.add((SupplementRecord) fields.get("department"));
				supplementFields.add((SupplementRecord) fields.get("space"));
			}
			if(!module.equalsIgnoreCase(FacilioConstants.ContextNames.SPACE) && floorId > 0) {
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule spaceCategorymodule  = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
				List<FacilioField> spaceCategoryFields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);
				Criteria spaceCriteria = new Criteria();

				SelectRecordsBuilder<SpaceCategoryContext> builder = new SelectRecordsBuilder<SpaceCategoryContext>()
						.module(spaceCategorymodule)
						.beanClass(SpaceCategoryContext.class)
						.select(spaceCategoryFields);
				spaceCriteria
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.Floorplan.DESKS, StringOperators.IS))
					.orCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.LOCKERS, StringOperators.IS))
					.orCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.PARKING_STALL, StringOperators.IS));
				
				builder.andCriteria(spaceCriteria);
				List<SpaceCategoryContext> spaceCategories = builder.get();
				List<Long> categoryIds = new ArrayList<>();
				spaceCategories.forEach(i -> categoryIds.add(i.getId()));
				
				criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("SpaceCategory"), StringUtils.join(categoryIds,",") , NumberOperators.EQUALS));
			}
			
			V3Config v3Config = ChainUtil.getV3Config(module);
            Class beanClass = ChainUtil.getBeanClass(v3Config, moduleObj);
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
	                .module(moduleObj)
	                .beanClass(beanClass);
			
			if (searchCriteria != null) {
	            selectRecordsBuilder.andCriteria(searchCriteria);
	        }

	        if (criteria != null) {
	            selectRecordsBuilder.andCriteria(criteria);
	        }
	        
	        if (filterCriteria != null) {
	            selectRecordsBuilder.andCriteria(filterCriteria);
	        }
	        
	        if (CollectionUtils.isNotEmpty(supplementFields)) {
	            selectRecordsBuilder.fetchSupplements(supplementFields);
	        }

	        selectRecordsBuilder.select(modBean.getAllFields(module));
	        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();
	        context.put(moduleName, records);
			
		}

		return false;
	}
	
	private Criteria constructMainFieldSearchCriteria(String moduleName, String search) throws Exception {
		if (search != null && search instanceof String && StringUtils.isNotEmpty((String) search)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			FacilioField primaryField = modBean.getPrimaryField(moduleName);
			Criteria criteria = null;
			if (primaryField instanceof LookupField) {
				// Not handling special lookup here. If a module as special lookup as main field, I think it has to be searched locally like special lookup modules
				LookupField lookupField = (LookupField) primaryField;
				if (StringUtils.isEmpty(lookupField.getSpecialType())) {
					FacilioField lookupFieldPrimary = modBean.getPrimaryField(lookupField.getLookupModule().getName());
					Criteria lookupCriteria = constructMainFieldSearchCriteria(lookupFieldPrimary, search, modBean);
					criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(primaryField, lookupCriteria, LookupOperator.LOOKUP));
				}
			}
			else {
				criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition(primaryField, (String) search, StringOperators.CONTAINS));
			}
		return criteria;
		}
	}
	
}