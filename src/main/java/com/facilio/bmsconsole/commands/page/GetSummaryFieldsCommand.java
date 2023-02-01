package com.facilio.bmsconsole.commands.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.modules.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.page.factory.PageFactory.SummaryOrderType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.google.common.collect.ImmutableMap;

import lombok.extern.log4j.Log4j;

@Log4j
public class GetSummaryFieldsCommand extends FacilioCommand {

	String moduleName;
	FacilioModule module;
	ModuleBaseWithCustomFields record;
	List<FacilioField> allFields;

	@Override
	public boolean executeCommand(Context context) throws Exception {

		moduleName = (String) context.get(ContextNames.MODULE_NAME);
		Boolean fetchMainFields = (Boolean) context.getOrDefault(ContextNames.FETCH_MAIN_FIELDS, false);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = modBean.getModule(moduleName);

		allFields = modBean.getAllFields(moduleName);
		record = (ModuleBaseWithCustomFields) context.get(ContextNames.RECORD);

		long formId = (long) context.get(ContextNames.FORM_ID);
		long widgetId = (long) context.get(ContextNames.WIDGET_ID);

		if (widgetId != -1) {
			// TODO check if fields for the widget/record and return if any
		}
		// TODO remove once page db support..get params from db
		Map<String, Object> widgetParams = (Map<String, Object>) context.get(ContextNames.WIDGET_PARAMJSON);


		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		List<FormField> formFields = null;
		if (currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP) || currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			if (orgId == 407 || orgId == 418 || moduleName.equals("peopleannouncement")) {
				formFields = getFieldsForAtre(modBean);
			}
		}

		if (formFields == null) {
			SummaryOrderType orderType = SummaryOrderType.FORM;
			Long formSectionId = null;
			if (widgetParams != null) {
				if (widgetParams.containsKey("orderType")) {
					orderType = SummaryOrderType.valueOf(Integer.parseInt(widgetParams.get("orderType").toString()));
				}
				formSectionId = (Long) widgetParams.get("formSectionId");
			}

			FacilioForm form = fetchForm(formId);
			if (form == null) {
				formFields = getFieldsAsFormFields(modBean, fetchMainFields);
			}
			else if (orderType == SummaryOrderType.FORM_SECTION) {
				formFields = new ArrayList<>();
				for (FormField formField: form.getFields()) {
					if (formField.getSectionId() == formSectionId &&
							showField(formField, fetchMainFields) && !formField.isFieldHidden()) {
						formFields.add(formField);
					}
				}
			}
			else {
				formFields = form.getFields().stream().filter(formField -> (
						showField(formField, fetchMainFields)  &&
						(!formField.isFieldHidden() || isValPresent(formField.getField()))
						)).collect(Collectors.toList());
			}

			if (orderType != SummaryOrderType.FORM_SECTION) {
				int count = !formFields.isEmpty() ? Collections.max(formFields, Comparator.comparing(s -> s.getSequenceNumber())).getSequenceNumber() : 0;
				List<String> existingFieldNames = formFields.stream().map(FormField::getName).collect(Collectors.toList());
				count = addModuleAndSystemFields(modBean, formFields, existingFieldNames, count);
				if (orderType == SummaryOrderType.ALPHA || formId == -1) {
					alphaSort(formFields);
				}
				addFieldsonBottom(modBean, formFields, existingFieldNames, count);
			}
		}

		// replaces sysCreatedTime with createdTime
		if (moduleName.equals("workorder")) {
			FormField sysCreatedTimeField = formFields.stream()
					.filter(f -> "sysCreatedTime".equals(f.getName()))
					.findAny()
					.orElse(null);

			if (sysCreatedTimeField != null) {
				// field exists and needs to be replaced.
				formFields.removeIf(f -> "sysCreatedTime".equals(f.getName()));
				FacilioField field = modBean.getField("createdTime", moduleName);
				formFields.add(FormsAPI.getFormFieldFromFacilioField(field, 1));
			}

		}

