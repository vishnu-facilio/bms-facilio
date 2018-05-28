package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
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
import com.facilio.bmsconsole.modules.FieldFactory;
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
				List<FacilioField> fields = new ArrayList<>();
				Map<String, FacilioField> defaultFieldMap = FieldFactory.getAsMap(FieldFactory.getDefaultReadingFields(module));
				fields.add(defaultFieldMap.get("ttime"));
				fields.add(defaultFieldMap.get("actualTtime"));
				
				Condition idCondition = new Condition();
				idCondition.setField(modBean.getField("parentId", module.getName()));
				idCondition.setOperator(NumberOperators.EQUALS);
				idCondition.setValue(String.valueOf(parentId));
				
				for(FacilioField field : module.getFields()) {
					fields.add(field);
					SelectRecordsBuilder<ReadingContext> readingBuilder = new SelectRecordsBuilder<ReadingContext>()
																				.module(module)
																				.beanClass(ReadingContext.class)
																				.select(fields)
																				.andCondition(idCondition)
																				.andCustomWhere(field.getColumnName()+" IS NOT NULL")
																				.orderBy("TTIME DESC")
																				.limit(count);
					
					List<ReadingContext> readings = readingBuilder.get();
					if(readings != null && !readings.isEmpty()) {
						readingData.put(field.getName(), readings);
					}
					fields.remove(field);
				}
			}
			context.put(FacilioConstants.ContextNames.READINGS, readingData);
		}
		return false;
	}

}
