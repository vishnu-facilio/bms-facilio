package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetMeterMonthlyConsumptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long meterId = (long) context.get(FacilioConstants.ContextNames.ID);
		String utilityTypeModuleName = (String) context.get(FacilioConstants.Meter.UTILITY_TYPE);

		long currentMonthStartTime = DateTimeUtil.getMonthStartTime();
		long currentMonthEndTime = DateTimeUtil.getCurrenTime();
		long diff = currentMonthEndTime - currentMonthStartTime;

		long lastMonthStartTime = DateTimeUtil.getMonthStartTime(-1,false);
		long lastMonthEndTime = lastMonthStartTime + diff;
		if(utilityTypeModuleName.equals(FacilioConstants.Meter.GAS_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.GAS_DATA_READING);
			double thisMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentMonthStartTime, currentMonthEndTime,"gasVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisMonthTotal = Double.parseDouble(String.format("%.2f", thisMonthTotal));
			double lastMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastMonthStartTime, lastMonthEndTime,"gasVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastMonthTotal = Double.parseDouble(String.format("%.2f", lastMonthTotal));
			context.put("thisMonthConsumption", thisMonthTotal);
			context.put("lastMonthConsumption", lastMonthTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "gasVolumeAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.WATER_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.WATER_DATA_READING);
			double thisMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentMonthStartTime, currentMonthEndTime,"waterVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisMonthTotal = Double.parseDouble(String.format("%.2f", thisMonthTotal));
			double lastMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastMonthStartTime, lastMonthEndTime,"waterVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastMonthTotal = Double.parseDouble(String.format("%.2f", lastMonthTotal));
			context.put("thisMonthConsumption", thisMonthTotal);
			context.put("lastMonthConsumption", lastMonthTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "waterVolumeAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.ELECTRICITY_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_DATA_READING);
			double thisMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentMonthStartTime, currentMonthEndTime,"totalEnergyConsumptionDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisMonthTotal = Double.parseDouble(String.format("%.2f", thisMonthTotal));
			double lastMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastMonthStartTime, lastMonthEndTime,"totalEnergyConsumptionDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastMonthTotal = Double.parseDouble(String.format("%.2f", lastMonthTotal));
			context.put("thisMonthConsumption", thisMonthTotal);
			context.put("lastMonthConsumption", lastMonthTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "totalEnergyConsumptionDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.HEAT_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.HEAT_DATA_READING);
			double thisMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentMonthStartTime, currentMonthEndTime,"thermalEnergyAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisMonthTotal = Double.parseDouble(String.format("%.2f", thisMonthTotal));
			double lastMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastMonthStartTime, lastMonthEndTime,"thermalEnergyAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastMonthTotal = Double.parseDouble(String.format("%.2f", lastMonthTotal));
			context.put("thisMonthConsumption", thisMonthTotal);
			context.put("lastMonthConsumption", lastMonthTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "thermalEnergyAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.BTU_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.BTU_DATA_READING);
			double thisMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentMonthStartTime, currentMonthEndTime,"CHWConsumptionAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisMonthTotal = Double.parseDouble(String.format("%.2f", thisMonthTotal));
			double lastMonthTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastMonthStartTime, lastMonthEndTime,"CHWConsumptionAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastMonthTotal = Double.parseDouble(String.format("%.2f", lastMonthTotal));
			context.put("thisMonthConsumption", thisMonthTotal);
			context.put("lastMonthConsumption", lastMonthTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "CHWConsumptionAccumulatorDelta");
		}

		return false;
	}

	public static void calculatePercentageDifference(Context context) {
		double thisMonthTotal = (double) context.get("thisMonthConsumption");
		double lastMonthTotal = (double) context.get("lastMonthConsumption");
		double percentDiff = 0;
		int scaleVal = 0;
		if(lastMonthTotal != 0 && thisMonthTotal != 0 && thisMonthTotal > lastMonthTotal){
			percentDiff = ((thisMonthTotal - lastMonthTotal)/lastMonthTotal)*100;
			scaleVal = 1;
		}
		else if(lastMonthTotal != 0 && thisMonthTotal !=0 && lastMonthTotal > thisMonthTotal){
			percentDiff = ((lastMonthTotal - thisMonthTotal)/lastMonthTotal)*100;
		}
		context.put("percentDifference",percentDiff);
		context.put("scale",scaleVal );
	}
	
}
