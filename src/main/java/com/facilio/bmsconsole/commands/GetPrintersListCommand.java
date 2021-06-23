package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetPrintersListCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(GetPrintersListCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getPrinterFields())
				.table(ModuleFactory.getPrinterModule().getTableName())
				;
		List<Map<String, Object>> props = selectBuilder.get();
		List<PrinterContext> printers = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				printers.add(FieldUtil.getAsBeanFromMap(prop, PrinterContext.class));
			}
			
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, printers);

		return false;
	}
}
