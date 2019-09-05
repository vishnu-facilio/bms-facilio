package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LookupPrimaryFieldHandlingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (records == null) {
			records = new ArrayList<ModuleBaseWithCustomFields>();
			ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
			if (record != null) {
				records.add(record);					
			}
		}
		if (records == null) {
			return false;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		List<FacilioField> lookupFields = fields.stream().filter(field -> field.getDataTypeEnum() == FieldType.LOOKUP && !field.isDefault()).collect(Collectors.toList());
		Map<String, FacilioField> primaryFieldMap = new HashMap<>();
		for (FacilioField field : lookupFields) {
			String name = ((LookupField) field).getLookupModule().getName();
			FacilioField primaryField = modBean.getPrimaryField(name);
			if (primaryField != null) {
				primaryFieldMap.put(field.getName(), primaryField);
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
		
		
		return false;
	}

}
