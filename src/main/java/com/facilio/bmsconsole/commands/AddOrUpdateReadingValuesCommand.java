package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateReadingValuesCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		Boolean updateLastReading = (Boolean) context.get(FacilioConstants.ContextNames.UPDATE_LAST_READINGS);
		if (updateLastReading == null) {
			updateLastReading = true;
		}
		Map<String, Map<String,Object>> lastReadingMap =(Map<String, Map<String,Object>>)context.get(FacilioConstants.ContextNames.LAST_READINGS);
		
		System.out.println("Debug insert reading : ");
		System.out.println(readingMap);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				
				System.out.println("Debug insert reading moduleName : "+moduleName);
				
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> fields= bean.getAllFields(moduleName);
				FacilioModule module = bean.getModule(moduleName);
		
				List<ReadingContext> readingsToBeAdded = new ArrayList<>();
				System.out.println("Debug insert reading values : ");
				for(ReadingContext reading : readings) {
					if(reading.getTtime() == -1) {
						reading.setTtime(System.currentTimeMillis());
					}
					if(reading.getParentId() == -1) {
						throw new IllegalArgumentException("Invalid parent id for readings of module : "+moduleName);
					}
		
					if(reading.getId() == -1) {
						readingsToBeAdded.add(reading);
					}
					else {
						updateReading(module, fields, reading,lastReadingMap, updateLastReading);
					}
					System.out.println(reading.getReadings());
				}
				addReadings(module, fields, readingsToBeAdded,lastReadingMap, updateLastReading);
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_MAP, readingMap);
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		return false;
	}
	
	private void addReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings,
			Map<String, Map<String,Object>> lastReadingMap, boolean isUpdateLastReading) throws Exception {
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(readings);
		readingBuilder.save();
		if (isUpdateLastReading) {
			ReadingsAPI.updateLastReading(fields,readings,lastReadingMap);
		}
	}
	
	private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading,
			Map<String, Map<String,Object>> lastReadingMap, boolean isUpdateLastReading) throws Exception {
		UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		updateBuilder.update(reading);
		if (isUpdateLastReading) {
			ReadingsAPI.updateLastReading(fields,Collections.singletonList(reading),lastReadingMap);
		}
	}
}