		context.put("fields", formFields);

		return false;
	}
	
	private boolean showField(FormField formField, Boolean fetchMainFields) {
		return formField.getField() != null  && (fetchMainFields  || (!fetchMainFields && !formField.getField().isMainField())) &&
				formField.getDisplayTypeEnum() != FieldDisplayType.IMAGE;
	}

	private FacilioForm fetchForm(long formId) throws Exception {
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		Context formContext = chain.getContext();

		formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
		formContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		String spaceForm = getSpaceForm(formId);
		if (spaceForm != null) {
			formContext.put(FacilioConstants.ContextNames.FORM_NAME, spaceForm);

		}
		chain.execute();

		return (FacilioForm) formContext.get(FacilioConstants.ContextNames.FORM);
	}

	// Till all forms are moved to db
	private String getSpaceForm(long formId) {
		String name = null;
		if (formId == -1 && moduleName.equals(ContextNames.SPACE)) {
			SpaceContext space = (SpaceContext) record;
			if (space.getSpaceId1() > 0) {
				name = "default_space_web_space";
			}
			else if (space.getFloorId() > 0) {
				name = "default_space_web_floor";
			}
			else if (space.getBuildingId() > 0) {
				name = "default_space_web_building";
			}
			else {
				name = "default_space_web_site";
			}
		}
		return name;
	}

	private List<FormField> getFieldsAsFormFields(ModuleBean modBean, Boolean fetchMainFields) throws Exception {
		List<FormField> fields = new ArrayList<>();
		int count = 0;
		for(FacilioField field: allFields) {
			if (fetchMainFields || (Boolean.FALSE.equals(fetchMainFields) && !field.isMainField())) {
				fields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
			}
		}
		return fields;
	}

	private void alphaSort(List<FormField> fields) {
		fields.sort(new Comparator<FormField>() {
			@Override
			public int compare(FormField f1, FormField f2) {
				return f1.getDisplayName().compareTo(f2.getDisplayName());
			}
		});
	}

	private int addModuleAndSystemFields(ModuleBean modBean, List<FormField> formFields,List<String> existingFieldNames, int count) throws Exception {
		List<String> additionalFields = new ArrayList<>();
		if (moduleVsFields.containsKey(moduleName)) {
			additionalFields.addAll(moduleVsFields.get(moduleName));
		}
		if (record != null && record.getStateFlowId() != -1 ) {
			List<String> transitionFields = getTransitionFormFields(modBean, record.getStateFlowId());
			if (transitionFields != null) {
				additionalFields.addAll(transitionFields);
			}
		}

		additionalFields.addAll(FieldFactory.getSystemFieldNames());
		addAdditionalFields(additionalFields, formFields, existingFieldNames, count);
		return count;
	}

	private void addFieldsonBottom(ModuleBean modBean, List<FormField> formFields,List<String> existingFieldNames, int count) throws Exception {
		List<String> additionalFields = new ArrayList<>();
		boolean isAssetModule = AssetsAPI.isAssetsModule(module);
		if (isAssetModule)  {
			AssetContext asset = (AssetContext)record;
			if (asset instanceof EnergyMeterContext) {
				boolean removed = formFields.removeIf(field -> field.getName().equals("childMeterExpression"));
				if (removed) {
					--count;
					existingFieldNames.removeIf(name -> name.equals("childMeterExpression"));
				}
				if (((EnergyMeterContext)asset).getChildMeterExpression() != null) {
					additionalFields.add("childMeterExpression");
				}
			}
			if (asset.getGeoLocationEnabled() != null && asset.getGeoLocationEnabled()) {
				additionalFields.addAll(bottomFields.get(ContextNames.ASSET));
			}
		}
		else {
			if (bottomFields.containsKey(moduleName)) {
				additionalFields.addAll(bottomFields.get(moduleName));
			}
		}
		addAdditionalFields(additionalFields, formFields, existingFieldNames, count);
	}

	private void addAdditionalFields(List<String> additionalFields, List<FormField> formFields,List<String> existingFieldNames, int count) {
		if (!additionalFields.isEmpty()) {
			for(FacilioField field: allFields) {
				String name = field.getName();
				if (additionalFields.contains(name) && !existingFieldNames.contains(name)) {
					formFields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
				}
			}
		}
	}

	// To fetch the fields of transition forms
	private List<String> getTransitionFormFields(ModuleBean modBean, long stateflowId) throws Exception {
		if (stateflowId <= 0) {
			return null;
		}
		FacilioModule module = ModuleFactory.getStateRuleTransitionModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getStateRuleTransitionFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(Collections.singletonList(fieldMap.get("formId")))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("stateFlowId"), String.valueOf(stateflowId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), CommonOperators.IS_NOT_EMPTY))
				;
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			List<Long> formIds = props.stream().map(prop -> (long)prop.get("formId")).collect(Collectors.toList());
			module = ModuleFactory.getFormFieldsModule();
			fieldMap = FieldFactory.getAsMap(FieldFactory.getFormFieldsFields());
			builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(Collections.singletonList(fieldMap.get("fieldId")))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), formIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_NOT_EMPTY));
			props = builder.get();
			if (CollectionUtils.isNotEmpty(props)) {
				List<Long> fieldIds = props.stream().map(prop -> (long)prop.get("fieldId")).collect(Collectors.toList());
				List<FacilioField> fields = modBean.getFields(fieldIds);
				return fields.stream().filter(field -> isValPresent(field)).map(prop -> prop.getName()).collect(Collectors.toList());
			}
		}
		return null;
	}

	private boolean isValPresent(FacilioField field) {
		if (field == null) {
			return false;
		}
		Object value = null;
		if (field.isDefault()) {
			try {
				value = PropertyUtils.getProperty(record, field.getName());
			} catch (Exception e) {
				LOGGER.debug("Exception while getting record data", e);
			}
		} else {
			value = record.getDatum(field.getName());
		}
		if (value == null && (FieldType.FILE).equals(field.getDataTypeEnum())) {
			if (field.isDefault()) {
				try {
					value = PropertyUtils.getProperty(record, field.getName() + "Id");
				} catch (Exception e) {
					LOGGER.debug("Exception while getting record data", e);
				}
			} else {
				value = record.getDatum(field.getName() + "Id");
			}
		}
		return value != null;
	}


	// To be removed once page db support is there
	private List<FormField> getFieldsForAtre (ModuleBean modBean) throws Exception {
		List<FormField> fields = new ArrayList<FormField>();

		Map<String, FacilioField> fieldMap = allFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(),
				(prevValue, curValue) -> prevValue));
		List<String> atreFields = getAtreFieldMap().get(moduleName);
		if (atreFields == null) {
			return null;
		}
		int count = 0;
		for (String fieldName: atreFields) {
			FacilioField field;
			if(fieldName.equals("siteId")){
				field = FieldFactory.getSiteIdField(module);
			} else {
				field = fieldMap.get(fieldName);
			}
			fields.add(FormsAPI.getFormFieldFromFacilioField(field, ++count));
		}
		return fields;
	}

	// To be removed once page db support is there
	private static Map<String, List<String>> getAtreFieldMap() {
		Map<String, List<String>> fieldMap = new HashMap<>();
		fieldMap.put("tenantunit", tenantUnitAtre);
		fieldMap.put("tenantcontact", tenantContact);
		fieldMap.put("custom_contracts", customContracts);
		fieldMap.put("custom_contractunits", customContractunits);
		fieldMap.put("custom_payment", customPayment);
		fieldMap.put("custom_receipts", customReceipts);
		fieldMap.put("peopleannouncement", peopleAnnouncement);
		fieldMap.put("custom_vendormapping", vendorMapping);
		fieldMap.put("workorder", workOrderMappingAtre);

		return fieldMap;
	}


	/******* Module Based Fields **********/

	Map<String, List<String>> moduleVsFields = ImmutableMap.<String, List<String>>builder()
			.put(ContextNames.WORK_ORDER, Arrays.asList(new String[] {
					"actualWorkEnd"
			}))
			.put(ContextNames.TENANT_UNIT_SPACE, Arrays.asList(new String[] {
					"site",
					"building",
					"floor"
			}))
			.put(ContextNames.TENANT, Arrays.asList(new String[] {
					"siteId",
			}))
			.put(ContextNames.WorkPermit.WORKPERMIT,  Arrays.asList(new String[] {
					"requestedBy",
			}))
			.build();

	Map<String, List<String>> bottomFields = ImmutableMap.<String, List<String>>builder()
			.put(ContextNames.ASSET,  Arrays.asList(new String[] {
					"geoLocation",
					"currentLocation",
					"designatedLocation",
					"distanceMoved",
					"boundaryRadius",
			}))
			.build();

	/************** Module Based End *********/





	/******* ATRE Fields.. TODO Remove *****************/
	private static final List<String> tenantUnitAtre = Collections.unmodifiableList(Arrays.asList(new String[] {
			"building",
			"site",
			"space",
			"description"
	}));

	private static final List<String> tenantContact = Collections.unmodifiableList(Arrays.asList(new String[] {
			"email",
			"phone",
			"isPrimaryContact"
	}));

	private static final List<String> customContracts = Collections.unmodifiableList(Arrays.asList(new String[] {
			"number",
			"date",
			"date_1",
			"multiline",
			"multiline_1",
			"number_1",
			"singleline_2",
			"number_3",
			"picklist",
			"picklist_2",
			"singleline_3",
			"number_2",
			"sysCreatedTime",
			"sysCreatedBy",
			"sysModifiedTime",
			"sysModifiedBy"
	}));

	private static final List<String> customContractunits = Collections.unmodifiableList(Arrays.asList(new String[] {
			"contractid",
			"siteId",
			"sysCreatedTime",
			"sysCreatedBy",
			"sysModifiedTime",
			"sysModifiedBy"
	}));

	private static final List<String> customPayment = Collections.unmodifiableList(Arrays.asList(new String[] {
			"date_2",
			"singleline_2",
			"date_1",
			"date",
			"number_3",
			"number_2",
			"singleline_1",
			"contract",
			"sysModifiedBy",
			"sysCreatedTime",
			"sysModifiedTime",
			"sysCreatedBy"
	}));

	private static final List<String> customReceipts = Collections.unmodifiableList(Arrays.asList(new String[] {
			"sysModifiedBy",
			"sysCreatedTime",
			"sysModifiedTime",
			"sysCreatedBy"
	}));


	private static final List<String> peopleAnnouncement = Collections.unmodifiableList(Arrays.asList(new String[] {
			"category",
			"expiryDate",
			"createdBy",
			"createdTime",
	}));

	private static final List<String> vendorMapping = Collections.unmodifiableList(Arrays.asList(new String[] {
			"space",
			"tenantunit",
			"building",
			"category",
			"subcategory",
			"contacts",
			"date",
			"date_1",
			"number_1",
			"picklist",
			"date_2",
			"picklist_1",
			"date_3",
			"picklist_2",
			"date_4",
			"sysModifiedBy",
			"sysCreatedTime",
			"sysModifiedTime",
			"sysCreatedBy"
	}));

	private static final List<String> workOrderMappingAtre = Collections.unmodifiableList(Arrays.asList(new String[]{
			"siteId", // Site
			"lookup", // Tenant Unit
			"resource", // Space / Asset
			"lookup_3", // Managed By
			"category", // Category
			"lookup_1", // Sub Category
			"item", // Item
			"createdTime", // Created Time
			"actualWorkEnd", // Resolved Time
			"vendor", // Vendor
			"type" // Type
	}));

	/******* ATRE End ******************/
}
