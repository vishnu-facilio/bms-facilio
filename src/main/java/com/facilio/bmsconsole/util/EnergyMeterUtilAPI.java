package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.CreateFormulaFromVMCommand;
import com.facilio.bmsconsole.commands.ExecuteSpecificWorkflowsCommand;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.jobs.FormulaLeafTriggerJob;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;

public class EnergyMeterUtilAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(EnergyMeterUtilAPI.class.getName());
	
	public static void runHistoricalVMBasedonHierarchyWithoutLoggers (long startTime, long endTime, List<Long> vmList) throws Exception
	{
		long executionStartTime = System.currentTimeMillis();	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);

		List<EnergyMeterContext> virtualMeters = new ArrayList<EnergyMeterContext>();
		
		if(vmList == null || vmList.isEmpty()) {
			virtualMeters = DeviceAPI.getAllVirtualMeters();
		}
		else
		{
			virtualMeters = DeviceAPI.getVirtualMeters(vmList);
		}
	
		if(virtualMeters == null || virtualMeters.isEmpty()) {
			return;
		}
		LOGGER.info("VirtualMetersList Size while calculating historical data without job -- " + virtualMeters.size());
		
		Map<Long,EnergyMeterContext> virtualEnergyMeterContextMap = new HashMap<Long,EnergyMeterContext>();
		for(EnergyMeterContext vm:virtualMeters) {
			virtualEnergyMeterContextMap.put(vm.getId(), vm);
		}

		int i=0;
		Map <Long, List<Long>> childMeterIdMap= new HashMap<Long,List<Long>>();
		Map<Integer,List<EnergyMeterContext>> hierarchyVMMap = new HashMap<Integer, List<EnergyMeterContext>>();
				
		for(EnergyMeterContext vm:virtualMeters)
		{			
			Integer hierarchy = DeviceAPI.getVMHierarchy(vm, 0, virtualEnergyMeterContextMap, childMeterIdMap);
			if(hierarchy != null)
			{
				if(hierarchyVMMap.containsKey(hierarchy))
				{
					List<EnergyMeterContext> groupedVMList = hierarchyVMMap.get(hierarchy);
					groupedVMList.add(vm);
				}
				else
				{
					List<EnergyMeterContext> groupedVMList = new ArrayList<EnergyMeterContext>();
					groupedVMList.add(vm);
					hierarchyVMMap.put(hierarchy, groupedVMList);
				}		
			}
			
		}
		
		if(MapUtils.isNotEmpty(hierarchyVMMap))
		{
			Map<Integer,List<EnergyMeterContext>> sortedHierarchyVMMap = new TreeMap<Integer,List<EnergyMeterContext>>(hierarchyVMMap); 
			
			for(Integer hierarchy:sortedHierarchyVMMap.keySet())
			{		
				List<EnergyMeterContext> groupedVMList = sortedHierarchyVMMap.get(hierarchy);
				LOGGER.info(" VM Hierarchy -- " + hierarchy + " VMs --" + groupedVMList +" GroupedVMList Size -- " + groupedVMList.size());
			}
								
			for(Integer hierarchy:sortedHierarchyVMMap.keySet())
			{		
				List<EnergyMeterContext> groupedVMList = sortedHierarchyVMMap.get(hierarchy);
				
				for(EnergyMeterContext vm:groupedVMList)
				{
					int interval = ReadingsAPI.getDataInterval(vm.getId(), deltaField);
					DeviceAPI.insertVirtualMeterReadings(vm, childMeterIdMap.get(vm.getId()), startTime, endTime, interval,false, true);		
			    	LOGGER.info("Readings Inserted for VM Id -- "+vm.getId());
			    	LOGGER.info("Inserted VMs count-- "+ ++i);
				}					
			}
			
			LOGGER.info(" VM Calculation Timetaken -- " + (System.currentTimeMillis()-executionStartTime));		
		}	
	}
	
	public static void markDataGapforHistoricalPeriod (long startTime, long endTime, List<Long> vmList) {
		
		try {		
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);

			if(vmList != null && !vmList.isEmpty()) 
			{
				LOGGER.info("VM Marking for data gap Start --------------- No.of VMs --- "+vmList.size());			
				for(Long vmId: vmList)
				{
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
						ReadingContext currentReading, previousReading;
						Long currentTime, previousTime; 
						for (int i=1;i<readingsSize;++i)
						{
							currentReading = readings.get(i);
							previousReading = readings.get(i-1);					
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
			}
			catch(Exception e) {
				LOGGER.error("Exception while cheking data Gap", e);
			}
			
		}
	
	public static void runMig() throws Exception {
		AccountUtil.setCurrentAccount(1l);

		JobContext jc = new JobContext();
		jc.setJobId(1l);

		FormulaLeafTriggerJob fLeaf = new FormulaLeafTriggerJob();
		fLeaf.execute(jc);
	}
	
	public static void convertVMToFormulaMig() throws Exception {
		
		List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
		if(virtualMeters != null && !virtualMeters.isEmpty()) {
			for(EnergyMeterContext vm:virtualMeters) 
			{
				FacilioChain chain = FacilioChain.getTransactionChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.RECORD, vm);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ENERGY_DATA_READING);
				chain.addCommand(new CreateFormulaFromVMCommand());
				chain.execute();
			}
		}
		
	}
	

}
