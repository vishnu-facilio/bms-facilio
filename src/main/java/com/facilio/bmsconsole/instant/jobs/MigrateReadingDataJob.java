package com.facilio.bmsconsole.instant.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.InstantJob;

public class MigrateReadingDataJob extends InstantJob {
	
	private static final Logger LOGGER = LogManager.getLogger(MigrateReadingDataJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		try {
			
			long oldFieldId = (long) context.get(FacilioConstants.ContextNames.OLD_FIELD_ID);
			long fieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField oldField = bean.getField(oldFieldId);
			FacilioModule oldModule = bean.getModule(oldField.getModuleId());
			
			List<FacilioField> oldModulefields = bean.getAllFields(oldModule.getName());
			Map<String, FacilioField> oldModulefieldMap = FieldFactory.getAsMap(oldModulefields);
			

			SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
					.module(oldModule)
					.beanClass(ReadingContext.class)
					.select(oldModulefields)
					.andCondition(CriteriaAPI.getCondition(oldModulefieldMap.get("parentId"), Collections.singletonList(parentId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(oldField, CommonOperators.IS_NOT_EMPTY));
			
			List<ReadingContext> readingsList = builder.get();
			if (readingsList != null && !readingsList.isEmpty()) {
				
				addReading(parentId, fieldId, oldField.getName(), readingsList);
				
				Pair<Long, FacilioField> fieldAssetPair = Pair.of(parentId, oldField);
				ReadingsAPI.deleteAssetReading(Collections.singletonList(fieldAssetPair), oldModule, oldModulefields, oldModulefieldMap);
			}
			
		} catch (Exception e) {
			LOGGER.error("Error occurred during execution of MigrateReadingDataJob", e);
			CommonCommandUtil.emailException("MigrateReadingDataJob", "Error occurred during execution of MigrateReadingDataJob", e);
		}

	}
	
	private void addReading (long parentId, long fieldId, String oldFieldName, List<ReadingContext> readingsList) throws Exception {
		
		List<ReadingContext> newList = new ArrayList<>();
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField newField = bean.getField(fieldId);
		FacilioModule newModule = bean.getModule(newField.getModuleId());
		
		readingsList.forEach(reading -> {
			ReadingContext newReading = new ReadingContext();
			newReading.setParentId(parentId);
			
			Object value = reading.getReading(oldFieldName);
			newReading.addReading(newField.getName(), value);
			
			newReading.setTtime(reading.getTtime());
			newList.add(newReading);
		});
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, newModule.getName());
		context.put(FacilioConstants.ContextNames.READINGS, newList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}

}
