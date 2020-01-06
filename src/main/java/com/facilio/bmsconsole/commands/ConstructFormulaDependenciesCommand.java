package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.FormulaFieldDependenciesAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;


public class ConstructFormulaDependenciesCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ConstructFormulaDependenciesCommand.class.getName());
	
	public boolean executeCommand(Context context) throws Exception {
		
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = (List<FormulaFieldResourceStatusContext>) context.get(FacilioConstants.ContextNames.FORMULA_RESOURCE_STATUS_LIST);
		List<FormulaFieldResourceContext> dependentFormulaFieldResourceContextList = (List<FormulaFieldResourceContext>) context.get(FacilioConstants.ContextNames.DEPENDENT_FIELD_RESOURCE_CONTEXT_LIST);
		
		Map<Long, FormulaFieldResourceStatusContext> resourceIdVsFormulaResourceStatusMap = new LinkedHashMap<Long, FormulaFieldResourceStatusContext>();
		for(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext:formulaFieldResourceStatusContextList)     //resourceId is the key as formulaFieldId will be the same
		{
			resourceIdVsFormulaResourceStatusMap.put(formulaFieldResourceStatusContext.getResourceId(), formulaFieldResourceStatusContext); //resourceId keyset here will be same as that of parentIds
		}
		
		if(dependentFormulaFieldResourceContextList == null || dependentFormulaFieldResourceContextList.isEmpty())
		{
			throw new Exception("Dependent Field Context cannot be null for the given parent formula id " +formula.getId());
		}
		
		List<FormulaFieldDependenciesContext> formulaFieldDependenciesContextList = new ArrayList<FormulaFieldDependenciesContext>();	
		
		for(FormulaFieldResourceContext dependentFormulaFieldResourceContext: dependentFormulaFieldResourceContextList)
		{		
			if(dependentFormulaFieldResourceContext.getResourceId() == null) 						//No dependent resource, so assign the field to all the associated resource
			{
				for(long resourceId: resourceIdVsFormulaResourceStatusMap.keySet())   				//current parent formula associated resource
				{							
					FormulaFieldDependenciesAPI.setFormulaFieldDependenciesContext(dependentFormulaFieldResourceContext.getFieldId(), resourceId, resourceIdVsFormulaResourceStatusMap.get(resourceId).getId(), formulaFieldDependenciesContextList); //taking dependency field resource as parent associated resource for granted as the dependent field resource is null
				}
			}
			else																					//Dependent field has only single associated resource in the formula
			{	
				for(long resourceId: resourceIdVsFormulaResourceStatusMap.keySet())					//current parent formula associated resource
				{
					long dependentResourceId =  dependentFormulaFieldResourceContext.getResourceId();					
					FormulaFieldDependenciesAPI.setFormulaFieldDependenciesContext(dependentFormulaFieldResourceContext.getFieldId(), dependentResourceId, resourceIdVsFormulaResourceStatusMap.get(resourceId).getId(), formulaFieldDependenciesContextList);						
				}			
			}
		}
		
		FormulaFieldDependenciesAPI.addFormulaFieldDependencies(formulaFieldDependenciesContextList);

		FormulaFieldResourceStatusAPI.rollUpIsLeafBasedOnDependencies(formulaFieldResourceStatusContextList,formulaFieldDependenciesContextList);
					
		return false;		
	}


}

