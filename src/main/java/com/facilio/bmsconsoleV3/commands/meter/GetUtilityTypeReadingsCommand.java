package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetUtilityTypeReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean onlyReading = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_READING);
		if (onlyReading == null) {
			onlyReading = false;
		}
		
		long parentUtilityTypeId = (long) context.get(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID);

		
		if(parentUtilityTypeId != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule utilityTypeModule = modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE);
			SelectRecordsBuilder<V3UtilityTypeContext> builder = new SelectRecordsBuilder<V3UtilityTypeContext>()
					.beanClass(V3UtilityTypeContext.class)
					.module(utilityTypeModule)
					.select(modBean.getAllFields(FacilioConstants.Meter.UTILITY_TYPE))
					.andCondition(CriteriaAPI.getIdCondition(parentUtilityTypeId, utilityTypeModule));
			List<V3UtilityTypeContext> list = builder.get();

			List<FacilioModule> readings = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(list)) {
				V3UtilityTypeContext v3utilityTypeContext = list.get(0);

				List<FacilioField> subModuleRelFields = new ArrayList<>();
				subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
				subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
						.table("SubModulesRel")
						.select(subModuleRelFields)
						.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(v3utilityTypeContext.getMeterModuleID()), NumberOperators.EQUALS));
				List<Map<String, Object>> props = selectRecordBuilder.get();

				if (CollectionUtils.isNotEmpty(props)) {
					for(Map<String, Object> prop : props) {
						FacilioModule readingModule = modBean.getModule((long) prop.get("childModuleId"));
						if(readingModule.getTypeEnum().isReadingType())
						{
							readings.add(readingModule);
						}
					}
				}
			}

			List<FacilioModule> existingReadings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			if (existingReadings == null) {
				context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			}
			else {
				existingReadings.addAll(readings);
			}
		}
		
		return false;
	}
	
}
