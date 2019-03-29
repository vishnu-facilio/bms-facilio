package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddVirtualMeterRelCommand implements Command {
	
	public static final Pattern NUMBERS = Pattern.compile("\\d+");

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
			
			long startTime=DateTimeUtil.getMonthStartTime(DeviceAPI.VM_HISTORICAL_DATA_CALCULATION_INTERVAL);
			long endTime=System.currentTimeMillis();
			int interval=15;//ideally this value should be fetched from org settings.
			DeviceAPI.addHistoricalVMCalculationJob(meter.getId(),startTime,endTime,interval,true, true);
		}
		return false;
	}

	

}
