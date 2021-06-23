package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;


public class AddOrUpdatePrinterCommand extends FacilioCommand {


	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		PrinterContext printerContext = (PrinterContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (printerContext != null) {
			
			if (printerContext.getId() > 0) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getPrinterModule().getTableName())
						.fields(FieldFactory.getPrinterFields())
						.andCondition(CriteriaAPI.getIdCondition(printerContext.getId(), ModuleFactory.getPrinterModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(printerContext);
				int updatedRows = updateBuilder.update(props);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getPrinterModule().getTableName())
						.fields(FieldFactory.getPrinterFields());

				Map<String, Object> props = FieldUtil.getAsProperties(printerContext);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				long recordId = (Long) props.get("id");
				printerContext.setId(recordId);
			}
		}
		
		return false;
	}

}

