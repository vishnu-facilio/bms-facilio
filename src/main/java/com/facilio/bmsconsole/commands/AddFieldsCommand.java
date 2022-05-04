package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount50;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.FacilioException;
import com.facilio.i18n.util.Keys.Error;
import com.facilio.i18n.util.TranslationUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class AddFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		//Have to be converted to batch insert
		List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);
		if(modules != null && !modules.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> fieldIds = new ArrayList<>();
			List<List<ReadingRuleContext>> readingRules = new ArrayList<>();
			List<List<List<ActionContext>>> actionsList = new ArrayList<>();
			boolean changeDisplayName = (Boolean) context.getOrDefault(ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, false);
			boolean isNewModules = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_NEW_MODULES, false);

			boolean isSkipCounterfieldAdd = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_SKIP_COUNTER_FIELD_ADD, false);

			for (FacilioModule module : modules) {
				FacilioModule cloneMod = new FacilioModule(module);
				if(module != null && CollectionUtils.isNotEmpty(module.getFields())) {
					List<Long> extendedModuleIds = module.getExtendedModuleIds();
					List<FacilioField> existingFields = isNewModules ? null : modBean.getAllFields(module.getName());
					List<String> existingNames = existingFields != null ? existingFields.stream().map(FacilioField::getName).collect(Collectors.toList()) : null;
					Map<FieldType, List<String>> existingColumns = getColumnNamesGroupedByType(existingFields, module.getModuleId());

					List<FacilioField> counterFields = new ArrayList<>();

					for(FacilioField field : module.getFields()) {
						try {
							field.setModule(cloneMod);
							setColumnName(field, existingColumns);
							constructFieldName(field, module, changeDisplayName, existingNames, cloneMod.getName ());
							long fieldId = modBean.addField(field);
							field.setFieldId(fieldId);
							fieldIds.add(fieldId);
							if (field instanceof NumberField) {
								NumberField numberField = (NumberField) field;
								if (numberField.isCounterField() && !isSkipCounterfieldAdd) {
									NumberField deltaField = numberField.clone();
									deltaField.setCounterField(null);
									deltaField.setId(-1);

									deltaField.setColumnName(null);
									setColumnName(deltaField, existingColumns);

									deltaField.setName(deltaField.getName() + "Delta");
									deltaField.setDisplayName(deltaField.getDisplayName() + " Delta");

									long deletaFieldId = modBean.addField(deltaField);
									deltaField.setFieldId(deletaFieldId);
									fieldIds.add(deletaFieldId);

									counterFields.add(deltaField);
								}
							}
						}
						catch (Exception e) {
							LOGGER.error(MessageFormat.format("Error occurred while adding field {0}", field), e);
							throw e;
						}
					}

					if (!counterFields.isEmpty()) {
						module.getFields().addAll(counterFields);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
			context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
		}
		else {
			Boolean suppressException = (Boolean) context.get("should_suppress_exception");
			if (suppressException == null || !suppressException) {
				throw new IllegalArgumentException("No Fields to Add");
			}
		}
		return false;
	}

	private void setColumnName (FacilioField field, Map< FieldType, List< String > > existingColumns) throws Exception {

		FieldType dataType = field.getDataTypeEnum ();
		FacilioUtil.throwIllegalArgumentException (dataType == null, "Invalid Data Type Value");

		if (!dataType.isRelRecordField ()) {
			List< String > existingColumnNames = existingColumns.get (dataType);
			if (existingColumnNames == null) {
				existingColumnNames = new ArrayList<> ();
				existingColumns.put (dataType, existingColumnNames);
			}
			if (field.getColumnName () == null || field.getColumnName ().isEmpty ()) {
				V3Config v3Config = ChainUtil.getV3Config (field.getModule ().getName ());
				String newColumnName;
				if (v3Config != null) {
					FacilioUtil.throwIllegalArgumentException (v3Config.getCustomFieldsCount () == null, "No column available for the Field type");
					newColumnName = v3Config.getCustomFieldsCount ().getNewColumnNameForFieldType (dataType.getTypeAsInt (), existingColumnNames);
				} else {
					newColumnName = new ModuleCustomFieldCount50 ().getNewColumnNameForFieldType (dataType.getTypeAsInt (), existingColumnNames);
				}
				FacilioUtil.throwIllegalArgumentException (StringUtils.isEmpty (newColumnName), "No more column available for the Field type");

				field.setColumnName (newColumnName);
			}
			existingColumnNames.add (field.getColumnName ());
		}
	}

	private void constructFieldName(FacilioField field, FacilioModule module, boolean changeDisplayName, List<String> existingNames, String moduleName) throws Exception {
		if(field.getName() == null || field.getName().isEmpty()) {
			if(field.getDisplayName() != null && !field.getDisplayName().isEmpty()) {
				field.setName(field.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+","")+"_"+moduleName);
				// Will be used while adding a new field from form
				handleDuplicateField (field, changeDisplayName, existingNames, moduleName);
			}
			else {
				throw new IllegalArgumentException("Invalid name for field of module : "+module.getName());
			}
		}else {
			handleDuplicateField (field, changeDisplayName, existingNames, moduleName);
		}
	}

	private void handleDuplicateField (FacilioField field, boolean changeDisplayName, List< String > existingNames, String moduleName) throws Exception {

		if (changeDisplayName) {
			setDisplayNameAndFieldName(field, moduleName);
		}
		else if (existingNames != null && existingNames.contains(field.getName())) {
			throw new FacilioException(TranslationUtil.getString(Error.FIELD_DUPLICATE, field.getDisplayName()));
		}
	}


	private void setDisplayNameAndFieldName(FacilioField field, String moduleName) throws Exception {

		List< FacilioField > existingFields = Constants.getModBean ().getAllFields (moduleName);
		int count = 0;
		if (CollectionUtils.isNotEmpty (existingFields)) {
			List< FacilioField > filteredFields = existingFields.stream ().filter (str -> str.getName ().contains (field.getName ())).collect (Collectors.toList ());
			if (CollectionUtils.isNotEmpty (filteredFields)) {
				String fieldName = field.getName ();
				while ( true ) {
					String finalDbFieldName = fieldName;
					FacilioField duplicateField = filteredFields.stream ().filter (f -> f.getName ().equals (finalDbFieldName)).findFirst ().orElse (null);
					if (duplicateField == null) {
						break;
					}
					if (!fieldName.equals (field.getName ()) && fieldName.contains ("_")) {
						count = Integer.parseInt (fieldName.substring (fieldName.lastIndexOf ('_') + 1));
					}
					fieldName = field.getName () + "_" + ++count;
				}
				field.setName(field.getName() +"_"+moduleName + "_" + count);
				field.setDisplayName(field.getDisplayName() + " " + count);
			}else {
				field.setName(field.getName()+"_"+moduleName);
			}
		}
	}

	private Map<FieldType, List<String>> getColumnNamesGroupedByType(List<FacilioField> fields, long moduleId) {
		Map<FieldType, List<String>> existingColumns = new HashMap<>();
		if(fields != null) {
			for(FacilioField field : fields) {
				if (field.getModuleId() == moduleId) {
					List<String> columns = existingColumns.get(field.getDataTypeEnum());
					if(columns == null) {
						columns = new ArrayList<>();
						existingColumns.put(field.getDataTypeEnum(), columns);
					}
					columns.add(field.getColumnName());
				}
			}
		}
		return existingColumns;
	}

}