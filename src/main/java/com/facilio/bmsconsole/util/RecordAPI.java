package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;

public class RecordAPI {

	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		addRecord(isLocalIdNeeded, list, module, fields, false);
	}

	public static Map<Long, List<UpdateChangeSet>> addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {
		return addRecord(isLocalIdNeeded, list, module, fields, isChangeSetNeeded, null);
	}

	public static Map<Long, List<UpdateChangeSet>> addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded, List<SupplementRecord> supplements) throws Exception {

		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		if (isChangeSetNeeded != null && isChangeSetNeeded) {
			insertRecordBuilder.withChangeSet();
		}

		if(CollectionUtils.isNotEmpty(supplements)) {
			insertRecordBuilder.insertSupplements(supplements);
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
		return updateRecord(data, module, fields, isChangeSetNeeded, null);
	}

	public static Map<Long, List<UpdateChangeSet>> updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded, List<SupplementRecord> supplements) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		if(isChangeSetNeeded != null && isChangeSetNeeded) {
			updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
		}
		if(CollectionUtils.isNotEmpty(supplements)) {
			updateRecordBuilder.updateSupplements(supplements);
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

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField primaryField = modBean.getPrimaryField(modName);
		List<FacilioField> fields = Collections.singletonList(primaryField);

		ModuleBaseWithCustomFields record = getRecord(modName, recId, fields);

		String primaryVal = null;
		try {
			primaryVal = (String) PropertyUtils.getProperty(record, primaryField.getName());
		} catch (Exception e) {
			primaryVal = String.valueOf(recId);
		}
		return primaryVal;
	}

	public static ModuleBaseWithCustomFields getRecord (String modName, Long recId) throws Exception{
		return (ModuleBaseWithCustomFields) getRecord(modName, recId, null);
	}

	public static ModuleBaseWithCustomFields getRecord (String modName, Long recId, List<FacilioField> fields) throws Exception{
		List<? extends ModuleBaseWithCustomFields> records = getRecords(modName, Collections.singletonList(recId), fields);
		if(CollectionUtils.isNotEmpty(records)) {
			return records.get(0);
		}
		return null;
	}

	public static List<? extends ModuleBaseWithCustomFields> getRecords(String modName, Collection<Long> ids) throws Exception {
		return getRecords(modName, ids, null);
	}

	public static List<? extends ModuleBaseWithCustomFields> getRecords(String modName, Collection<Long> ids, List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder builder = getRecordsBuilder(modName, ids, fields);
		return builder.get();
	}

	public static List<Map<String, Object>> getRecordsAsProps(String modName, Collection<Long> ids, List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder builder = getRecordsBuilder(modName, ids, fields);
		return builder.getAsProps();
	}

	private static SelectRecordsBuilder getRecordsBuilder(String modName, Collection<Long> ids, List<FacilioField> fields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(modName);
		if (fields == null) {
			fields = modBean.getAllFields(modName);
		}
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(beanClassName)
															.select(fields)
															.andCondition(CriteriaAPI.getIdCondition(ids, module))
															;
		return builder;
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
		String[] fieldNames = fieldName.split("\\.");
		if(fieldNames.length > 1) {
			fieldName = fieldNames[1];
		}
		return modBean.getField(fieldName, modName);
		
	}

	// TODO : This should be made db driven like main field
	public static String getDefaultOrderByForModuleIfAny (String moduleName) throws Exception {
		SortField field = getDefaultSortFieldForModule(moduleName);
		if (field != null) {
			String columnName = field.getSortField().getCompleteColumnName();
			return columnName + " IS NULL," + columnName + " " + (field.getIsAscending()? "asc" : "desc");
		}
		return null;
	}
	public static SortField getDefaultSortFieldForModule(String moduleName) throws Exception {
		Objects.requireNonNull(moduleName, "Module name cannot be null for getting default order by");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Pair<String, Boolean> fieldOrdering = FieldFactory.getSortableFieldName(moduleName);
		if (fieldOrdering != null) {
			return new SortField(modBean.getField(fieldOrdering.getLeft(), moduleName), fieldOrdering.getRight()) ;
		}
		return null;
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
