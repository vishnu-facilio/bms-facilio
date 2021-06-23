package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdatePrinterCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// Add Picklist entry for visitor and insert that ID Into table

		PrinterContext printerContext=(PrinterContext)context.get(ContextNames.RECORD);
		
		long printerID=printerContext.getId();
		Map<String, Object> props = new HashMap<>();
		props=FieldUtil.getAsProperties(printerContext);
		
		FacilioModule printerModule=ModuleFactory.getPrinterModule();
		List<FacilioField> printerModuleField=FieldFactory.getPrinterFields();
		
		GenericUpdateRecordBuilder updateBuilder=new GenericUpdateRecordBuilder();
		updateBuilder.table(printerModule.getTableName()).
		fields(printerModuleField).
		andCondition(CriteriaAPI.getCondition("ID", "id", printerID+"", NumberOperators.EQUALS));
		updateBuilder.update(props);
					
		return false;
		
	}

}
