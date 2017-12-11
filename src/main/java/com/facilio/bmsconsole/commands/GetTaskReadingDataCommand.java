package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetTaskReadingDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null && task.isReadingTask()) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField readingField = modBean.getField(task.getReadingFieldId());
			task.setReadingField(readingField);
			if(task.getReadingDataId() != -1) {
				FacilioModule readingModule = readingField.getModule();
				SelectRecordsBuilder<ReadingContext> readingBuilder = new SelectRecordsBuilder<ReadingContext>()
																			.select(modBean.getAllFields(readingModule.getName()))
																			.module(readingModule)
																			.beanClass(ReadingContext.class)
																			.andCondition(CriteriaAPI.getIdCondition(task.getReadingDataId(), readingModule));
				
				List<ReadingContext> readings = readingBuilder.get();
				if(readings != null && !readings.isEmpty()) {
					task.setReadingData(readings.get(0));
				}
			}
		}
		return false;
	}

}
