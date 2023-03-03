package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
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
		if (CollectionUtils.isNotEmpty(fields)) {
		List<FacilioField> lookupFields = fields.stream().filter(field -> field.getDataTypeEnum() == FieldType.LOOKUP && ((LookupField) field).getSpecialType() == null).collect(Collectors.toList());
		Map<String, FacilioField> primaryFieldMap = new HashMap<>();
		for (FacilioField field : lookupFields) {
			LookupField lookupField = (LookupField) field;
			String name = lookupField.getLookupModule().getName();
			FacilioField primaryField = modBean.getPrimaryField(name);
			if (primaryField != null) {
				primaryFieldMap.put(field.getName(), primaryField);
			}
		}

		if (CollectionUtils.isNotEmpty(lookupFields)) {
			for (ModuleBaseWithCustomFields record : records) {
				for (FacilioField field : lookupFields) {
					if (!primaryFieldMap.containsKey(field.getName())) {
						continue;
					}
					LookupField lookupField = (LookupField) field;
					ModuleBaseWithCustomFields lookupRecord=null;
					Object lookupRecordObject = FieldUtil.getValue(record,lookupField);

					if(lookupRecordObject instanceof ModuleBaseWithCustomFields){
						lookupRecord=(ModuleBaseWithCustomFields)lookupRecordObject;
					}

					if (lookupRecord == null) {
						continue;
					}

					Object property = getMainFieldProperty(lookupRecord, primaryFieldMap.get(field.getName()), modBean);
					if (property == null) {
						continue;
					}
					FieldUtil.setPrimaryValue(lookupRecord,property);
				}
			}
		}
	}
		
		
		return false;
	}

	public static Object getMainFieldProperty(ModuleBaseWithCustomFields lookupRecord, FacilioField primaryField, ModuleBean modBean) {
		if (lookupRecord == null) {
			return null;
		}
		Object property;
		try {
			property = PropertyUtils.getProperty(lookupRecord, primaryField.getName());
			if (primaryField instanceof LookupField && ((LookupField) primaryField).getSpecialType() == null) {
				FacilioField childMainField = modBean.getPrimaryField(((LookupField) primaryField).getLookupModule().getName());
				if (childMainField != null) {
					property = getMainFieldProperty((ModuleBaseWithCustomFields) property, childMainField, modBean);
				}
			}
		} catch (Exception e) {
			property = lookupRecord.getId();
		}
		return property;
	}

}
