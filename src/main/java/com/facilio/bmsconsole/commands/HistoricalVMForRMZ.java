package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.jobs.VirtualMeterEnergyDataCalculator;

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
}
