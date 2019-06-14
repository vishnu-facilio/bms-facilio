package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ToolsApi {
	public static ToolTypesContext getToolTypes(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> selectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<ToolTypesContext> tools = selectBuilder.get();

		if (tools != null && !tools.isEmpty()) {
			return tools.get(0);
		}
		return null;
	}
	
	public static ToolContext getTool(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) fieldsAsMap.get("toolType"));
		lookUpfields.add((LookupField) fieldsAsMap.get("storeRoom"));
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolContext.class).andCondition(CriteriaAPI.getIdCondition(id, module))
				.fetchLookups(lookUpfields);

		List<ToolContext> tools = selectBuilder.get();

		if (tools != null && !tools.isEmpty()) {
			return tools.get(0);
		}
		return null;
	}
	
	public static Map<String, Long> getToolTypesMap() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> selectBuilder = new SelectRecordsBuilder<ToolTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ToolTypesContext.class);

		List<ToolTypesContext> tools = selectBuilder.get();
		Map<String, Long> toolNameVsIdMap = new HashMap<>();
		if (tools != null && !tools.isEmpty()) {
			for(ToolTypesContext toolType : tools) {
				toolNameVsIdMap.put(toolType.getName(), toolType.getId());
			}
			return toolNameVsIdMap;
		}
		return null;
	}

	public static List<ToolContext> getToolsForStore(long storeId) throws Exception {
		if (storeId <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
				;
		List<ToolContext> tools = selectBuilder.get();
		return tools;
	}

		public static ToolTransactionContext getToolTransactionsForRequestedLineItem(long requestedLineItem) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTransactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> toolTransactionFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

		SelectRecordsBuilder<ToolTransactionContext> selectBuilder = new SelectRecordsBuilder<ToolTransactionContext>()
				.select(toolTransactionFields).table(toolTransactionModule.getTableName())
				.moduleName(toolTransactionModule.getName()).beanClass(ToolTransactionContext.class)
				.andCondition(CriteriaAPI.getCondition("REQUESTED_LINEITEM", "requestedLineItem", String.valueOf(requestedLineItem), NumberOperators.EQUALS));

		List<ToolTransactionContext> toolTransactions = selectBuilder.get();
		if(!CollectionUtils.isEmpty(toolTransactions)) {
			return toolTransactions.get(0);
		}
		throw new IllegalArgumentException("Tool shoud be issued before being used");
	}
	
	public static List<ToolContext> getToolsForType(long toolTypeId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition("TOOL_TYPES_ID", "toolType", String.valueOf(toolTypeId), NumberOperators.EQUALS))
				
				;
		List<ToolContext> tools = selectBuilder.get();
		if(!CollectionUtils.isEmpty(tools)) {
			return tools;
		}
	 throw new IllegalArgumentException("No appropriate tool found");
	}


	public static long getLastPurchasedToolDateForToolId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
		long lastPurchasedDate = -1;
		SelectRecordsBuilder<AssetContext> itemselectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(assetFields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingTool"), String.valueOf(id), NumberOperators.EQUALS))
				.orderBy("PURCHASED_DATE DESC");
		List<AssetContext> assetscontext = itemselectBuilder.get();
		if(assetscontext!=null && !assetscontext.isEmpty()) {
			lastPurchasedDate = assetscontext.get(0).getPurchasedDate();
		}

		return lastPurchasedDate;
	}

	public static void updateLastPurchasedDateForTool(ToolContext tool)
			throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
		updateBuilder.update(tool);
	}

	public static void updatelastPurchaseddetailsInToolType(long id) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);

		FacilioModule transactionModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		List<FacilioField> transactionFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		Map<String, FacilioField> transactionFieldMap = FieldFactory.getAsMap(transactionFields);

		long lastPurchasedDate = -1, lastIssuedDate = -1;

			SelectRecordsBuilder<ToolContext> builder = new SelectRecordsBuilder<ToolContext>()
					.select(toolFields).moduleName(toolModule.getName())
					.andCondition(CriteriaAPI.getCondition(toolFieldMap.get("toolType"), String.valueOf(id),
							NumberOperators.EQUALS))
					.beanClass(ToolContext.class).orderBy("LAST_PURCHASED_DATE DESC");

			List<ToolContext> tools = builder.get();
			long storeRoomId = -1;
			ToolContext tool;
			if (tools != null && !tools.isEmpty()) {
				tool = tools.get(0);
				storeRoomId = tool.getStoreRoom().getId();
				lastPurchasedDate = tool.getLastPurchasedDate();
			}

			SelectRecordsBuilder<ToolTransactionContext> issuetransactionsbuilder = new SelectRecordsBuilder<ToolTransactionContext>()
					.select(transactionFields).moduleName(transactionModule.getName())
					.andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("toolType"),
							String.valueOf(id), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(transactionFieldMap.get("transactionState"),
							String.valueOf(2), NumberOperators.EQUALS))
					.beanClass(ToolTransactionContext.class).orderBy("CREATED_TIME DESC");

			List<ToolTransactionContext> transactions = issuetransactionsbuilder.get();
			ToolTransactionContext transaction;
			if (transactions != null && !transactions.isEmpty()) {
				transaction = transactions.get(0);
				lastIssuedDate = transaction.getSysCreatedTime();
			}

			ToolTypesContext toolType = new ToolTypesContext();
			toolType.setId(id);
			toolType.setLastPurchasedDate(lastPurchasedDate);
			toolType.setLastIssuedDate(lastIssuedDate);

			UpdateRecordBuilder<ToolTypesContext> updateBuilder = new UpdateRecordBuilder<ToolTypesContext>()
					.module(toolTypesModule).fields(modBean.getAllFields(toolTypesModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(id, toolTypesModule));

			updateBuilder.update(toolType);
			StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
	}

	public static ToolContext getTool(ToolTypesContext toolType, StoreRoomContext storeroom) throws Exception {
		ToolContext toolc = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
		SelectRecordsBuilder<ToolContext> itemselectBuilder = new SelectRecordsBuilder<ToolContext>().select(toolFields)
				.table(toolModule.getTableName()).moduleName(toolModule.getName()).beanClass(ToolContext.class)
				.andCondition(CriteriaAPI.getCondition(toolFieldsMap.get("storeRoom"),
						String.valueOf(storeroom.getId()), NumberOperators.EQUALS)
						);

		List<ToolContext> tools = itemselectBuilder.get();
		if (tools != null && !tools.isEmpty()) {
			for (ToolContext tool : tools) {
				if (tool.getToolType().getId() == toolType.getId()) {
					return tool;
				}
			}
			return addTool(toolModule, toolFields,  storeroom, toolType);
		} else {
			return addTool(toolModule, toolFields, storeroom, toolType);
		}
	}

	public static ToolContext addTool(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ToolTypesContext toolType) throws Exception {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(store);
		tool.setToolType(toolType);
		InsertRecordBuilder<ToolContext> readingBuilder = new InsertRecordBuilder<ToolContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(tool);
		return tool;
	}

	public static ToolContext getToolsForTypeAndStore(long storeId, long toolTypeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
        SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(ToolContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TOOL_TYPE_ID", "toolType", String.valueOf(toolTypeId), NumberOperators.EQUALS))

                ;
        List<ToolContext> tools = selectBuilder.get();
        if(!CollectionUtils.isEmpty(tools)) {
            return tools.get(0);
        }
     throw new IllegalArgumentException("No appropriate Tool found");
    }

}
