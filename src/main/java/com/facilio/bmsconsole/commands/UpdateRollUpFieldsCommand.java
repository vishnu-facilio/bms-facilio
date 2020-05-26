package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class UpdateRollUpFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		Long rollUpFieldId = (Long) context.get(FacilioConstants.ContextNames.ROLL_UP_FIELD_IDS);
		Long childModuleId = (Long) context.get(FacilioConstants.ContextNames.CHILD_MODULE_ID);
		Long childFieldId = (Long) context.get(FacilioConstants.ContextNames.CHILD_FIELD_ID);
		Long aggregateFieldId = (Long) context.get(FacilioConstants.ContextNames.AGGREGATE_FIELD_ID);
		Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CHILD_CRITERIA);
		Integer aggregateFunctionId = (Integer) context.get(FacilioConstants.ContextNames.AGGREGATE_FUNCTION_ID);
		
		List<RollUpField> rollUpFields = RollUpFieldUtil.getRollUpFieldsByIds(Collections.singletonList(rollUpFieldId), true);
		if(rollUpFields == null || rollUpFields.isEmpty()) {
			throw new IllegalArgumentException("Invalid rollup field id for configuration.");
		}
		
		RollUpField rollUpField = rollUpFields.get(0);
		if(!isAllowedRollUpAggregationType(aggregateFunctionId)) {
			throw new IllegalArgumentException("Please provide a valid rollup type in aggregation.");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = rollUpField.getParentModule();
		List<RollUpField> rollUpFieldContexts = new ArrayList<RollUpField>();
		
		if(childFieldId == null) 
		{
			FacilioModule childModule = modBean.getModule(childModuleId);
			if(childModule == null) {
				throw new IllegalArgumentException("Please provide a valid child Module in the configuration.");
			}
			
			List<FacilioField> childFields = modBean.getAllFields(childModule.getName());
			List<FacilioField> childLookUpFields = new ArrayList<FacilioField>();
			if(!childModule.getTypeEnum().isReadingType()) {
				childLookUpFields = getChildLookUpFields(childFields, parentModule);
			}
			
			if(childLookUpFields == null || childLookUpFields.isEmpty()) {
				throw new IllegalArgumentException("No lookup fields in "+ childModule.getDisplayName() +" module, pointing to the rollup module "+parentModule.getDisplayName()+ ".");
			}
			if(childLookUpFields != null && childLookUpFields.size() > 1) {
				throw new IllegalArgumentException("Multiple lookup fields in "+ childModule.getDisplayName() +" module, pointing to the rollup module "+parentModule.getDisplayName()+ ".");
			}
			childFieldId = childLookUpFields.get(0).getFieldId();			
		}
		
		aggregateFieldId = (aggregateFieldId == null) ? childFieldId : aggregateFieldId;
		rollUpField.setChildModuleId(childModuleId);
		rollUpField.setChildFieldId(childFieldId);
		rollUpField.setAggregateFunctionId(aggregateFunctionId);
		rollUpField.setAggregateFieldId(aggregateFieldId);
		
		if(criteria != null) {
			long childCriteriaId = CriteriaAPI.addCriteria(criteria);
			rollUpField.setChildCriteriaId(childCriteriaId);
		}
		
		context.put(FacilioConstants.ContextNames.ROLL_UP_FIELDS, rollUpFields);
		RollUpFieldUtil.updateRollUpField(rollUpField);		
		return false;
	}
	
	private boolean isAllowedRollUpAggregationType(int aggregateFunctionId){
		List<Integer> allowedAggregationTypes = new ArrayList<Integer>();
		allowedAggregationTypes.add(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
		allowedAggregationTypes.add(BmsAggregateOperators.NumberAggregateOperator.MAX.getValue());
		allowedAggregationTypes.add(BmsAggregateOperators.NumberAggregateOperator.MIN.getValue());
		allowedAggregationTypes.add(BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
		allowedAggregationTypes.add(BmsAggregateOperators.NumberAggregateOperator.AVERAGE.getValue());
		return allowedAggregationTypes.contains(aggregateFunctionId);
	}
	
	private List<FacilioField> getChildLookUpFields(List<FacilioField> childFields, FacilioModule parentModule){
		List<FacilioField> childLookUpFields = new ArrayList<FacilioField>();
		for(FacilioField childField: childFields) 
		{
			if(childField instanceof LookupField) 
			{
				if(((LookupField) childField).getLookupModule() != null && ((LookupField) childField).getLookupModule().equals(parentModule)){
					childLookUpFields.add(childField);	
				}
			}				
		}
		return childLookUpFields;		
	}
	
	private List<FacilioField> getSubModuleReadingFields(List<FacilioField> childFields, FacilioModule parentModule) throws Exception{
		List<FacilioField> childLookUpFields = new ArrayList<FacilioField>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules = modBean.getSubModules(parentModule.getModuleId());
		for(FacilioField childField: childFields) 
		{	
			if(subModules.contains(childField.getModule())) {
				childLookUpFields.add(childField);	
			}					
		}
		return childLookUpFields;		
	}

}
