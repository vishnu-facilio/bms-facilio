package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.time.SecondsChronoUnit;

public class AddOrUpdateReadingValuesCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(AddOrUpdateReadingValuesCommand.class.getName());
	private SecondsChronoUnit defaultAdjustUnit = null;
	private Map<Long, ControllerContext> controllers = null;
	private Map<Long, ResourceContext> resources = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		Boolean updateLastReading = (Boolean) context.get(FacilioConstants.ContextNames.UPDATE_LAST_READINGS);
		if (updateLastReading == null) {
			updateLastReading = true;
		}
//		System.err.println( Thread.currentThread().getName()+"Inside AddorUpdateCommand#######  "+readingMap);
		Boolean adjustTime = (Boolean) context.get(FacilioConstants.ContextNames.ADJUST_READING_TTIME);
		if (adjustTime == null) {
			adjustTime = true;
		}
		
		SourceType sourceType = (SourceType) context.get(FacilioConstants.ContextNames.READINGS_SOURCE);
		if (AccountUtil.getCurrentOrg().getId() == 134) {
			LOGGER.info("Adding readings from source : "+sourceType);
		}
		
		Map<String, ReadingDataMeta> lastReadingMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			boolean useControllerDataInterval = useControllerDataInterval(readingMap);
			Map<String, ReadingDataMeta> currentReadingMap = new HashMap<>();
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> fields= bean.getAllFields(moduleName);
				FacilioModule module = bean.getModule(moduleName);
				List<ReadingContext> readingsToBeAdded = addDefaultPropsAndGetReadingsToBeAdded(module, fields, readings, lastReadingMap, currentReadingMap, adjustTime, useControllerDataInterval, updateLastReading, sourceType);
				addReadings(module, fields, readingsToBeAdded,lastReadingMap, currentReadingMap, updateLastReading);
			}
			context.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, currentReadingMap);
		}
		LOGGER.info("Time taken to add/update Readings data to DB : "+(System.currentTimeMillis() - startTime));
		context.put(FacilioConstants.ContextNames.RECORD_MAP, readingMap);
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		
		return false;
	}
	
	private List<ReadingContext> addDefaultPropsAndGetReadingsToBeAdded(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings, Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean adjustTime, boolean useControllerDataInterval, boolean updateLastReading, SourceType sourceType) throws Exception {
		List<ReadingContext> readingsToBeAdded = new ArrayList<>();
		Iterator<ReadingContext> itr = readings.iterator();
		while (itr.hasNext()) {
			ReadingContext reading = itr.next();
			if(reading.getTtime() == -1) {
				reading.setTtime(System.currentTimeMillis());
			}
			if(reading.getParentId() == -1) {
				throw new IllegalArgumentException("Invalid parent id for readings of module : "+module.getName());
			}
			
			reading.setActualTtime(reading.getTtime());
			if (adjustTime) {
				adjustTtime(module, reading, useControllerDataInterval);
			}
			Map<String, Object> readingData = reading.getReadings();
			if (readingData != null && !readingData.isEmpty()) {
				if(reading.getId() == -1) {
					reading.setNewReading(true);
					readingsToBeAdded.add(reading);
				}
				else {
					reading.setNewReading(false);
					updateReading(module, fields, reading, metaMap, currentReadingMap, updateLastReading);
				}
				reading.setSourceType(sourceType);
			}
			else {
				itr.remove();
			}
		}
		return readingsToBeAdded;
	}
	
	private void adjustTtime(FacilioModule module, ReadingContext reading, boolean useControllerDataInterval) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(reading.getTtime());
		
		if (module.getDateIntervalUnit() != null) {
			zdt = zdt.truncatedTo(module.getDateIntervalUnit());
		}
		else {
			if (useControllerDataInterval) {
				ResourceContext parent = resources.get(reading.getParentId());
				if (parent.getControllerId() != -1) {
					ControllerContext controller = controllers.get(parent.getControllerId());
					if (controller.getDateIntervalUnit() != null) {
						zdt = zdt.truncatedTo(controller.getDateIntervalUnit());
					}
					else {
						zdt = zdt.truncatedTo(defaultAdjustUnit);
					}
				}
				else {
					zdt = zdt.truncatedTo(defaultAdjustUnit);
				}
			}
			else {
				zdt = zdt.truncatedTo(defaultAdjustUnit);
			}
		}
		reading.setTtime(DateTimeUtil.getMillis(zdt, true));
	}
	
	private boolean useControllerDataInterval(Map<String, List<ReadingContext>> readingMap) throws Exception {
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL, FacilioConstants.OrgInfoKeys.USE_CONTROLLER_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp == null || defaultIntervalProp.isEmpty()) {
			defaultAdjustUnit = ReadingsAPI.DEFAULT_DATA_INTERVAL_UNIT;
		}
		else {
			defaultAdjustUnit = new SecondsChronoUnit(Long.parseLong(defaultIntervalProp) * 60);
		}
		
		boolean useControllerDataInterval = Boolean.valueOf(orgInfo.get(FacilioConstants.OrgInfoKeys.USE_CONTROLLER_DATA_INTERVAL));
		if (useControllerDataInterval) {
			fetchControllerAndResources(readingMap);
		}
		return useControllerDataInterval;
	}
	
	private void fetchControllerAndResources (Map<String, List<ReadingContext>> readingMap) throws Exception {
		controllers = DeviceAPI.getAllControllersAsMap();
		
		Set<Long> resourceIds = new HashSet<>();
		for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
			List<ReadingContext> readings = entry.getValue();
			if (readings != null) {
				for (ReadingContext reading : readings) {
					resourceIds.add(reading.getParentId());
				}
			}
		}
		resources = ResourceAPI.getResourceAsMapFromIds(resourceIds);
	}
	
	private void addReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings,
			Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean isUpdateLastReading) throws Exception {
		
//		System.err.println( Thread.currentThread().getName()+"Inside addReadings in  AddorUpdateCommand#######  "+readings);

		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(readings);
		readingBuilder.save();
		if (isUpdateLastReading) {
			Map<String, ReadingDataMeta> currentRDMs = ReadingsAPI.updateReadingDataMeta(fields,readings,metaMap);
			if (currentRDMs != null) {
				currentReadingMap.putAll(currentRDMs);
			}
		}
//		System.err.println( Thread.currentThread().getName()+"Exiting addReadings in  AddorUpdateCommand#######  ");

	}
	
	private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading,
			Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean isUpdateLastReading) throws Exception {
		System.err.println( Thread.currentThread().getName()+"Inside updateReadings in  AddorUpdateCommand#######  "+reading);

		UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		updateBuilder.update(reading);
		if (isUpdateLastReading) {
			Map<String, ReadingDataMeta> currentRDMs = ReadingsAPI.updateReadingDataMeta(fields,Collections.singletonList(reading),metaMap);
			if (currentRDMs != null) {
				currentReadingMap.putAll(currentRDMs);
			}
		}
		
		System.err.println( Thread.currentThread().getName()+"Exiting updateReadings in  AddorUpdateCommand#######  ");

	}
}
