package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.InstantJob;
import com.facilio.timeseries.TimeSeriesAPI;

public class ProcessUnmodelledHistoricalDataJob extends InstantJob {
	
	private static final Logger LOGGER = LogManager.getLogger(ProcessUnmodelledHistoricalDataJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		
		try {
			String device = (String) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
			String instance = (String) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
			long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
			List<Map<String, Object>> unmodelledData = TimeSeriesAPI.getUnmodeledData(device, instance, controllerId);
			if (unmodelledData != null && !unmodelledData.isEmpty()) {
				
				long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
				long fieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
				
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field =bean.getField(fieldId);
				
				List<ReadingContext> readings = new ArrayList<>();
				unmodelledData.forEach(data -> {
					
					ReadingContext reading=new ReadingContext();
					reading.addReading(field.getName(), data.get("value"));
					reading.setParentId(assetId);
					reading.setTtime((long) data.get("ttime"));
					readings.add(reading);
				});
				
				FacilioContext readingsContext = new FacilioContext();
				readingsContext.put(FacilioConstants.ContextNames.READINGS,readings);
				readingsContext.put(FacilioConstants.ContextNames.HISTORY_READINGS, true);
				readingsContext.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
				context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
				readingsContext.put(FacilioConstants.ContextNames.MODULE_NAME,field.getModule().getName());
				
				Chain addReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
				addReading.execute(readingsContext);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during execution of ProcessUnmodelledHistoricalDataJob", e);
			CommonCommandUtil.emailException("MigrateReadingDataJob", "Error occurred during execution of ProcessUnmodelledHistoricalDataJob", e);
		}
	}

}
