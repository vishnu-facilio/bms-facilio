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
	
    public static void runHistoricalForRMZ() throws Exception{
    	
    	try {
    		    	
	    	List<Integer> VMstoRun = Arrays.asList(81453, 81454, 81455, 81456, 81457, 81458, 81459, 81460, 81461, 81462, 81463, 81464, 81465, 81466, 81467, 81468, 81469, 81470, 81471, 81472, 81473, 81482, 275380, 275381, 275382, 275383, 275384, 275386, 275387, 275388, 275389, 275390, 275391, 275392, 275393, 275394, 275395, 275396, 275397, 275398, 275399, 275400, 275401, 275402, 275403, 275404, 275405, 275406, 275407, 275408, 275409, 275410, 275411, 275412, 275413, 275414, 275415, 275416, 275417, 275418, 275419, 275420, 275421, 275422, 275423, 275424, 275425, 566149, 566150, 586158, 586159, 586160, 586161, 586162, 586163, 586164, 586165, 586166, 586167, 882535, 882536, 882537, 882538, 882539, 882540, 882541, 882542, 882543, 882544, 882545, 882546, 882547, 882548, 882549, 882550, 882551, 882552, 882553, 882554, 882555, 882556, 882557, 882558, 882559, 882560, 882561, 882562, 882563, 882564, 882565, 882566, 882567, 882568, 882569, 882570, 882571, 882572, 882573, 882574, 882575, 882576, 882577, 882578, 882579, 882580, 890284, 1067849, 1067851, 1108562, 1108563, 1110302, 1110303, 1110304, 1110305, 1114798, 1114799, 1122276, 1122277);
						
			long startTime = 1573324201000L;
			long endTime = 1574706599000L;
	
			int noOfVms = VMstoRun.size();
			LOGGER.info("VM Mig Start --------------- No.of VMs --- "+noOfVms);
			
			List<Long> vmList = new ArrayList<Long>(noOfVms);
			for (int i=0;i<noOfVms;++i) {
				vmList.add(VMstoRun.get(i).longValue());
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			
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
			}
	
	    	LOGGER.info("VM Mig End ---------------");
    	
    	}
    	
    	catch (Exception VMException) {			
    		LOGGER.info("Exception Occurred ---" + VMException);
    		LOGGER.error(VMException.getMessage(), VMException);
		}
  	        
    }
}
