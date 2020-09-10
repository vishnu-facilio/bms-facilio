package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.util.FormulaFieldDependenciesAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;

public class ScheduleFormulaFieldParentJobCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ScheduleFormulaFieldParentJobCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		FormulaFieldResourceStatusContext formulaResourceStatusContext = (FormulaFieldResourceStatusContext) context.get(FacilioConstants.ContextNames.FORMULA_RESOURCE);
		List<Integer> frequencyTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES);
		
		formulaResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal());
		formulaResourceStatusContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		FormulaFieldResourceStatusAPI.updateInProgressFormulaFieldResourceStatus(formulaResourceStatusContext);
		
		List<Long> parentFormulaResourceIds = FormulaFieldDependenciesAPI.getFormulaFieldResourceParentIdsByDependentFormula(formulaResourceStatusContext.getId());
		if(parentFormulaResourceIds != null && !parentFormulaResourceIds.isEmpty())
		{
			List<FormulaFieldResourceStatusContext> typedParentFormulaResourceStatusList = FormulaFieldResourceStatusAPI.getNotInProgressFormulaFieldResourceStatusByFrequencyAndIds(parentFormulaResourceIds, frequencyTypes);
			if(typedParentFormulaResourceStatusList != null && !typedParentFormulaResourceStatusList.isEmpty())
			{					
				for(FormulaFieldResourceStatusContext typedParentFormulaResourceStatusContext :typedParentFormulaResourceStatusList)
				{
					typedParentFormulaResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.IN_QUEUE.getIntVal());
					int rowsUpdated = FormulaFieldResourceStatusAPI.updateNotInProgressFormulaFieldResourceStatus(typedParentFormulaResourceStatusContext);	
					if (rowsUpdated == 1)
					{
						LOGGER.info("Triggering parents for --"+ formulaResourceStatusContext.getId() + " parent --" +typedParentFormulaResourceStatusContext.getId());
						System.out.println("Triggering parents for --"+ formulaResourceStatusContext.getId() + " parent --" +typedParentFormulaResourceStatusContext.getId());	
						FacilioContext instantJobcontext = new FacilioContext();
						instantJobcontext.put(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID, typedParentFormulaResourceStatusContext.getId());
						instantJobcontext.put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, frequencyTypes);
						FacilioTimer.scheduleInstantJob("formula","FormulaFieldCalculatorJob", instantJobcontext);				
					}
				}	
			}				
		}				
	
		
		return false;
	}

}
