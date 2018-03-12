package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class AddVirtualMeterRelCommand implements Command {
	
	public static final Pattern NUMBERS = Pattern.compile("-?\\d+");

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyMeterContext meter = (EnergyMeterContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(meter != null && meter.getChildMeterExpression() != null) {
			String expression = meter.getChildMeterExpression();
			if(expression == null || expression.isEmpty()) {
				throw new IllegalArgumentException("Formula expression cannot be null while adding virtual meter");
			}
			expression = expression.trim();
			if(!Pattern.matches("^[0-9-+()\\s]+$", expression)) {
				throw new IllegalAccessException("Invalid expression during addition of virtual meter");
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.fields(FieldFactory.getVirtualMeterRelFields())
															.table(ModuleFactory.getVirtualMeterRelModule().getTableName());
			
			Matcher matcher = NUMBERS.matcher(meter.getChildMeterExpression());
			while (matcher.find()) {
				Map<String, Object> relProp = new HashMap<>();
				relProp.put("virtualMeterId", meter.getId());
				relProp.put("childMeterId", Long.parseLong(matcher.group()));
				insertBuilder.addRecord(relProp);
			}
			insertBuilder.save();
			
			FacilioTimer.deleteJob(meter.getId(), "HistoricalVMCalculation");
			FacilioTimer.scheduleOneTimeJob(meter.getId(), "HistoricalVMCalculation", 30, "priority");
		}
		return false;
	}

}
