package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;

public class AddVirtualMeterRelCommand extends FacilioCommand {
	
	public static final Pattern NUMBERS = Pattern.compile("\\d+");

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
				
			HistoricalLoggerContext historicalLoggerContext = DeviceAPI.setHistoricalLoggerContext(meter.getId(), startTime, endTime, true, (long) -1);
			HistoricalLoggerUtil.addHistoricalLogger(historicalLoggerContext);
			DeviceAPI.addHistoricalVMCalculationJob(historicalLoggerContext.getId(),meter.getId(),startTime, endTime,true);
		}	

		if(meter != null && meter.getMultiplicationFactor() != -1 && meter.getMultiplicationFactor() != 1l) {
			AggregatedEnergyConsumptionUtil.calculateHistoryForAggregatedEnergyConsumption(-1l, -1l, Collections.singletonList(meter.getId()));
		}	
		return false;
	}

	

}
