package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PurchaseOrderAPI {

	private static int getSerialNumberCount (long lineItemId) throws Exception {
		int count = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String polineitemserialnomodulename = FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS;
		List<FacilioField> fields = modBean.getAllFields(polineitemserialnomodulename);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<PoLineItemsSerialNumberContext> builder = new SelectRecordsBuilder<PoLineItemsSerialNumberContext>()
				.moduleName(polineitemserialnomodulename)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(polineitemserialnomodulename))
				.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("lineItem"), String.valueOf(lineItemId), NumberOperators.EQUALS))
		        ;
		List<PoLineItemsSerialNumberContext> list = builder.get();
		if(list!=null && !list.isEmpty()) {
			count = list.size();
		}
		
		return count;
	}
	
	public static void setLineItems(PurchaseOrderContext po) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
				.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(po.getId()), NumberOperators.EQUALS))
		        .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
				(LookupField) fieldsAsMap.get("toolType")))
		        ;
		List<PurchaseOrderLineItemContext> list = builder.get();
		for(PurchaseOrderLineItemContext item : list) {
			item.setNoOfSerialNumbers(getSerialNumberCount(item.getId()));
		}
		po.setLineItems(list);
		
	}

	public static void setLineItemsV3(V3PurchaseOrderContext po) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

		FacilioModule lineItemModule = modBean.getModule(lineItemModuleName);
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(lineItemModule)) {
			fields.addAll(FieldFactory.getCurrencyPropsFields(lineItemModule));
		}

		SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
				.moduleName(lineItemModuleName)
				.select(fields)
				.beanClass(V3PurchaseOrderLineItemContext.class)
				.andCondition(CriteriaAPI.getCondition("PO_ID", "purchaseOrder", String.valueOf(po.getId()), NumberOperators.EQUALS))
				.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
						(LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service"), (LookupField) fieldsAsMap.get("tax")));
		List<V3PurchaseOrderLineItemContext> list = builder.get();
		Map<String, Object> baseCurrencyInfo = CurrencyUtil.getCurrencyInfo();

		for(V3PurchaseOrderLineItemContext item : list) {
			item.setNoOfSerialNumbers(getSerialNumberCount(item.getId()));
			CurrencyUtil.checkAndFillBaseCurrencyToRecord(item, baseCurrencyInfo);
		}
		setLineItemName(list);
		setEnumValues(list);
		po.setLineItems(list);
	}
	private static void setEnumValues(List<V3PurchaseOrderLineItemContext> list) throws Exception {
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		FacilioField field = Constants.getModBean().getField("unitOfMeasure", FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		if(field == null){
			return;
		}
		EnumField enumField = (EnumField) field;
		List<EnumFieldValue<Integer>> enumValues = enumField.getValues();
		if(CollectionUtils.isEmpty(enumValues)){
			return;
		}

		for (V3PurchaseOrderLineItemContext lineItem : list) {
			Long unitOfMeasure = lineItem.getUnitOfMeasure();
			if (unitOfMeasure == null || unitOfMeasure <= 0) {
				continue;
			}
			EnumFieldValue<Integer> value = enumValues.stream().filter(e -> e.getIndex() == unitOfMeasure.intValue()).findFirst().orElse(null);

			if (value == null) {
				continue;
			}
			lineItem.setUnitOfMeasureEnum(value.getValue());
		}
	}

	private static void setLineItemName(List<V3PurchaseOrderLineItemContext> lineItems) {
		if(CollectionUtils.isEmpty(lineItems)){
			return;
		}
		for (V3PurchaseOrderLineItemContext lineItem: lineItems) {
			if(lineItem.getInventoryTypeEnum() == null){
				continue;
			}
			if(lineItem.getInventoryTypeEnum() == InventoryType.ITEM){
				if(lineItem.getItemType() == null){
					continue;
				}
				lineItem.setName(lineItem.getItemType().getName());
			}
			if(lineItem.getInventoryTypeEnum() == InventoryType.TOOL){
				if(lineItem.getToolType() == null){
					continue;
				}
				lineItem.setName(lineItem.getToolType().getName());
			}
			if(lineItem.getInventoryTypeEnum() == InventoryType.SERVICE){
				if(lineItem.getService() == null){
					continue;
				}
				lineItem.setName(lineItem.getService().getName());
			}
		}
	}

	public static V3PurchaseOrderContext getPoContext(long poId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String poModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER;
		List<FacilioField> fields = modBean.getAllFields(poModuleName);

		SelectRecordsBuilder<V3PurchaseOrderContext> builder = new SelectRecordsBuilder<V3PurchaseOrderContext>()
				.moduleName(poModuleName)
				.select(fields)
				.beanClass(V3PurchaseOrderContext.class)
				.andCondition(CriteriaAPI.getIdCondition(poId, modBean.getModule(poModuleName)))
		        ;
		List<V3PurchaseOrderContext> list = builder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		throw new IllegalArgumentException("No relevant PO found");

	}
	public static PurchaseOrderLineItemContext getPoLineItemContext(long poLineItemId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String poLineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(poLineItemModuleName);

		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.moduleName(poLineItemModuleName)
				.select(fields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(poLineItemModuleName))
				.andCondition(CriteriaAPI.getIdCondition(poLineItemId, modBean.getModule(poLineItemModuleName)))
		        ;
		List<PurchaseOrderLineItemContext> list = builder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		throw new IllegalArgumentException("No relevant PO line item found");

	}

	public static List<PurchaseOrderLineItemContext> getPoReceivedLineItemList(int inventoryType) throws Exception {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

			FacilioField availableQuantityField = new FacilioField();
			availableQuantityField.setColumnName("QUANTITY_RECEIVED - QUANTITY_USED");
			availableQuantityField.setName("availableQuantity");
			availableQuantityField.setModule(modBean.getModule(lineItemModuleName));


			SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("INVENTORY_TYPE", "inventoryType", String.valueOf(inventoryType), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(availableQuantityField, String.valueOf(0), NumberOperators.GREATER_THAN))
			        .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service")))
			        ;
			List<PurchaseOrderLineItemContext> list = builder.get();
			return list;

	}
	
	public static void updatePoReceivable(long poId,List<FacilioField> updatedFields, Map<String, Object> updateMap) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
		
		UpdateRecordBuilder<ReceivableContext> updateBuilder = new UpdateRecordBuilder<ReceivableContext>()
				.module(module)
				.fields(updatedFields)
				.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(poId), NumberOperators.EQUALS))
			;
		updateBuilder.updateViaMap(updateMap);
	
	}
	
	public static void updateTermsAssociated(Long id, List<PoAssociatedTermsContext> associatedTerms) throws Exception {
		if(CollectionUtils.isNotEmpty(associatedTerms)) {
			for(PoAssociatedTermsContext term : associatedTerms) {
				term.setPoId(id);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			RecordAPI.addRecord(false, associatedTerms, module, fields);
		}
	}

	public static void updateTermsAssociatedV3(List<V3PoAssociatedTermsContext> associatedTerms) throws Exception {
		if(CollectionUtils.isNotEmpty(associatedTerms)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			RecordAPI.addRecord(false, associatedTerms, module, fields);
		}
	}

	public static List<TermsAndConditionContext> fetchPoDefaultTerms() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SelectRecordsBuilder<TermsAndConditionContext> builder = new SelectRecordsBuilder<TermsAndConditionContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("DEFAULT_ON_PO", "defaultOnPo", String.valueOf(1), NumberOperators.EQUALS))
				;
		;
		List<TermsAndConditionContext> list = builder.get();
				
		
		return list;
			                 
	}

	public static List<V3TermsAndConditionContext> fetchPoDefaultTermsV3() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());

		SelectRecordsBuilder<V3TermsAndConditionContext> builder = new SelectRecordsBuilder<V3TermsAndConditionContext>()
				.module(module)
				.beanClass(V3TermsAndConditionContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("DEFAULT_ON_PO", "defaultOnPo", String.valueOf(1), NumberOperators.EQUALS))
				;
		;
		List<V3TermsAndConditionContext> list = builder.get();


		return list;

	}
	
	public static List<PoAssociatedTermsContext> fetchAssociatedTerms(Long poId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
		LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		termsField.addChildSupplement(termsLongDescField);
		
		SelectRecordsBuilder<PoAssociatedTermsContext> builder = new SelectRecordsBuilder<PoAssociatedTermsContext>()
				.module(module)
				.beanClass(PoAssociatedTermsContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("PURCHASE_ORDER_ID", "poId", String.valueOf(poId),NumberOperators.EQUALS))
				.fetchSupplement(termsField)
		;
		List<PoAssociatedTermsContext> list = builder.get();
		return list;
			                 
	}

	public static List<V3PoAssociatedTermsContext> fetchAssociatedTermsV3(Long poId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

		LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
		LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		termsField.addChildSupplement(termsLongDescField);

		SelectRecordsBuilder<V3PoAssociatedTermsContext> builder = new SelectRecordsBuilder<V3PoAssociatedTermsContext>()
				.module(module)
				.beanClass(V3PoAssociatedTermsContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PURCHASE_ORDER_ID", "poId", String.valueOf(poId),NumberOperators.EQUALS))
				.fetchSupplement(termsField)
				;
		List<V3PoAssociatedTermsContext> list = builder.get();
		return list;
	}

	public static List<PrAssociatedTermsContext> fetchAssociatedPrTerms(Long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
		LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		termsField.addChildSupplement(termsLongDescField);

		SelectRecordsBuilder<PrAssociatedTermsContext> builder = new SelectRecordsBuilder<PrAssociatedTermsContext>()
				.module(module)
				.beanClass(PrAssociatedTermsContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("PURCHASE_REQUEST_ID", "purchaseRequest", String.valueOf(id),NumberOperators.EQUALS))
				.fetchSupplement(termsField)
				;
		List<PrAssociatedTermsContext> list = builder.get();
		return list;
	}
	public static List<PrAssociatedTermsContext> fetchTermsForConvertPo(List<Long> idList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

		LookupFieldMeta termsField = new LookupFieldMeta((LookupField) fieldsAsMap.get("terms"));
		LargeTextField termsLongDescField = (LargeTextField) modBean.getField("longDesc", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		termsField.addChildSupplement(termsLongDescField);

		List<FacilioField> termsFields = modBean.getAllFields(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		Map<String, FacilioField> termsFieldsAsMap = FieldFactory.getAsMap(termsFields);
		Criteria lookupCriteria = new Criteria();
		lookupCriteria.addAndCondition(CriteriaAPI.getCondition(termsFieldsAsMap.get("defaultOnPo"), String.valueOf(false) , BooleanOperators.IS));
		SelectRecordsBuilder<PrAssociatedTermsContext> builder = new SelectRecordsBuilder<PrAssociatedTermsContext>()
				.module(module)
				.beanClass(PrAssociatedTermsContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("terms"), lookupCriteria, LookupOperator.LOOKUP))
				.andCondition(CriteriaAPI.getCondition("PURCHASE_REQUEST_ID", "purchaseRequest", StringUtils.join(idList, ","),NumberOperators.EQUALS))
				.fetchSupplement(termsField)
				;
		List<PrAssociatedTermsContext> list = builder.get();
		return list;
	}
}
