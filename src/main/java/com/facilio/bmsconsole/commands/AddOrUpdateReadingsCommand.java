package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateReadingsCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(AddOrUpdateReadingsCommand.class.getName());
	
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
		
		try {
			Chain executeWorkflowChain = FacilioChainFactory.executeWorkflowsForReadingChain();
			executeWorkflowChain.execute(context);
		}
		catch (Exception e) {
			Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			LOGGER.error("Error occurred during workflow execution of readings. \n"+readingMap, e);
			CommonCommandUtil.emailException("AddOrUpdateReading", "Error occurred during workflow execution of readings.", e, String.valueOf(readingMap));
		}
		
		try {
			Chain formulaChain = FacilioChainFactory.calculateFormulaChain();
			formulaChain.execute(context);
		}
		catch (Exception e) {
			Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
			LOGGER.error("Error occurred during formula calculation of readings. \n"+readingMap, e);
			CommonCommandUtil.emailException("AddOrUpdateReading", "Error occurred during formula calculation of readings.", e, String.valueOf(readingMap));
		}
		
		return false;
	}

}
