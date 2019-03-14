package com.facilio.bmsconsole.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
//import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetDepartmentContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.ItemStatusContext;
import com.facilio.bmsconsole.context.ItemTypesCategoryContext;
import com.facilio.bmsconsole.context.ItemTypesStatusContext;
import com.facilio.bmsconsole.context.ToolStatusContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.ToolTypesCategoryContext;
import com.facilio.bmsconsole.context.ToolTypesStatusContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PickListAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			setPickList(LookupSpecialTypeUtil.getPickList(moduleName));
		}
		else {
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
			//System.out.println("Context is "+context);
			//FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
			Chain pickListChain = FacilioChainFactory.getPickListChain();
			pickListChain.execute(context);
			//FacilioTransactionManager.INSTANCE.getTransactionManager().commit();			
			setPickList((Map<Long, String>) context.get(FacilioConstants.ContextNames.PICKLIST));
		}
		
		return SUCCESS;
	}
	
	private Map<Long, String> pickList;
	public Map<Long, String> getPickList() {
		return pickList;
	}
	public void setPickList(Map<Long, String> pickList) {
		this.pickList = pickList;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	TicketCategoryContext ticketCategory;
	public TicketCategoryContext getTicketCategory() {
		return ticketCategory;
	}
	public void setTicketCategory(TicketCategoryContext ticketCategory) {
		this.ticketCategory = ticketCategory;
	}

	public String addTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		Chain addTicketCategoryChain = FacilioChainFactory.getAddTicketCategoryChain();
		addTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketCategory().getId()));
		Chain updateTicketCategoryChain = FacilioChainFactory.getUpdateTicketCategoryChain();
		updateTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketCategory().getId()));
		Chain deleteTicketCategoryChain = FacilioChainFactory.getDeleteTicketCategoryChain();
		deleteTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	
	TicketPriorityContext ticketPriority;
	public TicketPriorityContext getTicketPriority() {
		return ticketPriority;
	}
	public void setTicketPriority(TicketPriorityContext ticketPriority) {
		this.ticketPriority = ticketPriority;
	}
	List<TicketPriorityContext> ticketPriorties;
	public List<TicketPriorityContext> getTicketPriorties() {
		return ticketPriorties;
	}
	public void setTicketPriorties(List<TicketPriorityContext> ticketPriorties) {
		this.ticketPriorties = ticketPriorties;
	}

	public String addTicketPriority() throws Exception {
		if(ticketPriority.getPriority() == null || ticketPriority.getPriority().isEmpty()) {
			if(ticketPriority.getDisplayName() != null && !ticketPriority.getDisplayName().isEmpty()) {
				ticketPriority.setPriority(ticketPriority.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			}
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		Chain addTicketPriorityChain = FacilioChainFactory.getAddTicketPriorityChain();
		addTicketPriorityChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateTicketPriority() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketPriority().getId()));
		Chain updateTicketPriorityChain = FacilioChainFactory.getUpdateTicketPriorityChain();
		updateTicketPriorityChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		return SUCCESS;
	}
	
	public String deleteTicketPriority() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketPriority().getId()));
		Chain deleteTicketPriorityChain = FacilioChainFactory.getDeleteTicketPriorityChain();
		deleteTicketPriorityChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	TicketTypeContext ticketType;
	public TicketTypeContext getTicketType() {
		return ticketType;
	}
	public void setTicketType(TicketTypeContext ticketType) {
		this.ticketType = ticketType;
	}

	public String addTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		Chain addTicketTypeChain = FacilioChainFactory.getAddTicketTypeChain();
		addTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	public String updateTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketType().getId()));
		Chain updateTicketTypeChain = FacilioChainFactory.getUpdateTicketTypeChain();
		updateTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	public String deleteTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketType().getId()));
		Chain deleteTicketTypeChain = FacilioChainFactory.getDeleteTicketTypeChain();
		deleteTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	AssetCategoryContext assetCategory;
	public AssetCategoryContext getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCategoryContext assetCategory) {
		this.assetCategory = assetCategory;
	}
	
	private long assetCategoryID;
	public long getAssetCategoryID() {
		return assetCategoryID;
	}
	public void setAssetCategoryID(long assetCategoryID) {
		this.assetCategoryID = assetCategoryID;
	}
	
	public String addAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetCategory());
		Chain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
		addAssetCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.assetCategory.getId()));
		Chain updateAssetCategoryChain = FacilioChainFactory.getUpdateAssetCategoryChain();
		updateAssetCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	private List<String> relatedModules;
	public List<String> getRelatedModules() {
		return relatedModules;
	}
	public void setRelatedModules(List<String> relatedModules) {
		this.relatedModules = relatedModules;
	}
	
	public String deleteAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.getAssetCategoryID());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.getAssetCategoryID()));
		Command deleteAssetCategory = FacilioChainFactory.getDeleteAssetCategoryChain();
		deleteAssetCategory.execute(context);
		
		this.relatedModules = (List<String>) context.get(FacilioConstants.ContextNames.MODULE_NAMES);
		if (!this.relatedModules.isEmpty()) {
			return ERROR;
		}
		return SUCCESS;
	}
	
	AssetDepartmentContext assetDepartment;
	public AssetDepartmentContext getAssetDepartment() {
		return assetDepartment;
	}
	public void setAssetDepartment(AssetDepartmentContext assetDepartment) {
		this.assetDepartment = assetDepartment;
	}

	public String addAssetDepartment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetDepartment());
		Chain addAssetDepartmentChain = FacilioChainFactory.getAddAssetDepartmentChain();
		addAssetDepartmentChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetDepartment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetDepartment());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.assetDepartment.getId()));
		Chain updateAssetDepartmentChain = FacilioChainFactory.getUpdateAssetDepartmentChain();
		updateAssetDepartmentChain.execute(context);
		
		return SUCCESS;
	}
	
	private long assetDepartmentID;
	
	
	public long getAssetDepartmentID() {
		return assetDepartmentID;
	}
	public void setAssetDepartmentID(long assetDepartmentID) {
		this.assetDepartmentID = assetDepartmentID;
	}
	
	public String deleteAssetDepartment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.getAssetDepartmentID());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.getAssetDepartmentID()));
		Command deleteAssetDepartment = FacilioChainFactory.getDeleteAssetDepartmentChain();
		deleteAssetDepartment.execute(context);
		
		this.relatedModules = (List<String>) context.get(FacilioConstants.ContextNames.MODULE_NAMES);
		if (!this.relatedModules.isEmpty()) {
			return ERROR;
		}
		return SUCCESS;
	}
	
	AssetTypeContext assetType;
	public AssetTypeContext getAssetType() {
		return assetType;
	}
	public void setAssetType(AssetTypeContext assetType) {
		this.assetType = assetType;
	}

	public String addAssetType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetType());
		Chain addAssetTypeChain = FacilioChainFactory.getAddAssetTypeChain();
		addAssetTypeChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.getAssetType().getId()));
		Chain updateAssetTypeChain = FacilioChainFactory.getUpdateAssetTypeChain();
		updateAssetTypeChain.execute(context);
		
		return SUCCESS;
	}

	private long assetTypeID;
	
	public long getAssetTypeID() {
		return assetTypeID;
	}
	public void setAssetTypeID(long assetTypeID) {
		this.assetTypeID = assetTypeID;
	}
	
	public String deleteAssetType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.getAssetTypeID());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.getAssetTypeID()));
		Command deleteAssetDepartment = FacilioChainFactory.getDeleteAssetTypeChain();
		deleteAssetDepartment.execute(context);
		
		this.relatedModules = (List<String>) context.get(FacilioConstants.ContextNames.MODULE_NAMES);
		if (!this.relatedModules.isEmpty()) {
			return ERROR;
		}
		return SUCCESS;
	}
	
	AlarmSeverityContext assetSeverity;
	public AlarmSeverityContext getAssetSeverity() {
		return assetSeverity;
	}
	public void setAssetSeverity(AlarmSeverityContext assetSeverity) {
		this.assetSeverity = assetSeverity;
	}

	public String addAlarmSeverity() throws Exception {
		if(assetSeverity.getSeverity() == null || assetSeverity.getSeverity().isEmpty()) {
			if(assetSeverity.getDisplayName() != null && !assetSeverity.getDisplayName().isEmpty()) {
				assetSeverity.setSeverity(assetSeverity.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			}
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		Chain addAlarmSeverityChain = FacilioChainFactory.getAddAlarmSeverityChain();
		addAlarmSeverityChain.execute(context);
		
		return SUCCESS;
	}
	List<AlarmSeverityContext> alarmSeverities;
	public List<AlarmSeverityContext> getAlarmSeverities() {
		return alarmSeverities;
	}
	public void setAlarmSeverities(List<AlarmSeverityContext> alarmSeverities) {
		this.alarmSeverities = alarmSeverities;
	}
	
	
	
	
/******************      V2 Api    
 * @throws Exception ******************/
	
	public String v2pickList() throws Exception {
		execute();
		setResult(FacilioConstants.ContextNames.PICKLIST, pickList);
		return SUCCESS;
	}
	public String v2updateTicketPriorties() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getTicketPriorties());
		Chain updateTicketPrioritiesChain = FacilioChainFactory.getUpdateTicketPrioritiesChain();
		updateTicketPrioritiesChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
		
	}
	
	public String v2updateAlarmSeverities() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getAlarmSeverities()	);
		Chain updateAlarmSeveritiesChain = FacilioChainFactory.getUpdateAlarmSeveritiesChain();
		updateAlarmSeveritiesChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
		
	}
	public String v2deleteAlarmSeverity () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getAssetSeverity().getId()));
		Chain deleteAlarmSeverityChain = FacilioChainFactory.getDeleteAlarmSeverityChain();
		deleteAlarmSeverityChain.execute(context);
		return SUCCESS;
	}
	public String v2updateAlarmSeverity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getAssetSeverity().getId()));
		Chain updateAlarmSeverityChain = FacilioChainFactory.getUpdateAlarmSeverityChain();
		updateAlarmSeverityChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, assetSeverity);
		
		return SUCCESS;
	}
	
	public String v2addTicketPriority () throws Exception {
		addTicketPriority();
		setResult(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		return SUCCESS;
	}
	public String v2addAlarmSeverity () throws Exception {
		addAlarmSeverity();
		setResult(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		return SUCCESS;
	}
	
	InventoryCategoryContext inventoryCategory;
	public InventoryCategoryContext getInventoryCategory() {
		return inventoryCategory;
	}
	public void setInventoryCategory(InventoryCategoryContext inventoryCategory) {
		this.inventoryCategory = inventoryCategory;
	}

	public String addInventoryCategory() throws Exception {
		if(inventoryCategory.getDisplayName() != null && !inventoryCategory.getDisplayName().isEmpty()) {
			inventoryCategory.setName(inventoryCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		Chain addInventoryCategoryChain = TransactionChainFactory.getAddInventoryCategoryChain();
		addInventoryCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		return SUCCESS;
	}
	
	private long inventoryCategoryId;
	public long getInventoryCategoryId() {
		return inventoryCategoryId;
	}
	public void setInventoryCategoryId(long inventoryCategoryId) {
		this.inventoryCategoryId = inventoryCategoryId;
	}
	public String updateInventoryCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getInventoryCategory().getId()));
		Chain updateInventoryCategoryChain = TransactionChainFactory.getUpdateInventoryCategoryChain();
		updateInventoryCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		return SUCCESS;
	}
	
	public String deleteInventoryCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.inventoryCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.inventoryCategoryId));
		Chain deleteInventoryCategoryChain = TransactionChainFactory.getDeleteInventoryCategoryChain();
		deleteInventoryCategoryChain.execute(context);
		setResult("inventoryCategoryId", inventoryCategoryId);
		return SUCCESS;
	}
	
	private ItemTypesCategoryContext itemTypeCategory;
	public ItemTypesCategoryContext getItemTypeCategory() {
		return itemTypeCategory;
	}
	public void setItemTypeCategory(ItemTypesCategoryContext itemCategory) {
		this.itemTypeCategory = itemCategory;
	}
	
	private long itemTypeCategoryId;
	public long getItemCategoryId() {
		return itemTypeCategoryId;
	}
	public void setItemCategoryId(long itemCategoryId) {
		this.itemTypeCategoryId = itemCategoryId;
	}
	
	public String addItemTypeCategory() throws Exception {
		if(itemTypeCategory.getDisplayName() != null && !itemTypeCategory.getDisplayName().isEmpty()) {
			itemTypeCategory.setName(itemTypeCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		itemTypeCategory.setTtime(System.currentTimeMillis());
		itemTypeCategory.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getItemTypeCategory());
		Chain addItemCategoryChain = TransactionChainFactory.getAddItemTypeCategoryChain();
		addItemCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemTypeCategory());
		return SUCCESS;
	}
	
	public String updateItemTypeCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		itemTypeCategory.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemTypeCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemTypeCategory().getId()));
		Chain updateItemCategoryChain = TransactionChainFactory.getUpdateItemTypeCategoryChain();
		updateItemCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemTypeCategory());
		return SUCCESS;
	}
	
	public String deleteItemTypeCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemTypeCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemTypeCategoryId));
		Chain deleteItemCategoryChain = TransactionChainFactory.getDeleteItemTypesCategoryChain();
		deleteItemCategoryChain.execute(context);
		setResult("itemTypeCategoryId", itemTypeCategoryId);
		return SUCCESS;
	}
	
	private ItemTypesStatusContext itemTypesStatus;
	public ItemTypesStatusContext getItemTypesStatus() {
		return itemTypesStatus;
	}
	public void setItemTypesStatus(ItemTypesStatusContext itemStatus) {
		this.itemTypesStatus = itemStatus;
	}
	
	private long itemTypesStatusId;
	public long getItemTypesStatusId() {
		return itemTypesStatusId;
	}
	public void setItemTypesStatusId(long itemStatusId) {
		this.itemTypesStatusId = itemStatusId;
	}
	
	public String addItemTypesStatus() throws Exception {
		if(itemTypesStatus.getDisplayName() != null && !itemTypesStatus.getDisplayName().isEmpty()) {
			itemTypesStatus.setName(itemTypesStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		itemTypesStatus.setTtime(System.currentTimeMillis());
		itemTypesStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getItemTypesStatus());
		Chain addItemstatusChain = TransactionChainFactory.getAddItemTypesStatusChain();
		addItemstatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemTypesStatus());
		return SUCCESS;
	}
	
	public String updateItemTypesStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		itemTypesStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemTypesStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemTypesStatus().getId()));
		Chain updateItemstatusChain = TransactionChainFactory.getUpdateItemTypesStatusChain();
		updateItemstatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemTypesStatus());
		return SUCCESS;
	}
	
	public String deleteItemTypesStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemTypesStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemTypesStatusId));
		Chain deleteItemStatusChain = TransactionChainFactory.getDeleteItemTypesStatusChain();
		deleteItemStatusChain.execute(context);
		setResult("itemTypesStatusId", itemTypesStatusId);
		return SUCCESS;
	}
	
	private ToolTypesCategoryContext toolTypesCategory;
	public ToolTypesCategoryContext getToolTypesCategory() {
		return toolTypesCategory;
	}
	public void setToolTypesCategory(ToolTypesCategoryContext toolsCategory) {
		this.toolTypesCategory = toolsCategory;
	}
	
	private long toolTypesCategoryId;
	public long getToolTypesCategoryId() {
		return toolTypesCategoryId;
	}
	public void setToolTypesCategoryId(long toolsCategoryId) {
		this.toolTypesCategoryId = toolsCategoryId;
	}
	
	public String addToolTypesCategory() throws Exception {
		if(toolTypesCategory.getDisplayName() != null && !toolTypesCategory.getDisplayName().isEmpty()) {
			toolTypesCategory.setName(toolTypesCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		toolTypesCategory.setTtime(System.currentTimeMillis());
		toolTypesCategory.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolTypesCategory());
		Chain addToolsCategoryChain = TransactionChainFactory.getAddToolTypesCategoryChain();
		addToolsCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolTypesCategory());
		return SUCCESS;
	}
	
	public String updateToolTypesCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		toolTypesCategory.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getToolTypesCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolTypesCategory().getId()));
		Chain updateToolsCategoryChain = TransactionChainFactory.getUpdateToolTypesCategoryChain();
		updateToolsCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolTypesCategory());
		return SUCCESS;
	}
	
	public String deleteToolTypesCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolTypesCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolTypesCategoryId));
		Chain deleteToolsCategoryChain = TransactionChainFactory.getDeleteToolTypesCategoryChain();
		deleteToolsCategoryChain.execute(context);
		setResult("toolTypesCategoryId", toolTypesCategoryId);
		return SUCCESS;
	}
	
	private ToolTypesStatusContext toolTypesStatus;
	public ToolTypesStatusContext getToolTypesStatus() {
		return toolTypesStatus;
	}

	public void setToolTypesStatus(ToolTypesStatusContext toolsStatus) {
		this.toolTypesStatus = toolsStatus;
	}

	private long toolTypesStatusId;
	public long getToolTypesStatusId() {
		return toolTypesStatusId;
	}

	public void setToolTypesStatusId(long toolsStatusId) {
		this.toolTypesStatusId = toolsStatusId;
	}
	
	public String addToolTypesStatus() throws Exception {
		if(toolTypesStatus.getDisplayName() != null && !toolTypesStatus.getDisplayName().isEmpty()) {
			toolTypesStatus.setName(toolTypesStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		toolTypesStatus.setTtime(System.currentTimeMillis());
		toolTypesStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolTypesStatus());
		Chain addToolsStatusChain = TransactionChainFactory.getAddToolsStatusChain();
		addToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolTypesStatus());
		return SUCCESS;
	}
	
	public String updateToolTypesStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		toolTypesStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getToolTypesStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolTypesStatus().getId()));
		Chain updateToolsStatusChain = TransactionChainFactory.getUpdateToolsStatusChain();
		updateToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolTypesStatus());
		return SUCCESS;
	}
	
	public String deleteToolTypesStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolTypesStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolTypesStatusId));
		Chain deleteToolsStatusChain = TransactionChainFactory.getDeleteToolsStatusChain();
		deleteToolsStatusChain.execute(context);
		setResult("toolsStatusId", toolTypesStatusId);
		return SUCCESS;
	}
	
	private ToolStatusContext toolStatus;
	public ToolStatusContext getToolStatus() {
		return toolStatus;
	}
	public void setToolStatus(ToolStatusContext stockedToolsStatus) {
		this.toolStatus = stockedToolsStatus;
	}
	
	private long toolStatusId;
	public long getToolStatusId() {
		return toolStatusId;
	}
	public void setToolStatusId(long stockedToolsStatusId) {
		this.toolStatusId = stockedToolsStatusId;
	}
	
	public String addToolStatus() throws Exception {
		if(toolStatus.getDisplayName() != null && !toolStatus.getDisplayName().isEmpty()) {
			toolStatus.setName(toolStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolStatus());
		Chain addStockedToolsStatusChain = TransactionChainFactory.getAddToolStatusChain();
		addStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolStatus());
		return SUCCESS;
	}
	
	public String updateToolStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolStatus().getId()));
		Chain updateStockedToolsStatusChain = TransactionChainFactory.getUpdateToolStatusChain();
		updateStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolStatus());
		return SUCCESS;
	}
	
	public String deleteToolStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolStatusId));
		Chain deleteStockedToolsStatusChain = TransactionChainFactory.getDeleteToolStatusChain();
		deleteStockedToolsStatusChain.execute(context);
		setResult("stockedToolsStatusId", toolStatusId);
		return SUCCESS;
	}
	
	private ItemStatusContext itemStatus;
	public ItemStatusContext getItemStatus() {
		return itemStatus;
	}
	public void setItemStatus(ItemStatusContext itemStatus) {
		this.itemStatus = itemStatus;
	}
	
	private long itemStatusId;
	public long getInventoryStatusId() {
		return itemStatusId;
	}
	public void setInventoryStatusId(long inventoryStatusId) {
		this.itemStatusId = inventoryStatusId;
	}
	
	public String addItemStatus() throws Exception {
		if(itemStatus.getDisplayName() != null && !itemStatus.getDisplayName().isEmpty()) {
			itemStatus.setName(itemStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		itemStatus.setTtime(System.currentTimeMillis());
		itemStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getItemStatus());
		Chain addInventoryStatusChain = TransactionChainFactory.getAddItemStatusChain();
		addInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String updateItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		itemStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemStatus().getId()));
		Chain updateInventoryStatusChain = TransactionChainFactory.getUpdateItemStatusChain();
		updateInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String deleteItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemStatusId));
		Chain deleteStockedToolsStatusChain = TransactionChainFactory.getItemStatusChain();
		deleteStockedToolsStatusChain.execute(context);
		setResult("itemStatusId", itemStatusId);
		return SUCCESS;
	}
}
