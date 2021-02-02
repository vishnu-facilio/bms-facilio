package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import com.facilio.modules.fields.LookupField;

public class RecordAPI {

	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		addRecord(isLocalIdNeeded, list, module, fields, false);
	}
	
	public static Map<Long, List<UpdateChangeSet>> addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {
		
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		if (isChangeSetNeeded != null && isChangeSetNeeded) {
			insertRecordBuilder.withChangeSet();
		}
	
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
		if (isChangeSetNeeded != null && isChangeSetNeeded) {
			return insertRecordBuilder.getChangeSet();
		}
		return null;
	}
	
	public static void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		updateRecord(data, module, fields, false);
	}
	
	public static Map<Long, List<UpdateChangeSet>> updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		if(isChangeSetNeeded != null && isChangeSetNeeded) {
			updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
		}
		updateRecordBuilder.update(data);
		if (isChangeSetNeeded != null && isChangeSetNeeded) {
			return updateRecordBuilder.getChangeSet();
		}
	
		return null;
	
	}
	
	public static Object getPrimaryValue (String modName, long recId) throws Exception{
		if (LookupSpecialTypeUtil.isSpecialType(modName)) {
			return LookupSpecialTypeUtil.getPrimaryFieldValue(modName, recId);
		}
		return getRecord(modName, recId, true);
	}
	
	public static ModuleBaseWithCustomFields getRecord (String modName, Long recId) throws Exception{
		return (ModuleBaseWithCustomFields) getRecord(modName, recId, false);
	}
	
	public static Object getRecord (String modName, Long recId, boolean fetchPrimary) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(modName);
		List<FacilioField> fields;
		FacilioField primaryField = modBean.getPrimaryField(modName);
		if(fetchPrimary) {
			fields = modBean.getAllFields(modName);
		}
		else {
			fields = Collections.singletonList(primaryField);
		}
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(beanClassName)
															.select(fields)
															.andCondition(CriteriaAPI.getIdCondition(Long.valueOf(recId), module))
															;
		
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		if(CollectionUtils.isNotEmpty(records)) {
			ModuleBaseWithCustomFields record = records.get(0);
			if (fetchPrimary) {
				String primaryVal = null;
				try {
					primaryVal = (String) PropertyUtils.getProperty(record, primaryField.getName());
				} catch (Exception e) {
					primaryVal = String.valueOf(recId);
				}
				return primaryVal;
			}
			return record;
		}
		else {
			return null;
		}
	}
	
	public static ClientContext getClientForSite(long siteId) throws Exception {
		ClientContext clientContext = null;
		if (siteId <= 0) {
			return clientContext;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(SiteContext.class)
				.andCondition(CriteriaAPI.getIdCondition(siteId, module))
				;
		List<SiteContext> services = selectBuilder.get();
		if(!CollectionUtils.isEmpty(services)) {
			return services.get(0).getClient();
		}
		
		return clientContext;
		
	}
	
	public static boolean checkChangeSet(List<UpdateChangeSet> changes, String fieldName, String moduleName) throws Exception {
		
		if(CollectionUtils.isNotEmpty(changes)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(fieldName, moduleName);
			if(field == null) {
				throw new IllegalArgumentException("Invalid Field");
			}
			for(UpdateChangeSet change : changes) {
				if(change.getFieldId() == field.getFieldId()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void handleCustomLookup(Map<String, Object> data, String modName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(modName);
		CommonCommandUtil.handleLookupFormData(fields, data);
		
	}
	

	public static FacilioField getField(String fieldName, String modName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		return modBean.getField(fieldName, modName);
		
	}

	// TODO : This should be made db driven like main field
	public static String getDefaultOrderByForModuleIfAny (String moduleName) {
		Objects.requireNonNull(moduleName, "Module name cannot be null for getting default order by");
		switch (moduleName) {
			case FacilioConstants.ContextNames.TICKET_PRIORITY:
				return "SEQUENCE_NUMBER";
			default:
				return null;
		}
	}

	public static String getDefaultIdOrderBy (FacilioModule module, List<Long> ids, String orderBy) {
		String defaultIdOrderBy = MessageFormat.format("FIELD ( {0}.ID, {1} ) DESC, {2}", module.getTableName(), StringUtils.join(ids, ","), orderBy);
//		orderBy = "FIELD(" + module.getTableName() + ".ID," + StringUtils.join(ids, ",") +") desc, " + orderBy;
		return defaultIdOrderBy;
	}

	private static FacilioField getMainFieldOfLookup (FacilioField field, ModuleBean modBean) throws Exception {
		if (field != null && field instanceof LookupField && StringUtils.isEmpty(((LookupField) field).getSpecialType())) {
			return modBean.getPrimaryField(((LookupField) field).getLookupModule().getName());
		}
		return null;
	}

	private static Object getValue (Map<String, Object> prop, FacilioField field, FacilioField lookupMainField) {
		if (field instanceof LookupField) {
			LookupField lookupField = (LookupField) field;
			if (StringUtils.isEmpty(lookupField.getSpecialType())) {
				Map<String, Object> val = (Map<String, Object>) prop.get(field.getName());
				return val == null ? null : val.get(lookupMainField.getName());
			}
			else {
				return LookupSpecialTypeUtil.getPrimaryFieldValue(lookupField.getSpecialType(), prop);
			}
		}
		else {
			return prop.get(field.getName());
		}
	}

	public static List<FieldOption<Long>> constructFieldOptionsFromRecords (List<Map<String, Object>> records, FacilioField defaultField, FacilioField secondaryField, boolean isResource) throws Exception {

		if (CollectionUtils.isEmpty(records)) {
			return null;
		}

		List<FieldOption<Long>> options = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField defaultFieldLookupPrimary = getMainFieldOfLookup(defaultField, modBean);
		FacilioField secondaryFieldLookupPrimary = getMainFieldOfLookup(secondaryField, modBean);
		for (Map<String, Object> prop : records) {
			Long id = (Long) prop.get("id");
			Object primaryLabel = getValue(prop, defaultField, defaultFieldLookupPrimary);
			Object secondaryLabel = secondaryField == null ? null : getValue(prop, secondaryField, secondaryFieldLookupPrimary);
			options.add(new FieldOption<>(
					id,
					primaryLabel,
					secondaryLabel,
					isResource ? ResourceAPI.getResourceSubModuleFromType((Integer) prop.get("resourceType")) : null
			));
		}
		return options;
	}
	
}
