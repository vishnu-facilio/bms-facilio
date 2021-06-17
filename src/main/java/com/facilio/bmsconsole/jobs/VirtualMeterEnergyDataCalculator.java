package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(VirtualMeterEnergyDataCalculator.class.getName());
	
	private boolean timedOut = false;
	
	public void execute(JobContext jc) throws Exception {
		try {	
			long jobStartTime = System.currentTimeMillis();			
			List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
			if(virtualMeters == null || virtualMeters.isEmpty()) {
				return;
			}
			int minutesInterval = getDefaultDataInterval();
			long endTime = DateTimeUtil.getDateTime(System.currentTimeMillis()).truncatedTo(new SecondsChronoUnit(minutesInterval * 60)).toInstant().toEpochMilli();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
			Map<Long,EnergyMeterContext> virtualEnergyMeterContextMap = new HashMap<Long,EnergyMeterContext>();
			for(EnergyMeterContext vm:virtualMeters) {
				virtualEnergyMeterContextMap.put(vm.getId(), vm);
			}
			LOGGER.info("VirtualMetersList Size -- " + virtualMeters.size() + " Job Id --" + jc.getJobId());
			Map <Long, List<Long>> childMeterIdMap= new HashMap<Long,List<Long>>();
			Map<Integer,List<EnergyMeterContext>> hierarchyVMMap = new HashMap<Integer, List<EnergyMeterContext>>();
					
			for(EnergyMeterContext vm:virtualMeters)
			{			
				Integer hierarchy = getHierarchy(vm, 0, virtualEnergyMeterContextMap, childMeterIdMap);
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
					LOGGER.info(" VM Job Hierarchy -- " + hierarchy + " VMs --" + groupedVMList + " Job Id --" + jc.getJobId());
					
					List<ReadingContext> hierarchicalVMReadings = new ArrayList<ReadingContext>();
					List<MarkedReadingContext> hierarchicalMarkedList = new ArrayList<MarkedReadingContext>();
					
					for(EnergyMeterContext vm:groupedVMList)
					{						
						ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(vm.getId(), deltaField); 
						long startTime = meta.getTtime()+1;
						List<ReadingContext> vmReadings = DeviceAPI.getandDeleteDuplicateVirtualMeterReadings(vm,childMeterIdMap.get(vm.getId()),startTime,endTime,minutesInterval,true, false);
						if (vmReadings != null) {
							hierarchicalVMReadings.addAll(vmReadings); 		
						}
						List<MarkedReadingContext> markedList= DeviceAPI.validatedataGapCountForVMReadings(vmReadings, vm, false);										
						if (vmReadings != null && markedList != null) {
							hierarchicalMarkedList.addAll(markedList); //Grouping readings for all the meters in the same hierarchy	
						}	
						
						if (AccountUtil.getCurrentOrg().getId() == 231) {
							LOGGER.info(" VM live readings starttime -- " + startTime + " VM Id --" + vm.getId() +" Job Id --" + jc.getJobId());
							LOGGER.info(" VM live readings endTime -- " + endTime + " VM Id --" + vm.getId() +" Job Id --" + jc.getJobId());
						}
					}
					
					LOGGER.info(" GroupedVMList Size -- " + groupedVMList.size() + " Job Id --" + jc.getJobId());
					LOGGER.info(" hierarchicalVMReadings Size -- " + hierarchicalVMReadings.size() + " Job Id --" + jc.getJobId());
					
					if (AccountUtil.getCurrentOrg().getId() == 231) {
						LOGGER.info(" VM live readings at hierarchy endtime -- " + endTime + " hierarchy Id --" + hierarchy +" Job Id --" + jc.getJobId());
					}
					
					DeviceAPI.insertVMReadingsBasedOnHierarchy(hierarchicalVMReadings,endTime,minutesInterval,true, false, hierarchicalMarkedList);						
				}
				LOGGER.info(" VM Job Timetaken -- " + (System.currentTimeMillis()-jobStartTime) + " Job Id --" + jc.getJobId());			
			}
				
		} catch (Exception e) {
			LOGGER.error("Exception occurred ", e);
			CommonCommandUtil.emailException("VMEnergyDataCalculator", "VM Calculation failed", e);
		}
	}
	
	private Integer getHierarchy(EnergyMeterContext vm, Integer hierarchy, Map<Long,EnergyMeterContext> virtualEnergyMeterContextMap, 
			 Map <Long, List<Long>> childMeterIdMap) throws Exception {
		
		if(timedOut) {
			return null;
		}
		hierarchy++;
		long vmId = vm.getId();
		
		List<Long> childMeterIds = childMeterIdMap.get(vmId);
		if(childMeterIds==null) {
			childMeterIds=DeviceAPI.getChildrenMeters(vm);
			childMeterIdMap.put(vmId, childMeterIds);
		}

		if(childMeterIds != null) 
		{			
			List<Long> vmChildren = getVmChildren(new ArrayList<Long>(virtualEnergyMeterContextMap.keySet()),childMeterIds);                
			if(vmChildren == null || vmChildren.isEmpty()) {
				return hierarchy;									//check if the children for that VM is a VM, if not, return hierarchy
			}
			else
			{
				List<Integer> hierarchyMaxList = new ArrayList<Integer>();
				for (Long vmid:vmChildren)
				{
					hierarchyMaxList.add(getHierarchy(virtualEnergyMeterContextMap.get(vmid), hierarchy, virtualEnergyMeterContextMap,  childMeterIdMap));
				}
				return Collections.max(hierarchyMaxList);				 
			}
		}
		return hierarchy;
	}

	private int getDefaultDataInterval() throws Exception {
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp == null || defaultIntervalProp.isEmpty()) {
			return ReadingsAPI.DEFAULT_DATA_INTERVAL;
		}
		else {
			return Integer.parseInt(defaultIntervalProp);
		}
	}		
	private List<Long> getVmChildren(List<Long> vmList, List<Long> children) {
		List<Long> childrenVms= new ArrayList<Long> ();
		
		for (Long id: children) {
			
			if(vmList.contains(id)) {
				childrenVms.add(id);
			}
		}
		return childrenVms;
	}
	
	public void handleTimeOut() {
		LOGGER.info("VM Energy Job calculator timed out!!");
		timedOut = true;
		super.handleTimeOut();
	}
}
