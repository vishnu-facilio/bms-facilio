package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetLatestCategoryReadingValuesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if(modules != null && !modules.isEmpty() && parentId != -1) {
			Map<String, List<ReadingContext>> readingData = new HashMap<>();
			for(FacilioModule module : modules) {
				int count = (int) context.get(FacilioConstants.ContextNames.LIMIT_VALUE);
				if(count == -1) {
					count = 1;
				}
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(module.getName());
				
				Condition idCondition = new Condition();
				idCondition.setField(modBean.getField("parentId", module.getName()));
				idCondition.setOperator(NumberOperators.EQUALS);
				idCondition.setValue(String.valueOf(parentId));
				
				SelectRecordsBuilder<ReadingContext> readingBuilder = new SelectRecordsBuilder<ReadingContext>()
																			.module(module)
																			.beanClass(ReadingContext.class)
																			.select(fields)
																			.andCondition(idCondition)
																			.orderBy("TTIME DESC")
																			.limit(count);
				
				List<ReadingContext> readings = readingBuilder.get();
				if(readings != null && !readings.isEmpty()) {
					readingData.put(module.getName(), readings);
				}
			}
			context.put(FacilioConstants.ContextNames.READINGS, readingData);
		}
		return false;
	}

}
