package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.SecondsChronoUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(VirtualMeterEnergyDataCalculator.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
			if(virtualMeters == null || virtualMeters.isEmpty()) {
				return;
			}
			int minutesInterval = getDefaultDataInterval();
			long endTime = DateTimeUtil.getDateTime(System.currentTimeMillis()).truncatedTo(new SecondsChronoUnit(minutesInterval * 60)).toInstant().toEpochMilli();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
			List<Long> completedVms= new ArrayList<Long>();
			List<Long> vmList=getVmList(virtualMeters);
			Map <Long, List<Long>> childMeterMap= new HashMap<Long,List<Long>> ();
			
			while (!virtualMeters.isEmpty()) {
				Iterator<EnergyMeterContext> itr = virtualMeters.iterator();
				while (itr.hasNext()) {
					EnergyMeterContext meter = itr.next();
					long meterId=meter.getId();
					try {
						List<Long> childMeterIds=childMeterMap.get(meterId);
						if(childMeterIds==null) {
							childMeterIds=DeviceAPI.getChildrenMeters(meter);
						}
						if(childMeterIds!=null) {

							//check any childMeter is a VM..
							List<Long> vmChildren= getVmChildren(vmList,childMeterIds);
							if(!vmChildren.isEmpty() && !isCompleted(completedVms,vmChildren)) {
								childMeterMap.put(meterId, childMeterIds);
								continue;
							}
							ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(meterId, deltaField);
							long startTime = meta.getTtime()+1;
							DeviceAPI.insertVirtualMeterReadings(meter,childMeterIds,startTime,endTime,minutesInterval,true, false);
						}

					}
					catch (Exception e) {
						LOGGER.info("Exception occurred ", e);
						CommonCommandUtil.emailException("VMEnergyDataCalculatorForMeter", "VM Calculation failed for meter : "+meterId, e);
					}
					completedVms.add(meter.getId());
					itr.remove();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred ", e);
			CommonCommandUtil.emailException("VMEnergyDataCalculator", "VM Calculation failed", e);
		}
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
	
	private List<Long> getVmList(List<EnergyMeterContext> virtualMeters){
		
		List<Long> vmList= new ArrayList<Long>();
		for(EnergyMeterContext meter:virtualMeters) {
			
			vmList.add( meter.getId());
		}
		return vmList;
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
	
	private boolean isCompleted(List<Long> completedList, List<Long> children) {
		
		for (Long id: children) {
			
			if(!completedList.contains(id)) {
				return false;
			}
		}
		return true;
	}
}
