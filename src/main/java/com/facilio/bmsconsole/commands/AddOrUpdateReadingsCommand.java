package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Chain addOrUpdateChain = FacilioChainFactory.onlyAddOrUpdateReadingsChain();
		addOrUpdateChain.execute(context);
		
		//Update Check point
		Record record = (Record) context.get(FacilioConstants.ContextNames.KINESIS_RECORD);
		if (record != null) {
			IRecordProcessorCheckpointer checkPointer = (IRecordProcessorCheckpointer) context.get(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER);
			checkPointer.checkpoint(record);
		}
		
		Chain executeWorkflowChain = FacilioChainFactory.executeWorkflowsForReadingChain();
		executeWorkflowChain.execute(context);
		
		Chain formulaChain = FacilioChainFactory.calculateFormulaChain();
		formulaChain.execute(context);
		
		return false;
	}

}
