package com.facilio.energystar.command;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class ESfetchMeterMissingData extends FacilioCommand {
	

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarPropertyContext> propertyContexts = (List<EnergyStarPropertyContext>) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule meterData = modBean.getModule(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		List<FacilioField> fields = modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		if(propertyContexts == null) {
			return false;
		}
		
		for(EnergyStarPropertyContext propertyContext :propertyContexts) {
			
			for(EnergyStarMeterContext meter : propertyContext.getMeterContexts()) {
				
				SelectRecordsBuilder<EnergyStarMeterDataContext> select = new SelectRecordsBuilder<EnergyStarMeterDataContext>()
						.beanClass(EnergyStarMeterDataContext.class)
						.module(meterData)
						.select(modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meter.getId()+"", NumberOperators.EQUALS))
						.orderBy("FROM_DATE");
				
				List<EnergyStarMeterDataContext> dataList = select.get();
				
				List<DateRange> dataAvailableRanges = new ArrayList<>();
				
				List<DateRange> dataMissingRanges = new ArrayList<>();
				
				DateRange currentDateRange = null;
				long toalDataMissingTime = 0;
				for(int i=0;i< dataList.size();i++) {
					
					EnergyStarMeterDataContext data = dataList.get(i);
					
					System.out.println("i -- "+i);
					if(i == dataList.size()-1) {
						long getLastendTime = data.getToDate();
						ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
						int currentDate = zdt.getDayOfMonth();
						
						long expectedEndTime = 0l;
						if(currentDate < EnergyStarUtil.ENERGY_STAR_DATA_PUSHING_DATE) {
							expectedEndTime = DateTimeUtil.addDays(DateTimeUtil.getMonthStartTime(-1), -1);
						}
						else {
							expectedEndTime = DateTimeUtil.addDays(DateTimeUtil.getMonthStartTime(), -1);
						}
						if(getLastendTime < expectedEndTime) {
							DateRange currentDateRange1 = new DateRange();
							currentDateRange1.setStartTime(DateTimeUtil.addDays(getLastendTime, 1));
							currentDateRange1.setEndTime(expectedEndTime);
							
							dataMissingRanges.add(currentDateRange1);
						}
					}
					
					if(currentDateRange != null) {
						
						long lastEndtime = DateTimeUtil.addDays(currentDateRange.getEndTime(), 1);
						
						if(data.getfromDate() <= lastEndtime) {
							currentDateRange.setEndTime(data.getToDate());
							continue;
						}
						else {
							DateRange dataMissingRange = new DateRange();
							dataMissingRange.setStartTime(lastEndtime);
							dataMissingRange.setEndTime(data.getfromDate()-1);
							
							toalDataMissingTime += dataMissingRange.getEndTime() - dataMissingRange.getStartTime();
							
							dataMissingRanges.add(dataMissingRange);
						}
					}
					
					currentDateRange = new DateRange();
					
					currentDateRange.setStartTime(data.getfromDate());
					currentDateRange.setEndTime(data.getToDate());
					
					dataAvailableRanges.add(currentDateRange);
					
				}
				
				fillMissingDateRangeString(meter, toalDataMissingTime);
				
				meter.setEnergyStarDataAvailableRanges(dataAvailableRanges);
				meter.setEnergyStarDataMissingRanges(dataMissingRanges);
				
				if(meter.getMeterId() > 0) {
					
					DateRange dataAvailabeRange = getEnergyMeterStartAndEndTime(meter.getMeterId());
					
					List<DateRange> dataMissingRangesThatCanBeFilled = new ArrayList<>();
					
					if(dataAvailabeRange != null && dataMissingRanges != null && !dataMissingRanges.isEmpty()) {
						
						for(DateRange dataMissingRange :dataMissingRanges) {
							
							if((dataMissingRange.getEndTime() < dataAvailabeRange.getStartTime()) || (dataMissingRange.getStartTime() > dataAvailabeRange.getEndTime())) {
								continue;
							}
							DateRange dataMissingRangeThatCanBeFilled = new DateRange(dataMissingRange.getStartTime(),dataMissingRange.getEndTime());
							
							if(dataMissingRangeThatCanBeFilled.getStartTime() < dataAvailabeRange.getStartTime()) {
								dataMissingRangeThatCanBeFilled.setStartTime(dataAvailabeRange.getStartTime());
							}
							
							if(dataMissingRangeThatCanBeFilled.getEndTime() > dataAvailabeRange.getEndTime()) {
								dataMissingRangeThatCanBeFilled.setEndTime(dataAvailabeRange.getEndTime());
							}
							
							dataMissingRangesThatCanBeFilled.add(dataMissingRangeThatCanBeFilled);
						}
					}
					else if (dataAvailabeRange != null && (dataAvailableRanges == null || dataAvailableRanges.isEmpty())) {
						dataMissingRanges = new ArrayList<>();
						dataMissingRanges.add(dataAvailabeRange);
						meter.setEnergyStarDataMissingRanges(dataMissingRanges);
						dataMissingRangesThatCanBeFilled.add(dataAvailabeRange);
					}
					
					meter.setDataAvailableInFacilio(dataAvailabeRange);
					meter.setEnergyStarDataMissingRangesThatCanBeFilled(dataMissingRangesThatCanBeFilled);
				}
			}
			
		}
		return false;
	}
	
	private void fillMissingDateRangeString(EnergyStarMeterContext meter,long toalDataMissingTime) {
		if(toalDataMissingTime > 0) {
			Double month = Math.ceil(toalDataMissingTime/2628000000l);
			month = month == 0 ? 1 : month; 
			if(month > 11) {
				Double year = month/12;
				month =  month%12;
				
				String res = "";
				
				if(year.intValue() == 1) {
					res += year.intValue() +" Year";
				}
				else {
					res += year.intValue() +" Years";
				}
				
				if(month > 0 ) {
					if(month.intValue() == 1) {
						res += " "+month.intValue() +" Month";
					}
					else {
						res += " "+month.intValue() +" Months";
					}
				}
				meter.setMissingRangeString(res);
			}
			else {
				if(month.intValue() == 1) {
					meter.setMissingRangeString(month.intValue() +" Month");
				}
				else {
					meter.setMissingRangeString(month.intValue() +" Months");
				}
			}
		}
	}

	private DateRange getEnergyMeterStartAndEndTime(long meterId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule energydata = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioField ttimeField = fieldMap.get("ttime");
		
		FacilioField ttimeMin = NumberAggregateOperator.MIN.getSelectField(ttimeField);
		ttimeMin.setName("min");
		FacilioField ttimeMax = NumberAggregateOperator.MAX.getSelectField(ttimeField);
		ttimeMax.setName("max");
		
		List<FacilioField> selectFields = new ArrayList<>();
		
		selectFields.add(ttimeMin);
		selectFields.add(ttimeMax);
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(energydata.getTableName())
				.select(selectFields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meterId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), DateTimeUtil.getCurrenTime()+"", NumberOperators.LESS_THAN));
		
		 List<Map<String, Object>> result = select.get();
		
		if(result != null && !result.isEmpty()) {
			Map<String, Object> res = result.get(0);
			long min = (long) res.get("min");
			long max = (long) res.get("max");
			
			return new DateRange(min,max);
		}
		
		return null;
	}

	
	
	
}
