package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.AdditionalCostContext;
import com.facilio.bmsconsole.context.AdditionalCostContext.CostType;
import com.facilio.bmsconsole.context.CostAssetsContext;
import com.facilio.bmsconsole.context.CostContext;
import com.facilio.bmsconsole.context.CostSlabContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.CostAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;

public class CalculateCostCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CalculateCostCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		CostContext cost = (CostContext) context.get(FacilioConstants.ContextNames.COST);
		CostAssetsContext asset = (CostAssetsContext) context.get(FacilioConstants.ContextNames.COST_ASSET);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		
		if (readings != null && !readings.isEmpty()) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			Map<Long, FacilioField> fieldIdMap = FieldFactory.getAsIdMap(fields);
			FacilioField totalCostField = fieldMap.get("totalCost");
			FacilioField slabCostField = fieldMap.get("slabCost");
			List<ReadingContext> costReadings = new ArrayList<>();
			FacilioField utilityField = cost.getUtilityEnum().getReadingField();
			Map<Double, List<CostSlabContext>> maxUnitWiseSlabs = getMaxUnitWiseSlabe(cost);
			LOGGER.debug("Max Unit wise Slabs : "+maxUnitWiseSlabs);
			List<DateRange> intervals = DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), 24 * 60);
			for (DateRange interval : intervals) {
				double totalPrevDayUnits = 0;
				double totalUnits = 0;
				long billStartTime = CostAPI.getBillStartTime(asset, DateTimeUtil.getZonedDateTime(interval.getStartTime())).toInstant().toEpochMilli();
				long prevDayEnd = interval.getStartTime() - 1;
				for (ReadingContext reading : readings) {
					Double val = (Double) reading.getReading(utilityField.getName());
					if (val != null) {
						if (billStartTime <= reading.getTtime() && reading.getTtime() <= interval.getEndTime()) {
							totalUnits += val;
						}
						if (billStartTime <= reading.getTtime() && reading.getTtime() <= prevDayEnd) {
							totalPrevDayUnits += val;
						}
					}
				}
				LOGGER.info("Calculating '"+cost.getName()+"' cost for : "+asset.getAssetId()+" between "+interval);
				LOGGER.info("Current Total Units : "+totalUnits);
				LOGGER.info("Prev Day Total Units : "+totalPrevDayUnits);
				ReadingContext reading = getCostReading(cost, asset, maxUnitWiseSlabs, interval, totalUnits, totalPrevDayUnits, totalCostField, slabCostField, fieldIdMap);
				LOGGER.info("Cost reading : "+reading);
				if (reading != null) {
					costReadings.add(reading);
				}
			}
			LOGGER.info("Cost Reading size : "+costReadings.size());
			context.put(FacilioConstants.ContextNames.COST_READINGS, costReadings);
		}
		return false;
	}
	
	private ReadingContext getCostReading (CostContext cost, CostAssetsContext asset, Map<Double, List<CostSlabContext>> maxUnitWiseSlabs, DateRange interval, double totalUnits, double totalPrevDayUnits, FacilioField totalCostField, FacilioField slabCostField, Map<Long, FacilioField> fieldIdMap) {
		if (totalUnits != 0) {
			double totalCost = calculateSlabCost(totalUnits, maxUnitWiseSlabs);
			LOGGER.debug("Slab Cost : "+totalCost);
			if (totalCost != 0) {
				ReadingContext reading = new ReadingContext();
				reading.setTtime(interval.getEndTime());
				reading.setParentId(asset.getAssetId());
				double prevDayTotalCost = calculateSlabCost(totalPrevDayUnits, maxUnitWiseSlabs);
				LOGGER.debug("Prev day slab Cost : "+prevDayTotalCost);
				reading.addReading(slabCostField.getName(), totalCost - prevDayTotalCost);
				Map<CostType, List<AdditionalCostContext>> typeWiseAdditionalCosts = getTypeWiseAdditionalCosts(cost);
				if (typeWiseAdditionalCosts != null && !typeWiseAdditionalCosts.isEmpty()) {
					List<AdditionalCostContext> unitCosts = typeWiseAdditionalCosts.get(CostType.UNIT_WISE);
					if (unitCosts != null && !unitCosts.isEmpty()) {
						for (AdditionalCostContext unitCost : unitCosts) {
							double currentDay = calculateUnitAdditionalCost(totalUnits, unitCost);
							totalCost += currentDay;
							double prevDay = calculateUnitAdditionalCost(totalPrevDayUnits, unitCost);
							prevDayTotalCost += prevDay;
							reading.addReading(fieldIdMap.get(unitCost.getReadingFieldId()).getName(), currentDay - prevDay);
						}
					}
					
					List<AdditionalCostContext> flatCosts = typeWiseAdditionalCosts.get(CostType.FLAT);
					if (flatCosts != null && !flatCosts.isEmpty()) {
						for (AdditionalCostContext flatCost : flatCosts) {
							double currentDay = calculateFlatAdditionalCost(totalUnits, flatCost);
							totalCost += currentDay;
							double prevDay = calculateFlatAdditionalCost(totalPrevDayUnits, flatCost);
							prevDayTotalCost += prevDay;
							reading.addReading(fieldIdMap.get(flatCost.getReadingFieldId()).getName(), currentDay - prevDay);
						}
					}
					
					List<AdditionalCostContext> percentageCosts = typeWiseAdditionalCosts.get(CostType.PERCENTAGE);
					if (percentageCosts != null && !percentageCosts.isEmpty()) {
						for (AdditionalCostContext percentCost : percentageCosts) {
							double currentDay = calculatePercentageAdditionalCost(totalCost, percentCost);
							totalCost += currentDay;
							double prevDay = calculatePercentageAdditionalCost(prevDayTotalCost, percentCost);
							prevDayTotalCost += prevDay;
							reading.addReading(fieldIdMap.get(percentCost.getReadingFieldId()).getName(), currentDay - prevDay);
						}
					}
				}
				reading.addReading(totalCostField.getName(), totalCost - prevDayTotalCost);
				return reading;
			}
		}
		return null;
	}
	
	private double calculateFlatAdditionalCost (double totalUnits, AdditionalCostContext cost) {
		return totalUnits > 0 ? cost.getCost() : 0;
	}
	
	private double calculatePercentageAdditionalCost (double totalCost, AdditionalCostContext cost) {
		return totalCost * cost.getCost() / 100;
	}
	
	private double calculateUnitAdditionalCost(double totalUnits, AdditionalCostContext cost) {
		return totalUnits * cost.getCost();
	}
	
	private Map<Double, List<CostSlabContext>> getMaxUnitWiseSlabe(CostContext cost) {
		Map<Double, List<CostSlabContext>> maxUnitWiseSlabs = new LinkedHashMap<>();
		for (CostSlabContext slab : cost.getSlabs()) {
			List<CostSlabContext> slabs = maxUnitWiseSlabs.get(slab.getMaxUnit());
			if (slabs == null) {
				slabs = new ArrayList<>();
				maxUnitWiseSlabs.put(slab.getMaxUnit(), slabs);
			}
			slabs.add(slab);
		}
		List<CostSlabContext> noMaxSlabs = maxUnitWiseSlabs.remove(-1); //Putting no max slabs in the end
		if (noMaxSlabs != null) {
			maxUnitWiseSlabs.put(-1d, noMaxSlabs);
		}
		return maxUnitWiseSlabs;
	}
	
	private Map<CostType, List<AdditionalCostContext>> getTypeWiseAdditionalCosts(CostContext cost) {
		if (cost.getAdditionalCosts() != null && !cost.getAdditionalCosts().isEmpty()) {
			Map<CostType, List<AdditionalCostContext>> typeWiseAdditionalCosts = new HashMap<>();
			for (AdditionalCostContext additionalCost : cost.getAdditionalCosts()) {
				List<AdditionalCostContext> costList = typeWiseAdditionalCosts.get(additionalCost.getTypeEnum());
				if (costList == null) {
					costList = new ArrayList<>();
					typeWiseAdditionalCosts.put(additionalCost.getTypeEnum(), costList);
				}
				costList.add(additionalCost);
			}
			return typeWiseAdditionalCosts;
		}
		return null;
	}
	
	private double calculateSlabCost(double totalUnits, Map<Double, List<CostSlabContext>> maxUnitWiseSlabs) {
		double totalCost = 0;
		
		if (totalUnits == 0) {
			return totalCost;
		}
		
		for (Map.Entry<Double, List<CostSlabContext>> entry : maxUnitWiseSlabs.entrySet()) {
			double maxUnit = entry.getKey();
			if (maxUnit == -1 || totalUnits <= maxUnit) {
				List<CostSlabContext> slabs = entry.getValue();
				
				for (int i = slabs.size() - 1; i >= 0; i--) {
					CostSlabContext slab = slabs.get(i);
					if (slab.getStartRange() == -1) {
						totalCost += totalUnits * slab.getCost();
						break;
					}
					if (totalUnits >= slab.getStartRange() && (slab.getEndRange() == -1 || totalUnits <= slab.getEndRange())) {
						double slabUnits = totalUnits - slab.getStartRange() + 1;
						totalUnits = slab.getStartRange() - 1;
						totalCost += slabUnits * slab.getCost();
					}
				}
				break;
			}
		}
		return totalCost;
	}
	
}
