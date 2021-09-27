package com.facilio.bmsconsole.commands.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.Induction;
import com.facilio.constants.FacilioConstants.Inspection;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldFactory.Fields;
import com.facilio.modules.FieldFactory.Fields.FilterType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import lombok.Getter;
import lombok.Setter;

public class GetSortableFieldsCommand extends FacilioCommand {
	

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(ContextNames.MODULE_NAME);
        List<FacilioField> fields = (List<FacilioField>) context.get(ContextNames.FIELDS);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        
        List<SortableField> sortFields = new ArrayList<>();
        for (FacilioField field : filterFields(module, fields)) {
        		FacilioField sortableField = filterSortableFields(module, field);
        		if (sortableField != null) {
        			sortFields.add(new SortableField(sortableField));
        		}
        }
        
        Collections.sort(sortFields, Comparator.comparing(SortableField::isMainField).reversed()
        		.thenComparing(Comparator.comparing(SortableField::getDisplayName))
        		); 
		
        context.put(ContextNames.SORT_FIELDS, sortFields);
		return false;
	}
	
	private List<FacilioField> filterFields (FacilioModule module, List<FacilioField> fields) throws Exception {
        fields = filterModuleFields(module, fields);
        FacilioField idField = FieldFactory.getIdField(module);
        idField.setDisplayName(FieldUtil.getRecordIdFieldName(module));
        fields.add(idField);
        return fields;
    }
	
	private FacilioField filterSortableFields(FacilioModule module, FacilioField field) throws Exception {
		switch (field.getDataTypeEnum()) {
			case FILE:
			case MISC:
			case LINE_ITEM:
			case MULTI_ENUM:
			case MULTI_LOOKUP:
			case LARGE_TEXT:
				return null;
			case LOOKUP:
				if ( ((LookupField)field).getLookupModule().getTypeEnum() != ModuleType.PICK_LIST) {
					return null;
				}
				break;
		}
		
		String name = field.getName();
		if (FieldUtil.INTERNAL_FIELDS.contains(name)) {
			return null;
		}
		if (name.equals("moduleState") && !module.isStateFlowEnabled()) {
			return null;
		}
		
		return field;
	}
	
    private static final List<String> INSPECTION_FIELDS_TO_HIDE = Arrays.asList(new String[] {"template", "sysCreatedTime"});
	private List<FacilioField> filterModuleFields(FacilioModule module, List<FacilioField> fields) {
		if (AssetsAPI.isAssetsModule(module)) {
			return Fields.filterOutFields(fields, Fields.ASSET_FIELDS_INCLUDE, Fields.FilterType.INCLUDE);
		}
		
		List<String> fieldsToFilter;
		FilterType filterType = FilterType.INCLUDE;
		switch(module.getName()) {
			case ContextNames.WORK_ORDER:
				fieldsToFilter = Fields.WORK_ORDER_FIELDS_INCLUDE;
				break;
			case ContextNames.NEW_READING_ALARM:
			case ContextNames.BMS_ALARM:
			case ContextNames.ML_ANOMALY_ALARM:
				fieldsToFilter = Fields.NEW_ALARMS_FIELDS_INCLUDE;
				break;
			case ContextNames.OPERATION_ALARM:
				fieldsToFilter = Fields.NEW_OP_ALARMS_FIELDS_INCLUDE;
				break;
			case Inspection.INSPECTION_RESPONSE:
			case Induction.INDUCTION_RESPONSE:
				fieldsToFilter = INSPECTION_FIELDS_TO_HIDE;
				filterType = FilterType.EXCLUDE;
			default:
				return fields;
		}
		return Fields.filterOutFields(fields, fieldsToFilter, filterType);

	}
	
	@Getter @Setter
	public class SortableField {
		private String name, displayName;
		private long id;
		private boolean isMainField;
		
		SortableField(FacilioField field) {
			name = field.getName();
			displayName = field.getDisplayName();
			id = field.getId();
			isMainField = field.isMainField();
		}
	}
}
