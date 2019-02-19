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
import com.facilio.bmsconsole.context.InventoryStatusContext;
import com.facilio.bmsconsole.context.ItemCategoryContext;
import com.facilio.bmsconsole.context.ItemStatusContext;
import com.facilio.bmsconsole.context.StockedToolsStatusContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.ToolsCategoryContext;
import com.facilio.bmsconsole.context.ToolsStatusContext;
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
	
	private ItemCategoryContext itemCategory;
	public ItemCategoryContext getItemCategory() {
		return itemCategory;
	}
	public void setItemCategory(ItemCategoryContext itemCategory) {
		this.itemCategory = itemCategory;
	}
	
	private long itemCategoryId;
	public long getItemCategoryId() {
		return itemCategoryId;
	}
	public void setItemCategoryId(long itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}
	
	public String addItemCategory() throws Exception {
		if(itemCategory.getDisplayName() != null && !itemCategory.getDisplayName().isEmpty()) {
			itemCategory.setName(itemCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		itemCategory.setTtime(System.currentTimeMillis());
		itemCategory.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getItemCategory());
		Chain addItemCategoryChain = TransactionChainFactory.getAddItemCategoryChain();
		addItemCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemCategory());
		return SUCCESS;
	}
	
	public String updateItemCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		itemCategory.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemCategory().getId()));
		Chain updateItemCategoryChain = TransactionChainFactory.getUpdateItemCategoryChain();
		updateItemCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemCategory());
		return SUCCESS;
	}
	
	public String deleteItemCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemCategoryId));
		Chain deleteItemCategoryChain = TransactionChainFactory.getDeleteItemCategoryChain();
		deleteItemCategoryChain.execute(context);
		setResult("itemCategoryId", itemCategoryId);
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
	public long getItemStatusId() {
		return itemStatusId;
	}
	public void setItemStatusId(long itemStatusId) {
		this.itemStatusId = itemStatusId;
	}
	
	public String addItemStatus() throws Exception {
		if(itemStatus.getDisplayName() != null && !itemStatus.getDisplayName().isEmpty()) {
			itemStatus.setName(itemStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		itemStatus.setTtime(System.currentTimeMillis());
		itemStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getItemStatus());
		Chain addItemstatusChain = TransactionChainFactory.getAddItemStatusChain();
		addItemstatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String updateItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		itemStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemStatus().getId()));
		Chain updateItemstatusChain = TransactionChainFactory.getUpdateItemStatusChain();
		updateItemstatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String deleteItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemStatusId));
		Chain deleteItemStatusChain = TransactionChainFactory.getDeleteItemStatusChain();
		deleteItemStatusChain.execute(context);
		setResult("itemStatusId", itemStatusId);
		return SUCCESS;
	}
	
	private ToolsCategoryContext toolsCategory;
	public ToolsCategoryContext getToolsCategory() {
		return toolsCategory;
	}
	public void setToolsCategory(ToolsCategoryContext toolsCategory) {
		this.toolsCategory = toolsCategory;
	}
	
	private long toolsCategoryId;
	public long getToolsCategoryId() {
		return toolsCategoryId;
	}
	public void setToolsCategoryId(long toolsCategoryId) {
		this.toolsCategoryId = toolsCategoryId;
	}
	
	public String addToolsCategory() throws Exception {
		if(toolsCategory.getDisplayName() != null && !toolsCategory.getDisplayName().isEmpty()) {
			toolsCategory.setName(toolsCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		toolsCategory.setTtime(System.currentTimeMillis());
		toolsCategory.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolsCategory());
		Chain addToolsCategoryChain = TransactionChainFactory.getAddToolsCategoryChain();
		addToolsCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolsCategory());
		return SUCCESS;
	}
	
	public String updateToolsCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		toolsCategory.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getToolsCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolsCategory().getId()));
		Chain updateToolsCategoryChain = TransactionChainFactory.getUpdateToolsCategoryChain();
		updateToolsCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolsCategory());
		return SUCCESS;
	}
	
	public String deleteToolsCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolsCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolsCategoryId));
		Chain deleteToolsCategoryChain = TransactionChainFactory.getDeleteToolsCategoryChain();
		deleteToolsCategoryChain.execute(context);
		setResult("toolsCategoryId", toolsCategoryId);
		return SUCCESS;
	}
	
	private ToolsStatusContext toolsStatus;
	public ToolsStatusContext getToolsStatus() {
		return toolsStatus;
	}

	public void setToolsStatus(ToolsStatusContext toolsStatus) {
		this.toolsStatus = toolsStatus;
	}

	private long toolsStatusId;
	public long getToolsStatusId() {
		return toolsStatusId;
	}

	public void setToolsStatusId(long toolsStatusId) {
		this.toolsStatusId = toolsStatusId;
	}
	
	public String addToolsStatus() throws Exception {
		if(toolsStatus.getDisplayName() != null && !toolsStatus.getDisplayName().isEmpty()) {
			toolsStatus.setName(toolsStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		toolsStatus.setTtime(System.currentTimeMillis());
		toolsStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolsStatus());
		Chain addToolsStatusChain = TransactionChainFactory.getAddToolsStatusChain();
		addToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolsStatus());
		return SUCCESS;
	}
	
	public String updateToolsStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		toolsStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getToolsStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolsStatus().getId()));
		Chain updateToolsStatusChain = TransactionChainFactory.getUpdateToolsStatusChain();
		updateToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolsStatus());
		return SUCCESS;
	}
	
	public String deleteToolsStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolsStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolsStatusId));
		Chain deleteToolsStatusChain = TransactionChainFactory.getDeleteToolsStatusChain();
		deleteToolsStatusChain.execute(context);
		setResult("toolsStatusId", toolsStatusId);
		return SUCCESS;
	}
	
	private StockedToolsStatusContext stockedToolsStatus;
	public StockedToolsStatusContext getStockedToolsStatus() {
		return stockedToolsStatus;
	}
	public void setStockedToolsStatus(StockedToolsStatusContext stockedToolsStatus) {
		this.stockedToolsStatus = stockedToolsStatus;
	}
	
	private long stockedToolsStatusId;
	public long getStockedToolsStatusId() {
		return stockedToolsStatusId;
	}
	public void setStockedToolsStatusId(long stockedToolsStatusId) {
		this.stockedToolsStatusId = stockedToolsStatusId;
	}
	
	public String addStockedToolsStatus() throws Exception {
		if(stockedToolsStatus.getDisplayName() != null && !stockedToolsStatus.getDisplayName().isEmpty()) {
			stockedToolsStatus.setName(stockedToolsStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		stockedToolsStatus.setTtime(System.currentTimeMillis());
		stockedToolsStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getStockedToolsStatus());
		Chain addStockedToolsStatusChain = TransactionChainFactory.getAddStockedToolsStatusChain();
		addStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getStockedToolsStatus());
		return SUCCESS;
	}
	
	public String updateStockedToolsStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		stockedToolsStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getStockedToolsStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getStockedToolsStatus().getId()));
		Chain updateStockedToolsStatusChain = TransactionChainFactory.getUpdateStockedToolsStatusChain();
		updateStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getStockedToolsStatus());
		return SUCCESS;
	}
	
	public String deleteStockedToolsStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.stockedToolsStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.stockedToolsStatusId));
		Chain deleteStockedToolsStatusChain = TransactionChainFactory.getDeleteStockedToolsStatusChain();
		deleteStockedToolsStatusChain.execute(context);
		setResult("stockedToolsStatusId", stockedToolsStatusId);
		return SUCCESS;
	}
	
	private InventoryStatusContext inventoryStatus;
	public InventoryStatusContext getInventoryStatus() {
		return inventoryStatus;
	}
	public void setInventoryStatus(InventoryStatusContext inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}
	
	private long inventoryStatusId;
	public long getInventoryStatusId() {
		return inventoryStatusId;
	}
	public void setInventoryStatusId(long inventoryStatusId) {
		this.inventoryStatusId = inventoryStatusId;
	}
	
	public String addInventoryStatus() throws Exception {
		if(inventoryStatus.getDisplayName() != null && !inventoryStatus.getDisplayName().isEmpty()) {
			inventoryStatus.setName(inventoryStatus.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		inventoryStatus.setTtime(System.currentTimeMillis());
		inventoryStatus.setModifiedTime(System.currentTimeMillis());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getInventoryStatus());
		Chain addInventoryStatusChain = TransactionChainFactory.getAddInventoryStatusChain();
		addInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getInventoryStatus());
		return SUCCESS;
	}
	
	public String updateInventoryStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		inventoryStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getInventoryStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getInventoryStatus().getId()));
		Chain updateInventoryStatusChain = TransactionChainFactory.getUpdateInventoryStatusChain();
		updateInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getInventoryStatus());
		return SUCCESS;
	}
	
	public String deleteInventoryStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.inventoryStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.inventoryStatusId));
		Chain deleteStockedToolsStatusChain = TransactionChainFactory.getInventoryStatusChain();
		deleteStockedToolsStatusChain.execute(context);
		setResult("inventoryStatusId", inventoryStatusId);
		return SUCCESS;
	}
}
