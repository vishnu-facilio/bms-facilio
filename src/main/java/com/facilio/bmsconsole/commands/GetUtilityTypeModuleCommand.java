package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GetUtilityTypeModuleCommand extends FacilioCommand {
	private static final int MAX_FIELDS_PER_TYPE_PER_MODULE = 5;
	private static final Logger LOGGER = LogManager.getLogger(CreateReadingModulesCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		LOGGER.info("Inside GetUtilityTypeModuleCommand");
			long utilityTypeId = (long) context.get(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID);
			if (utilityTypeId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				FacilioModule utilityTypeModule = modBean.getModule("utilitytype");
				SelectRecordsBuilder<V3UtilityTypeContext> builder = new SelectRecordsBuilder<V3UtilityTypeContext>()
						.module(utilityTypeModule)
						.beanClass(V3UtilityTypeContext.class)
						.select(modBean.getAllFields(utilityTypeModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(utilityTypeId, utilityTypeModule));
				List<V3UtilityTypeContext> list = builder.get();
				
				FacilioModule meterModule = null;
				if (list.size() > 0) {
					V3UtilityTypeContext meterUtilityType = list.get(0);
					long meterModuleID = meterUtilityType.getMeterModuleID();

					meterModule = modBean.getModule(meterModuleID);
				}
				
				if (meterModule == null) {
					throw new Exception("Cannot find module entry");
				}
				
				context.put(FacilioConstants.ContextNames.PARENT_MODULE, meterModule.getName());
			}
		return false;
	}
	
	private List<FacilioModule> splitFields(FacilioModule module, List<FacilioField> allFields) {
		if (allFields.size() <= MAX_FIELDS_PER_TYPE_PER_MODULE) {
			allFields.addAll(FieldFactory.getDefaultReadingFields(module));
			module.setFields(allFields);
			return Collections.singletonList(module);
		}
		else {
			Map<FieldType, List<FacilioField>> fieldMap = getTypeWiseFields(allFields);
			List<FacilioField> fieldList = new ArrayList<>();
			List<FacilioModule> modules = new ArrayList<>();
			while (!fieldMap.isEmpty()) {
				Iterator<List<FacilioField>> fieldsItr = fieldMap.values().iterator();
				while (fieldsItr.hasNext()) {
					List<FacilioField> fields = fieldsItr.next();
					Iterator<FacilioField> itr = fields.iterator();
					int count = 0;
					while (itr.hasNext() && count < MAX_FIELDS_PER_TYPE_PER_MODULE) {
						fieldList.add(itr.next());
						count++;
						itr.remove();
					}
					if (fields.isEmpty()) {
						fieldsItr.remove();
					}
				}
				
				if (!fieldList.isEmpty()) {
					FacilioModule clone = copyModule(module, fieldList);
					LOGGER.debug("Module : "+clone);
					LOGGER.debug("Fields : "+module.getFields());
					modules.add(clone);		// module addition done here
					fieldList = new ArrayList<>();
				}
				else {
					break;
				}
			}
			return modules;
		}
	}
	
	private FacilioModule copyModule(FacilioModule module, List<FacilioField> fields) {
		FacilioModule newModule = new FacilioModule(module);
		fields.addAll(FieldFactory.getDefaultReadingFields(newModule));
		newModule.setFields(fields);
		return newModule;
	}
	
	private Map<FieldType, List<FacilioField>> getTypeWiseFields(List<FacilioField> fields) {
		Map<FieldType, List<FacilioField>> typeWiseFields = new HashMap<>();
		for (FacilioField field : fields) {
			List<FacilioField> fieldList = typeWiseFields.get(field.getDataTypeEnum());
			if (fieldList == null) {
				fieldList = new ArrayList<>();
				typeWiseFields.put(field.getDataTypeEnum(), fieldList);
			}
			fieldList.add(field);
		}
		return typeWiseFields;
	}

}
