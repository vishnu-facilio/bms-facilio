package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.bmsconsole.util.PrinterUtils;
import com.facilio.constants.FacilioConstants;

public class SendMessageOnPrinterChangeCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(SendMessageOnPrinterChangeCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) {
		try {
			PrinterContext printer=(PrinterContext)context.get(FacilioConstants.ContextNames.RECORD);
			PrinterUtils.refreshPrinterHostDevice(printer.getId());
			return false;
		}
		catch (Exception e) {
			LOGGER.info(e);
		}
		return false;
	}
	
}
