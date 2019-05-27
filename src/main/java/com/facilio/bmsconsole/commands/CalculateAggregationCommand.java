package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class CalculateAggregationCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CalculateAggregationCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean isCalculate = (Boolean) context.get(FacilioConstants.ContextNames.CALCULATE_REPORT_AGGR_DATA);
		if (isCalculate != null && !isCalculate) {
			return false;
		}
		
		long startTime = System.currentTimeMillis();
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		String sortAlias = (String) context.get(FacilioConstants.ContextNames.REPORT_SORT_ALIAS);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportData != null && !reportData.isEmpty()) {
			Collection<Map<String, Object>> csvData = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
			Map<String, Object> aggrData = new HashMap<>();

			for (ReportDataPointContext dp : report.getDataPoints()) {
				if (!dp.isAggrCalculated()) {//Whenever new aggregation is handled for another field type, dp.setAggregation should be updated there
					switch (dp.getyAxis().getDataTypeEnum()) {
						case NUMBER:
						case DECIMAL:
							doDecimalAggr(dp, csvData, sortAlias, aggrData);
							break;
						case BOOLEAN:
						case ENUM:
							if (dp.isHandleEnum()) {
								doEnumAggr(report, dp, csvData, aggrData, sortAlias);
							}
							break;
						default:
							break;
					}
				}
			}

			if (reportAggrData == null) {
				reportData.put(FacilioConstants.ContextNames.AGGR_KEY, aggrData);
			}
			else {
				reportAggrData.putAll(aggrData);
			}
		}
		LOGGER.debug("Time taken for calculating aggregation is : "+(System.currentTimeMillis() - startTime));
		return false;
	}
	
	private void doEnumAggr (ReportContext report, ReportDataPointContext dp, Collection<Map<String, Object>> csvData, Map<String, Object> aggrData, String timeAlias) {
		if (CollectionUtils.isEmpty(csvData)) {
			return;
		}

		Map<String, SimpleEntry<Long, Integer>> previousRecords = new HashMap<>();
		Iterator<Map<String, Object>> itr = csvData.iterator();
		Map<String, Object> currentData = itr.next();
		boolean isFirst = true;
		while (itr.hasNext()) {
//			Map<String, Object> data = csvData.get(i);
			Map<String, Object> nextData = itr.next();
			aggregateEnum(report, dp, aggrData, timeAlias, currentData, nextData, isFirst, previousRecords);
			if (isFirst) {
				isFirst = false;
			}
			currentData = nextData;
		}
		aggregateEnum(report, dp, aggrData, timeAlias, currentData, null, isFirst, previousRecords); //for the last record
		dp.setAggrCalculated(aggrData.containsKey(dp.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) + ".timeline"));
	}

	private void aggregateEnum (ReportContext report, ReportDataPointContext dp, Map<String, Object> aggrData, String timeAlias, Map<String, Object> currentData, Map<String, Object> nextData, boolean isFirst, Map<String, SimpleEntry<Long, Integer>> previousRecords) {
		long startTime = -1, endTime = -1;

		if (!isFirst) {
			startTime = (long) currentData.get(timeAlias);
		}

		if (nextData != null) {
			endTime = (long) nextData.get(timeAlias);
		}

		for (String alias : dp.getAliases().values()) {
			EnumVal enumValue = calculateEnumAggr(report, dp.getyAxis().getEnumMap().keySet(), currentData.get(alias), alias, startTime, endTime, previousRecords, aggrData); //Starttime is included and endtime is excluded
			if (enumValue != null) {
				currentData.put(alias, enumValue);
			}
		}
	}
	
	private EnumVal calculateEnumAggr (ReportContext report, Set<Integer> enumValueKeys, Object value, String alias, long startTime, long endTime, Map<String, SimpleEntry<Long, Integer>> previousRecords, Map<String, Object> aggrData) {
		List<SimpleEntry<Long, Integer>> enumVal = (List<SimpleEntry<Long, Integer>>) value;
		EnumVal enumValue = combineEnumVal(report, enumValueKeys, enumVal, startTime, endTime, previousRecords.get(alias));

		if (enumValue != null) {
			previousRecords.put(alias, enumValue.timeline.get(enumValue.timeline.size() - 1));
			//Aggr
			String timelineKey = alias + ".timeline";
			String duraionKey = alias + ".duration";
			List<SimpleEntry<Long, Integer>> fullTimeline = (List<SimpleEntry<Long, Integer>>) aggrData.get(timelineKey);
			if (fullTimeline == null) {
				aggrData.put(timelineKey, new ArrayList<>(enumValue.timeline));
				aggrData.put(duraionKey, new HashMap<>(enumValue.duration));
			} else {
				int i = 0;
				if (fullTimeline.get(fullTimeline.size() - 1).getValue() == enumValue.timeline.get(0).getValue()) {
					i = 1;
				}
				for (; i < enumValue.timeline.size(); i++) {
					fullTimeline.add(enumValue.timeline.get(i));
				}

				Map<Integer, Long> completeDuration = (Map<Integer, Long>) aggrData.get(duraionKey);
				completeDuration.replaceAll((val, duration) -> duration + enumValue.duration.get(val));
			}
		}
		return enumValue;
	}
	
	private EnumVal combineEnumVal (ReportContext report, Set<Integer> enumValues, List<SimpleEntry<Long, Integer>> highResVal, long startTime, long endTime, SimpleEntry<Long, Integer> previousRecord) {
		long currentTime = System.currentTimeMillis();

		if (AccountUtil.getCurrentOrg().getId() == 134) {
			LOGGER.info(new StringBuilder()
							.append("High Res : ").append(highResVal).append("\n")
							.append("Start Time : ").append(startTime).append("\n")
							.append("Current Time : ").append(currentTime).append("\n")
							.append("Previous Record : ").append(previousRecord));
		}

		if (CollectionUtils.isEmpty(highResVal) && (previousRecord == null || startTime > currentTime)) {
			return null;
		}

		List<SimpleEntry<Long, Integer>> timeline = new ArrayList<>();
		Map<Integer, Long> durations = initDuration(enumValues);
		
		LOGGER.info(new StringBuilder()
						.append("High Res Val : ").append(highResVal).append("\n")
						.append("Enum : ").append(enumValues).append("\n")
						.append("StartTime : ").append(startTime).append("\n")
						.append("endTime : ").append(endTime).append("\n")
						.append("Prev record : ").append(previousRecord).append("\n"));
		
		if (previousRecord != null) {
			SimpleEntry<Long, Integer> val = new SimpleEntry<Long, Integer>(startTime, previousRecord.getValue());
			timeline.add(val);
			previousRecord = val;
		}

		if (CollectionUtils.isNotEmpty(highResVal)) {
			for (SimpleEntry<Long, Integer> val : highResVal) {
				if (previousRecord == null) {
					timeline.add(val);
					previousRecord = val;
				} else if (previousRecord.getValue() != val.getValue()) {
					timeline.add(val);
					long duration = durations.get(previousRecord.getValue());
					durations.put(previousRecord.getValue(), duration + (val.getKey() - previousRecord.getKey()));
					previousRecord = val;
				}
			}
		}
		
		if (endTime == -1) {
			if (report.getDateOperatorEnum().isCurrentOperator()) {
				endTime = currentTime;
			}
			else {
				endTime = report.getDateOperatorEnum().getRange(report.getDateValue()).getEndTime();
			}
			timeline.add(new SimpleEntry<Long, Integer>(endTime, previousRecord.getValue())); 
			endTime++; //Because this endttime is inclusive
		}
		
		long duration = durations.get(previousRecord.getValue());
		durations.put(previousRecord.getValue(), duration + (endTime - previousRecord.getKey())); //End time is start time of next cycle. It's excluded
		
		EnumVal val = new EnumVal(highResVal, timeline, durations);
		LOGGER.info(val);
		return val;
	}
	
	private Map<Integer, Long> initDuration(Set<Integer> enumValues) {
		Map<Integer, Long> duration = new HashMap<>();
		for (Integer enumVal : enumValues) {
			duration.put(enumVal, 0l);
		}
		return duration;
	}
	
	private void doDecimalAggr (ReportDataPointContext dp, Collection<Map<String, Object>> csvData, String sortAlias, Map<String, Object> aggrData) {
		for (Map<String, Object> data : csvData) {
			boolean isLatest = false;
			if (sortAlias != null && !sortAlias.isEmpty()) {
				Double sortVal = getDoubleVal(data.get(sortAlias));
				if (sortVal != null) {
					Double currentSortVal = (Double) aggrData.get(sortAlias+".lastValue");
					isLatest = currentSortVal == null || currentSortVal <= sortVal;
				}
			}
			
			for (String alias : dp.getAliases().values()) {
				calculateNumericAggr(data.get(alias), alias, aggrData, isLatest);
			}
		}
		dp.setAggrCalculated(aggrData.containsKey(dp.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) + ".sum"));
	}
	
	private void calculateNumericAggr (Object value, String alias, Map<String, Object> aggrData, boolean isLatest) {
		Double val = getDoubleVal(value);
		if (val != null) {
			String sumKey = alias+".sum";
			Double sum = (Double) aggrData.get(sumKey); 
			if (sum == null) {
				addFirstTimeAggr(alias, val, aggrData);
			}
			else {
				sum = sum + val; 
				aggrData.put(sumKey, sum);
				
				String minKey = alias+".min";
				if ((Double) aggrData.get(minKey) > val) {
					aggrData.put(minKey, val);
				}
				
				String maxKey = alias+".max";
				if ((Double) aggrData.get(maxKey) < val) {
					aggrData.put(maxKey, val);
				}
				
				String countKey = alias+".count";
				Double count = (Double) aggrData.get(countKey) + 1;
				aggrData.put(countKey, count);
				aggrData.put(alias+".avg", sum / count); //TODO Should handle Decimal Formatting
				
				if (isLatest) {
					aggrData.put(alias+".lastValue", val);
				}
			}
		}
	}
	
	private void addFirstTimeAggr (String key, Double val, Map<String, Object> aggrData) {
		aggrData.put(key+".sum", val);
		aggrData.put(key+".min", val);
		aggrData.put(key+".max", val);
		aggrData.put(key+".count", 1d);
		aggrData.put(key+".avg", val);
		aggrData.put(key+".lastValue", val);
	}
	
	private Double getDoubleVal (Object val) {
		if (val != null) {
			if (val instanceof Number) {
				return ((Number) val).doubleValue();
			}
			try {
				return new Double(val.toString());
			} catch (NumberFormatException ex) {
				return null;
			}
		}
		return null;
	}
	
	public static class EnumVal {
		
		public EnumVal() {
			// TODO Auto-generated constructor stub
		}
		
		public EnumVal(List<SimpleEntry<Long, Integer>> actualTimeline, List<SimpleEntry<Long, Integer>> timeline, Map<Integer, Long> duration) {
			// TODO Auto-generated constructor stub
			this.actualTimeline = actualTimeline;
			this.timeline = timeline;
			this.duration = duration;
		}
		
		private List<SimpleEntry<Long, Integer>> actualTimeline;
		public List<SimpleEntry<Long, Integer>> getActualTimeline() {
			return actualTimeline;
		}
		public void setActualTimeline(List<SimpleEntry<Long, Integer>> actualTimeline) {
			this.actualTimeline = actualTimeline;
		}

		private List<SimpleEntry<Long, Integer>> timeline;
		public List<SimpleEntry<Long, Integer>> getTimeline() {
			return timeline;
		}
		public void setTimeline(List<SimpleEntry<Long, Integer>> timeline) {
			this.timeline = timeline;
		}

		private Map<Integer, Long> duration;
		public Map<Integer, Long> getDuration() {
			return duration;
		}
		public void setDuration(Map<Integer, Long> duration) {
			this.duration = duration;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return new StringBuilder("EnumVal [\n")
						.append("Actual : ").append(actualTimeline).append("\n")
						.append("Time : ").append(timeline).append("\n")
						.append("Duration : ").append(duration).append("\n]")
						.toString();
		}
	}
}
