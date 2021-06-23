package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetResetCounterMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		List<Long> resetCounterIds = new ArrayList<>();
		if (resourceId == null || resourceId < 0 && resetCounterMetaList != null) {
			resetCounterIds = resetCounterMetaList.stream().map(ResetCounterMetaContext::getId).collect(Collectors.toList());
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = ModuleFactory.getResetCounterMetaModule();
		List<FacilioField> fields = FieldFactory.getResetCounterMetaFields();
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName());

		if (resourceId != null && resourceId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceId.toString(),NumberOperators.EQUALS));
		} else {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(resetCounterIds, module));
		}

		List<Map<String, Object>> props = selectBuilder.get();
		resetCounterMetaList = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props){
				ResetCounterMetaContext reset = FieldUtil.getAsBeanFromMap(prop, ResetCounterMetaContext.class);
				FacilioField field = modBean.getField(reset.getFieldId());
				reset.setField(field);
				String moduleName = field.getModule().getName();
				FacilioModule readingModule = modBean.getModule(moduleName);
				List<FacilioField> readingFields = modBean.getAllFields(moduleName);
				
				ReadingContext reading = ReadingsAPI.getReading(readingModule, readingFields, reset.getReadingDataId());
				reset.setReading(reading);
				for(Entry<String, Object> entry:reading.getReadings().entrySet()){
					if(entry.getKey().equals(field.getName())){
						reset.setValue(entry.getValue());
					}
				}
				resetCounterMetaList.add(reset);
			}
		}
		context.put(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST,resetCounterMetaList);
		return false;
	}

}
