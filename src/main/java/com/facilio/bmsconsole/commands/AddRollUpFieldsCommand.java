package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
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

public class AddRollUpFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<FacilioField> rollUpFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long childModuleId = (Long) context.get(FacilioConstants.ContextNames.CHILD_MODULE_ID);
		Integer aggregateFunctionId = (Integer) context.get(FacilioConstants.ContextNames.AGGREGATE_FUNCTION_ID);

		if(childModuleId == null || aggregateFunctionId == null) {
			throw new IllegalArgumentException("Insufficient params for the rollup field configuration.");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = modBean.getModule(moduleName);
		List<RollUpField> rollUpFieldContexts = new ArrayList<RollUpField>();
		for(FacilioField rollUpField:rollUpFields) 
		{
			if(rollUpField.getId() != - 1 && rollUpField.getModuleId() == parentModule.getModuleId()) 
			{	Long childFieldId = (Long) context.get(FacilioConstants.ContextNames.CHILD_FIELD_ID);
				FacilioModule childModule = modBean.getModule(childModuleId);
				if(childModule == null) {
					throw new IllegalArgumentException("Please provide a valid child Module in the configuration.");
				}
				if(childFieldId == null) 
				{	
					List<FacilioField> childFields = modBean.getAllFields(childModule.getName());
					List<FacilioField> childLookUpFields = new ArrayList<FacilioField>();
					if(childModule.getTypeEnum().isReadingType()) {
						throw new IllegalArgumentException("Reading Module is not a valid rollup field configuration.");
						//childLookUpFields = getSubModuleReadingFields(childFields, parentModule);
					}
					else {
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
				if(!isAllowedRollUpAggregationType(aggregateFunctionId)) {
					throw new IllegalArgumentException("Please provide a valid rollup type in aggregation.");
				}
				if(!modBean.getField(childFieldId).getModule().equals(childModule)) {
					throw new IllegalArgumentException("Please provide a valid child field module.");
				}

				Long aggregateFieldId = (Long) context.get(FacilioConstants.ContextNames.AGGREGATE_FIELD_ID);
				aggregateFieldId = (aggregateFieldId == null) ? childFieldId : aggregateFieldId;
				Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CHILD_CRITERIA);
				
				RollUpField rollUpFieldContext = new RollUpField();
				rollUpFieldContext.setChildModuleId(childModuleId);
				rollUpFieldContext.setChildFieldId(childFieldId);
				rollUpFieldContext.setParentModuleId(rollUpField.getModuleId());
				rollUpFieldContext.setParentRollUpFieldId(rollUpField.getId());
				rollUpFieldContext.setAggregateFunctionId(aggregateFunctionId);
				rollUpFieldContext.setAggregateFieldId(aggregateFieldId);
				if(criteria != null) {
					long childCriteriaId = CriteriaAPI.addCriteria(criteria);
					rollUpFieldContext.setChildCriteriaId(childCriteriaId);
				}
				rollUpFieldContexts.add(rollUpFieldContext);
			}
		}
		context.put(FacilioConstants.ContextNames.ROLL_UP_FIELDS, rollUpFieldContexts);
		RollUpFieldUtil.addRollUpField(rollUpFieldContexts);		
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
