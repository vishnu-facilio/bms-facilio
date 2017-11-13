package com.facilio.bmsconsole.commands;

import java.util.List;

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

public class GetLatestReadingValuesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if(moduleName != null && !moduleName.isEmpty() && parentId != -1) {
			int count = (int) context.get(FacilioConstants.ContextNames.LIMIT_VALUE);
			if(count == -1) {
				count = 1;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			Condition idCondition = new Condition();
			idCondition.setField(modBean.getField("parentId", moduleName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(String.valueOf(parentId));
			
			SelectRecordsBuilder<ReadingContext> readingBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.module(module)
																		.beanClass(ReadingContext.class)
																		.select(fields)
																		.andCondition(idCondition)
																		.orderBy("TTIME DESC")
																		.limit(count);
			
			context.put(FacilioConstants.ContextNames.READINGS, readingBuilder.get());
			
		}
		return false;
	}

}
