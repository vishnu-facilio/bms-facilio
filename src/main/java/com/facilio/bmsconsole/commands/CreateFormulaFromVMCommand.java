package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class CreateFormulaFromVMCommand extends FacilioCommand {
	
	public static final Pattern vmFormulaPattern = Pattern.compile("(\\d+)([+-])*");
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyMeterContext meter = (EnergyMeterContext) context.get(FacilioConstants.ContextNames.RECORD);	
		if(meter != null && meter.getChildMeterExpression() != null && meter.isVirtual()) {
			String expression = meter.getChildMeterExpression();
			if(expression == null || expression.isEmpty()) {
				throw new IllegalArgumentException("VM Formula expression cannot be null while adding virtual meter");
			}
			expression = expression.trim();
			if(!Pattern.matches("^[0-9-+()\\s]+$", expression)) {
				throw new IllegalArgumentException("Invalid VM Formula expression during addition of virtual meter");
			}
			
			boolean calculateVmThroughFormula = false;
			Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA);
			if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
				String calculateVmThroughFormulaProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA);
				if (calculateVmThroughFormulaProp != null && !calculateVmThroughFormulaProp.isEmpty() && StringUtils.isNotEmpty(calculateVmThroughFormulaProp)) {
					calculateVmThroughFormula = Boolean.parseBoolean(calculateVmThroughFormulaProp);
				}
			}
			
			if(calculateVmThroughFormula) 
			{	
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField deltaField = modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
				List<FormulaFieldResourceContext> dependentFieldResourceContextList = new ArrayList<FormulaFieldResourceContext>();

				WorkflowContext workflow = new WorkflowContext();
				workflow.addParamater(FormulaFieldAPI.getWorkflowParameter("startTime", "Number"));
				workflow.addParamater(FormulaFieldAPI.getWorkflowParameter("endTime", "Number"));
				workflow.setWorkflowUIMode(WorkflowContext.WorkflowUIMode.GUI);
				
				Matcher formulaMatcher = vmFormulaPattern.matcher(meter.getChildMeterExpression().trim());
				String resultEvaluator = "(";
				char expressionName = 'A';
				
				while (formulaMatcher.find()) {
					
					long parentMeterId = Long.parseLong(formulaMatcher.group(1));
					String operator = formulaMatcher.group(2);

					FormulaFieldResourceContext dependentFieldResourceContext = new FormulaFieldResourceContext();
					dependentFieldResourceContext.setFieldId(deltaField.getFieldId());
					dependentFieldResourceContext.setResourceId(parentMeterId);
					dependentFieldResourceContextList.add(dependentFieldResourceContext);
					
					setVMWorkflowExpression(expressionName, deltaField, parentMeterId, workflow);
					resultEvaluator = resultEvaluator + expressionName;
					if(operator != null) {
						resultEvaluator += operator;
					}
					expressionName++;
				}
				
				resultEvaluator = resultEvaluator + ")";
				workflow.setResultEvaluator(resultEvaluator);
				WorkflowUtil.addWorkflow(workflow);
			
				FormulaFieldContext vmFormula = setVMFormulaFieldContext(meter, deltaField, workflow);

				FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
				FacilioContext addEnpiChainContext = addEnpiChain.getContext();
				addEnpiChainContext.put(FacilioConstants.ContextNames.FORMULA_FIELD, vmFormula);
				addEnpiChainContext.put(FacilioConstants.ContextNames.DEPENDENT_FIELD_RESOURCE_CONTEXT_LIST,dependentFieldResourceContextList);
				addEnpiChainContext.put(FacilioConstants.ContextNames.FORMULA_METRIC, Metric.ENERGY.getMetricId());
				addEnpiChainContext.put(FacilioConstants.ContextNames.FORMULA_UNIT, UnitsUtil.getDisplayUnit((NumberField)deltaField).getUnitId());
				addEnpiChainContext.put(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA, true);
				addEnpiChain.execute();		
			}	
		}
		return false;
	}
	
	public void setVMWorkflowExpression(char expressionName, FacilioField deltaField, long parentMeterId, WorkflowContext workflow) 
	{
		ExpressionContext workflowExpression = new ExpressionContext();
		workflowExpression.setName(String.valueOf(expressionName));
		workflowExpression.setFieldName(deltaField.getName());
		workflowExpression.setModuleName(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		workflowExpression.setAggregateString("lastValue");
		workflowExpression.setOrderByFieldName("ttime");
		workflowExpression.setSortBy("desc");
		workflowExpression.setLimit("1");
		workflowExpression.setConditionSeqVsBaselineId(new HashMap<Integer, Long>());

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentId", ""+parentMeterId, NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("TTIME", "ttime", "${startTime}, ${endTime}", DateOperators.BETWEEN));
		workflowExpression.setCriteria(criteria);
		workflow.addWorkflowExpression(workflowExpression);
	}
	
	public FormulaFieldContext setVMFormulaFieldContext(EnergyMeterContext meter, FacilioField deltaField, WorkflowContext workflow) throws Exception 
	{
		FormulaFieldContext vmFormula = new FormulaFieldContext();
		vmFormula.setName(meter.getName()+" VM");
		vmFormula.setAssetCategoryId(meter.getCategory().getId());
		vmFormula.setSiteId(meter.getSiteId());
		vmFormula.setReadingField(deltaField);
		vmFormula.setModule(deltaField.getModule());
		vmFormula.setWorkflow(workflow);
		vmFormula.setIncludedResources(Collections.singletonList(meter.getId()));
		vmFormula.setResourceType(ResourceType.ASSET_CATEGORY);
		vmFormula.setTriggerType(TriggerType.SCHEDULE);
		vmFormula.setResultDataType(FieldType.DECIMAL);
		vmFormula.setFormulaFieldType(FormulaFieldType.VM);
		
		int deltaInterval = ReadingsAPI.getDataInterval(meter.getId(), deltaField);
		if(deltaInterval == 10) {
			vmFormula.setFrequency(FacilioFrequency.TEN_MINUTES);
		}
		else if(deltaInterval == 15){
			vmFormula.setFrequency(FacilioFrequency.FIFTEEN_MINUTES);
		}
		vmFormula.setInterval(deltaInterval);
		return vmFormula;
	}
}

