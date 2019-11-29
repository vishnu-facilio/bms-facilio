package com.facilio.bmsconsole.commands;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.mysql.UpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.jobs.VirtualMeterEnergyDataCalculator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Random;


public class HistoricalVMForRMZ {

	private static final Logger LOGGER = LogManager.getLogger(VirtualMeterEnergyDataCalculator.class.getName());
	
    public static void runHistoricalForRMZ(List<String> VMstoRun) throws Exception{
    	
    	try {
    		    	
	    	long startTime = 1573324201000L;
			long endTime = 1574706599000L;
	
			int noOfVms = VMstoRun.size();
			LOGGER.info("VM Mig Start --------------- No.of VMs --- "+noOfVms);
			
			List<Long> vmList = new ArrayList<Long>(noOfVms);
		
			for (int i=0;i<noOfVms;++i) {
				vmList.add(Long.parseLong(VMstoRun.get(i).trim()));
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
			int i=0;
			for(Long vmId: vmList)
			{	
				EnergyMeterContext meter = DeviceAPI.getEnergyMeter(vmId);
				List<Long> childMeterIds = DeviceAPI.getChildrenMeters(meter);
				int interval = ReadingsAPI.getDataInterval(meter.getId(), energyField);
			
				if(meter != null && childMeterIds != null)
				{
					DeviceAPI.insertVirtualMeterReadings(meter, childMeterIds, startTime, endTime, interval,false, true);
				}	
				
		    	LOGGER.info("Readings Inserted for VM Id -- "+vmId);
		    	LOGGER.info("Inserted VMs count-- "+ ++i);
			}
	
	    	LOGGER.info("VM Mig End ---------------");
    	
    	}
    	
    	catch (Exception VMException) {			
    		LOGGER.info("Exception Occurred ---" + VMException);
    		LOGGER.error(VMException.getMessage(), VMException);
		}
  	        
    }
    
	public static float getDataGapCount ()  {
		
		try {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
			long startTime = 1573324201000L;
			long endTime = 1574706599000L;
			
			List<Integer> VMstoRun = Arrays.asList(882590,81483);
			
			int noOfVms = VMstoRun.size();
			LOGGER.info("VM Marking for data gap End --------------- No.of VMs --- "+noOfVms);
			
			List<Long> vmList = new ArrayList<Long>(noOfVms);
			
			for (int i=0;i<noOfVms;++i) {
				vmList.add(VMstoRun.get(i).longValue());
			}
					
			for(Long vmId: vmList)
			{
				Long previousTime, currentTime;
				SelectRecordsBuilder<ReadingContext> selectbuilder = new SelectRecordsBuilder<ReadingContext>()
						.beanClass(ReadingContext.class).moduleName(FacilioConstants.ContextNames.ENERGY_DATA_READING)
						.select(fields)
						.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentId", "" + vmId, NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", ""+startTime,NumberOperators.GREATER_THAN_EQUAL))
						.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", ""+endTime,NumberOperators.LESS_THAN_EQUAL))
						.orderBy("TTIME");
				
				List<ReadingContext> readings = selectbuilder.get();
				
				if(readings != null && !readings.isEmpty())
				{
					int readingsSize = readings.size();
					for (int i=1;i<readingsSize;++i)
					{
						ReadingContext currentReading = readings.get(i);
						ReadingContext previousReading = readings.get(i-1);					
						if(currentReading != null && previousReading != null)
						{
							previousTime = previousReading.getTtime();
							currentTime = currentReading.getTtime();			
							if(previousTime != null && currentTime != null)
							{
								long dataIntervalSeconds=ReadingsAPI.getDataInterval(vmId, energyField, module)*60;
								SecondsChronoUnit defaultAdjustUnit = new SecondsChronoUnit(dataIntervalSeconds);
								ZonedDateTime zdt=	DateTimeUtil.getDateTime(currentTime).truncatedTo(defaultAdjustUnit);
								
								long timeDiff=DateTimeUtil.getMillis(zdt, true)-previousTime;								
								float gapCount=timeDiff/(dataIntervalSeconds*1000);
								
								if(gapCount > 1)
								{
									currentReading.setMarked(true);
									FacilioField markedField = modBean.getField("marked", FacilioConstants.ContextNames.ENERGY_DATA_READING);									
									GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(Collections.singletonList(markedField))
											.andCondition(CriteriaAPI.getCondition("ID", "id", ""+currentReading.getId(), NumberOperators.EQUALS));

									Map<String, Object> props = FieldUtil.getAsProperties(currentReading);
									updateBuilder.update(props);
									
									LOGGER.info("Updated for --- "+currentReading.getId());

								}								
							}
						}									
					}			
				}			
			}
			LOGGER.info("VM Marking for data gap End ---");
		}
		catch(Exception e) {
			LOGGER.error("Exception while cheking data Gap", e);
		}
		return 1;
	}
}
