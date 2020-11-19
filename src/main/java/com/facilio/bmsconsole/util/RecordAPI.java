package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.facilio.modules.fields.FieldOption;
import org.apache.commons.collections4.CollectionUtils;

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
import org.apache.commons.lang3.StringUtils;

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
	
	public static ModuleBaseWithCustomFields getRecord (String modName, Long recId) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(modName);
		List<FacilioField> fields = modBean.getAllFields(modName);
		
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
			return records.get(0);
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

	public static List<FieldOption> constructFieldOptionsFromRecords (List<Map<String, Object>> records, String defaultFieldName, boolean isResource) {
		return records.stream().map(prop -> new FieldOption(
				prop.get("id").toString(),
				prop.get(defaultFieldName),
				isResource ? ResourceAPI.getResourceSubModuleFromType((Integer) prop.get("resourceType")) : null
		)).collect(Collectors.toList());
	}

	
}
