package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.unitconversion.UnitsUtil;

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
		
		Map<String, ReadingDataMeta> lastReadingMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.READING_DATA_META);
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			boolean useControllerDataInterval = useControllerDataInterval(readingMap);
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> fields= bean.getAllFields(moduleName);
				FacilioModule module = bean.getModule(moduleName);
				List<ReadingContext> readingsToBeAdded = addDefaultPropsAndGetReadingsToBeAdded(module, fields, readings, lastReadingMap, useControllerDataInterval, updateLastReading);
				addReadings(module, fields, readingsToBeAdded,lastReadingMap, updateLastReading);
			}
		}
		LOGGER.info("Time taken to add/update Readings data to DB : "+(System.currentTimeMillis() - startTime));
		context.put(FacilioConstants.ContextNames.RECORD_MAP, readingMap);
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		
		//Temp code. To be removed later
		startTime = System.currentTimeMillis();
		Record record = (Record) context.get(FacilioConstants.ContextNames.KINESIS_RECORD);
		if (record != null) {
			IRecordProcessorCheckpointer checkPointer = (IRecordProcessorCheckpointer) context.get(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER);
			checkPointer.checkpoint(record);
			LOGGER.info("Time taken to update checkpoint : "+(System.currentTimeMillis() - startTime));
		}
		return false;
	}
	
	private List<ReadingContext> addDefaultPropsAndGetReadingsToBeAdded(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings, Map<String, ReadingDataMeta> metaMap, boolean useControllerDataInterval, boolean updateLastReading) throws Exception {
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
			adjustTtime(module, reading, useControllerDataInterval);
			
			Map<String, Object> readingData = reading.getReadings();
			if (readingData != null && !readingData.isEmpty()) {
				
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				
				if(metaMap != null) {
					for(String fieldName : readingData.keySet()) {
						FacilioField field = fieldMap.get(fieldName);
						
						if(field != null && readingData.get(fieldName) != null) {
							LOGGER.error("before conv -- "+field.getId() +" ---- "+readingData.get(fieldName)); 
							if(reading.getParentId() > 0  && field.getId() > 0) {
								
								LOGGER.error("REACHED 1");
								String key = reading.getParentId()+"_"+field.getId();
								LOGGER.error("key 1 --- "+key);
								ReadingDataMeta readingDataMeta = metaMap.get(key);
								if(readingDataMeta != null && readingDataMeta.getUnitEnum() != null) {
									LOGGER.error("REACHED 2");
									Object value = UnitsUtil.convertToSiUnit(readingData.get(fieldName), readingDataMeta.getUnitEnum());
									readingData.put(fieldName, value);
									LOGGER.error("after conversion --" +field.getId() +" ---- "+value);
								}
							}
						}
					}
				}
				
				if(reading.getId() == -1) {
					reading.setNewReading(true);
					readingsToBeAdded.add(reading);
				}
				else {
					reading.setNewReading(false);
					updateReading(module, fields, reading, metaMap, updateLastReading);
				}
			}
			else {
				itr.remove();
			}
		}
		return readingsToBeAdded;
	}
	
	private void adjustTtime(FacilioModule module, ReadingContext reading, boolean useControllerDataInterval) {
		reading.setActualTtime(reading.getTtime());
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
			Map<String, ReadingDataMeta> metaMap, boolean isUpdateLastReading) throws Exception {
		
//		System.err.println( Thread.currentThread().getName()+"Inside addReadings in  AddorUpdateCommand#######  "+readings);

		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(readings);
		readingBuilder.save();
		if (isUpdateLastReading) {
			ReadingsAPI.updateReadingDataMeta(fields,readings,metaMap);
		}
//		System.err.println( Thread.currentThread().getName()+"Exiting addReadings in  AddorUpdateCommand#######  ");

	}
	
	private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading,
			Map<String, ReadingDataMeta> metaMap, boolean isUpdateLastReading) throws Exception {
		System.err.println( Thread.currentThread().getName()+"Inside updateReadings in  AddorUpdateCommand#######  "+reading);

		UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		updateBuilder.update(reading);
		if (isUpdateLastReading) {
			ReadingsAPI.updateReadingDataMeta(fields,Collections.singletonList(reading),metaMap);
		}
		
		System.err.println( Thread.currentThread().getName()+"Exiting updateReadings in  AddorUpdateCommand#######  ");

	}
}
