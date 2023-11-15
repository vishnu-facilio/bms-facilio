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


public class GetMeterYearlyConsumptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long meterId = (long) context.get(FacilioConstants.ContextNames.ID);
		String utilityTypeModuleName = (String) context.get(FacilioConstants.Meter.UTILITY_TYPE);

		long currentYearStartTime = DateTimeUtil.getYearStartTime();
		long currentYearEndTime = DateTimeUtil.getCurrenTime();
		long diff = currentYearEndTime - currentYearStartTime;

		long lastYearStartTime = DateTimeUtil.getYearStartTime(-1,false);
		long lastYearEndTime = lastYearStartTime + diff;
		if(utilityTypeModuleName.equals(FacilioConstants.Meter.GAS_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.GAS_DATA_READING);
			double thisYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentYearStartTime, currentYearEndTime,"gasVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisYearTotal = Double.parseDouble(String.format("%.2f", thisYearTotal));
			double lastYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastYearStartTime, lastYearEndTime,"gasVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastYearTotal = Double.parseDouble(String.format("%.2f", lastYearTotal));
			context.put("totalConsumption", thisYearTotal);
			context.put("lastYearConsumption", lastYearTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "gasVolumeAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.WATER_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.WATER_DATA_READING);
			double thisYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentYearStartTime, currentYearEndTime,"waterVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisYearTotal = Double.parseDouble(String.format("%.2f", thisYearTotal));
			double lastYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastYearStartTime, lastYearEndTime,"waterVolumeAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastYearTotal = Double.parseDouble(String.format("%.2f", lastYearTotal));
			context.put("totalConsumption", thisYearTotal);
			context.put("lastYearConsumption", lastYearTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "waterVolumeAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.ELECTRICITY_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_DATA_READING);
			double thisYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentYearStartTime, currentYearEndTime,"totalEnergyConsumptionDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisYearTotal = Double.parseDouble(String.format("%.2f", thisYearTotal));
			double lastYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastYearStartTime, lastYearEndTime,"totalEnergyConsumptionDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastYearTotal = Double.parseDouble(String.format("%.2f", lastYearTotal));
			context.put("totalConsumption", thisYearTotal);
			context.put("lastYearConsumption", lastYearTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "totalEnergyConsumptionDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.HEAT_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.HEAT_DATA_READING);
			double thisYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentYearStartTime, currentYearEndTime,"thermalEnergyAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisYearTotal = Double.parseDouble(String.format("%.2f", thisYearTotal));
			double lastYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastYearStartTime, lastYearEndTime,"thermalEnergyAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastYearTotal = Double.parseDouble(String.format("%.2f", lastYearTotal));
			context.put("totalConsumption", thisYearTotal);
			context.put("lastYearConsumption", lastYearTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "thermalEnergyAccumulatorDelta");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.BTU_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.BTU_DATA_READING);
			double thisYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, currentYearStartTime, currentYearEndTime,"CHWConsumptionAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			thisYearTotal = Double.parseDouble(String.format("%.2f", thisYearTotal));
			double lastYearTotal = MetersAPI.fetchMeterAggregatedReading(meterId, lastYearStartTime, lastYearEndTime,"CHWConsumptionAccumulatorDelta", readingModule, BmsAggregateOperators.NumberAggregateOperator.SUM);
			lastYearTotal = Double.parseDouble(String.format("%.2f", lastYearTotal));
			context.put("totalConsumption", thisYearTotal);
			context.put("lastYearConsumption", lastYearTotal);

			calculatePercentageDifference(context);
			context.put("readingName", "CHWConsumptionAccumulatorDelta");
		}

		return false;
	}

	public static void calculatePercentageDifference(Context context) {
		double thisYearTotal = (double) context.get("totalConsumption");
		double lastYearTotal = (double) context.get("lastYearConsumption");
		double percentDiff = 0;
		int scaleVal = 0;
		if(lastYearTotal != 0 && thisYearTotal != 0 && thisYearTotal > lastYearTotal){
			percentDiff = ((thisYearTotal - lastYearTotal)/lastYearTotal)*100;
			scaleVal = 1;
		}
		else if(lastYearTotal != 0 && thisYearTotal !=0 && lastYearTotal > thisYearTotal){
			percentDiff = ((lastYearTotal - thisYearTotal)/lastYearTotal)*100;
		}
		context.put("percentDifference",percentDiff);
		context.put("scale",scaleVal );
	}
	
}
