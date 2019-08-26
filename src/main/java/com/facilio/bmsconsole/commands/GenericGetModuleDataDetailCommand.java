package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.modules.FieldType;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.MapUtils;

public class GenericGetModuleDataDetailCommand extends FacilioCommand {

	protected Class beanClassName;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(recordId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			if (beanClassName == null) {
				beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
				if (beanClassName == null) {
					if (module.getTypeEnum() == ModuleType.CUSTOM) {
						beanClassName = CustomModuleData.class;
					}
					else {
						beanClassName = ModuleBaseWithCustomFields.class;
					}
				}
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																	.module(module)
																	.beanClass(beanClassName)
																	.select(fields)
																	.andCondition(CriteriaAPI.getIdCondition(recordId, module))
																	;

			List<LookupField>fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
			if (CollectionUtils.isNotEmpty(fetchLookup)) {
				builder.fetchLookups(fetchLookup);
			}
			
			List<ModuleBaseWithCustomFields> records = builder.get();
			if(records.size() > 0) {
				ResourceAPI.loadModuleResources(records, fields);
				context.put(FacilioConstants.ContextNames.RECORD, records.get(0));

				List<FacilioField> lookupFields = fields.stream().filter(field -> field.getDataTypeEnum() == FieldType.LOOKUP && !field.isDefault()).collect(Collectors.toList());
				Map<String, FacilioField> primaryFieldMap = new HashMap<>();
				for (FacilioField field : lookupFields) {
					String name = ((LookupField) field).getLookupModule().getName();
					FacilioField primaryField = modBean.getPrimaryField(name);
					if (primaryField != null) {
						primaryFieldMap.put(name, primaryField);
					}
				}

				if (MapUtils.isNotEmpty(primaryFieldMap)) {
					for (ModuleBaseWithCustomFields record : records) {
						Map<String, Object> data = record.getData();
						if (MapUtils.isEmpty(data)) {
							continue;
						}
						for (String key : data.keySet()) {
							if (primaryFieldMap.containsKey(key)) {
								FacilioField primaryField = primaryFieldMap.get(key);
								ModuleBaseWithCustomFields lookupRecord = (ModuleBaseWithCustomFields) data.get(key);
								Object property = PropertyUtils.getProperty(lookupRecord, primaryField.getName());
								PropertyUtils.setProperty(lookupRecord, "primaryValue", property);
							}
						}
					}
				}
			}
		}
		return false;
	}
}
