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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetMeterPeakCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long meterId = (long) context.get(FacilioConstants.ContextNames.ID);
		String utilityTypeModuleName = (String) context.get(FacilioConstants.Meter.UTILITY_TYPE);

		long currentYearStartTime = DateTimeUtil.getYearStartTime();
		long currentYearEndTime = DateTimeUtil.getCurrenTime();

		long lastYearStartTime = DateTimeUtil.getYearStartTime(-1,false);
		long lastYearEndTime = currentYearStartTime - 1;

		if(utilityTypeModuleName.equals(FacilioConstants.Meter.GAS_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.GAS_DATA_READING);
			Map<String, Object> thisYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, currentYearStartTime, currentYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);
			Map<String, Object> lastYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, lastYearStartTime, lastYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);

			getThisYearPeak(thisYearPeakResult, context);
			getLastYearPeak(lastYearPeakResult, context);

			calculatePercentageDifference(context);

			context.put("readingName", "flowRate");
			context.put("widgetName", "Maximum Flow");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.WATER_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.WATER_DATA_READING);
			Map<String, Object> thisYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, currentYearStartTime, currentYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);
			Map<String, Object> lastYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, lastYearStartTime, lastYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);

			getThisYearPeak(thisYearPeakResult, context);
			getLastYearPeak(lastYearPeakResult, context);

			calculatePercentageDifference(context);

			context.put("readingName", "flowRate");
			context.put("widgetName", "Maximum Flow");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.ELECTRICITY_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_DATA_READING);
			Map<String, Object> thisYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, currentYearStartTime, currentYearEndTime,"totalDemand", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);
			Map<String, Object> lastYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, lastYearStartTime, lastYearEndTime,"totalDemand", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);

			getThisYearPeak(thisYearPeakResult, context);
			getLastYearPeak(lastYearPeakResult, context);

			calculatePercentageDifference(context);

			context.put("readingName", "totalDemand");
			context.put("widgetName", "Peak Demand");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.HEAT_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.HEAT_DATA_READING);
			Map<String, Object> thisYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, currentYearStartTime, currentYearEndTime,"thermalPower", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);
			Map<String, Object> lastYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, lastYearStartTime, lastYearEndTime,"thermalPower", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);

			getThisYearPeak(thisYearPeakResult, context);
			getLastYearPeak(lastYearPeakResult, context);

			calculatePercentageDifference(context);

			context.put("readingName", "thermalPower");
			context.put("widgetName", "Peak Thermal Power");
		}
		else if(utilityTypeModuleName.equals(FacilioConstants.Meter.BTU_METER)) {
			FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.BTU_DATA_READING);
			Map<String, Object> thisYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, currentYearStartTime, currentYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);
			Map<String, Object> lastYearPeakResult = MetersAPI.fetchMeterAggregatedReadingWithTime(meterId, lastYearStartTime, lastYearEndTime,"flowRate", readingModule, BmsAggregateOperators.NumberAggregateOperator.MAX);

			getThisYearPeak(thisYearPeakResult, context);
			getLastYearPeak(lastYearPeakResult, context);

			calculatePercentageDifference(context);

			context.put("readingName", "flowRate");
			context.put("widgetName", "Maximum Flow");
		}

		return false;
	}

	public static void calculatePercentageDifference(Context context) {
		double thisYearPeak = 0;
		double lastYearPeak = 0;
		if(context.get("peakDemand") != null) {
			thisYearPeak = (double) context.get("peakDemand");
		}
		if(context.get("lastYearPeakDemand") != null) {
			lastYearPeak = (double) context.get("lastYearPeakDemand");
		}
		double percentDiff = 0;
		int scaleVal = 0;
		if(lastYearPeak != 0 && thisYearPeak != 0 && thisYearPeak > lastYearPeak){
			percentDiff = ((thisYearPeak - lastYearPeak)/lastYearPeak)*100;
			scaleVal = 1;
		}
		else if(lastYearPeak != 0 && thisYearPeak !=0 && lastYearPeak > thisYearPeak){
			percentDiff = ((lastYearPeak - thisYearPeak)/lastYearPeak)*100;
		}
		context.put("percentDifference",percentDiff);
		context.put("scale",scaleVal );
	}

	public static void getThisYearPeak(Map<String, Object> thisYearPeakResult, Context context) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		if(thisYearPeakResult != null && thisYearPeakResult.get("reading") != null) {
			double thisYearPeak = (double) thisYearPeakResult.get("reading");
			thisYearPeak = Double.parseDouble(String.format("%.2f", thisYearPeak));
			context.put("peakDemand", thisYearPeak);
			if(thisYearPeak > 0 && thisYearPeakResult.get("ttime") != null) {
				long thisYearPeakTime = (long) thisYearPeakResult.get("ttime");
				String peakTime = sdf.format(thisYearPeakTime);
				context.put("peakTime", peakTime);
			}
		}
	}

	public static void getLastYearPeak(Map<String, Object> lastYearPeakResult, Context context) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		if(lastYearPeakResult != null && lastYearPeakResult.get("reading") != null) {
			double lastYearPeak = (double) lastYearPeakResult.get("reading");
			lastYearPeak = Double.parseDouble(String.format("%.2f", lastYearPeak));
			context.put("lastYearPeakDemand", lastYearPeak);
			if(lastYearPeak > 0 && lastYearPeakResult.get("ttime") != null) {
				long lastYearPeakTime = (long) lastYearPeakResult.get("ttime");
				String peakTime = sdf.format(lastYearPeakTime);
				context.put("lastYearPeakTime", peakTime);
			}
		}
	}

}
