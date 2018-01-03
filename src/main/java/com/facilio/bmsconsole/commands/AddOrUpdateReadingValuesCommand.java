package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.workflow.ActionType;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateReadingValuesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
		
		if(readings != null && !readings.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			List<ReadingContext> readingsToBeAdded = new ArrayList<>();
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
					updateReading(module, fields, reading);
				}
			}
			addReadings(module, fields, readingsToBeAdded);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, readingsToBeAdded);
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		}
		return false;
	}
	
	private void addReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings) throws Exception {
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(readings);

		readingBuilder.save();
	}
	
	private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading) throws Exception {
		UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		
		updateBuilder.update(reading);
	}

}
