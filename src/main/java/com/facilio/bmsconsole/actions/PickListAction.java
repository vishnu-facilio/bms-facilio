package com.facilio.bmsconsole.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.AssetDepartmentContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.ItemStatusContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.ToolStatusContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;

//import com.facilio.bmsconsole.commands.FacilioContext;

public class PickListAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void populatePicklistContext (FacilioContext context, String moduleName, String filters, String search, Criteria clientCriteria, String clientCriteriaStr, String defaultIds, String viewName, int page, int perPage) throws Exception {
		context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, clientCriteria);

		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if (search != null) {
			context.put(FacilioConstants.ContextNames.SEARCH, search);
		}
		if (page != 0) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", page);
			pagination.put("perPage", perPage);
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		if (StringUtils.isNotEmpty(clientCriteriaStr)) {
			JSONObject json = FacilioUtil.parseJson(clientCriteriaStr);
			Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
			context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
		}

		if (StringUtils.isNotEmpty(filters)) {
			JSONObject json = FacilioUtil.parseJson(filters);
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		if (StringUtils.isNotEmpty(defaultIds)) {
			String[] ids = FacilioUtil.splitByComma(defaultIds);
			List<Long> defaultIdList = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
			context.put(FacilioConstants.PickList.DEFAULT_ID_LIST, defaultIdList);
		}
		if (StringUtils.isNotEmpty(viewName)) {
	 		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		}
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			setPickList(LookupSpecialTypeUtil.getPickList(moduleName));
		}
		else {
			FacilioChain pickListChain = FacilioChainFactory.getPickListChain();
			populatePicklistContext(pickListChain.getContext(), getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());
			pickListChain.execute();
			setPickList((Map<Long, String>) pickListChain.getContext().get(FacilioConstants.ContextNames.PICKLIST));
		}
		
		return SUCCESS;
	}

	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}
	
	private int page;
	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return this.page;
	}
	
	public int perPage = 40;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public int getPerPage() {
		return this.perPage;
	}

	private String clientCriteria;
	public String getClientCriteria() {
		return clientCriteria;
	}
	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}

	private String filters;
	public void setFilters(String filters) {
		this.filters = filters;
	}
	public String getFilters() {
		return this.filters;
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

	private String _default;
	public String getDefault() {
		return _default;
	}
	public void setDefault(String _default) {
		this._default = _default;
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
		FacilioChain addTicketCategoryChain = FacilioChainFactory.getAddTicketCategoryChain();
		addTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketCategory().getId()));
		FacilioChain updateTicketCategoryChain = FacilioChainFactory.getUpdateTicketCategoryChain();
		updateTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketCategory().getId()));
		FacilioChain deleteTicketCategoryChain = FacilioChainFactory.getDeleteTicketCategoryChain();
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
		FacilioChain addTicketPriorityChain = FacilioChainFactory.getAddTicketPriorityChain();
		addTicketPriorityChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateTicketPriority() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketPriority().getId()));
		FacilioChain updateTicketPriorityChain = FacilioChainFactory.getUpdateTicketPriorityChain();
		updateTicketPriorityChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		return SUCCESS;
	}
	
	public String deleteTicketPriority() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketPriority().getId()));
		FacilioChain deleteTicketPriorityChain = FacilioChainFactory.getDeleteTicketPriorityChain();
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
		FacilioChain addTicketTypeChain = FacilioChainFactory.getAddTicketTypeChain();
		addTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	public String updateTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketType().getId()));
		FacilioChain updateTicketTypeChain = FacilioChainFactory.getUpdateTicketTypeChain();
		updateTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	public String deleteTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getTicketType().getId()));
		FacilioChain deleteTicketTypeChain = FacilioChainFactory.getDeleteTicketTypeChain();
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
		if(assetCategory.getName() == null || assetCategory.getName().isEmpty()) {
			if(assetCategory.getDisplayName() != null && !assetCategory.getDisplayName().isEmpty()) {
				assetCategory.setName(assetCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			}
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetCategory());
		FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
		addAssetCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.assetCategory.getId()));
		FacilioChain updateAssetCategoryChain = FacilioChainFactory.getUpdateAssetCategoryChain();
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
		FacilioChain addAssetDepartmentChain = FacilioChainFactory.getAddAssetDepartmentChain();
		addAssetDepartmentChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetDepartment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetDepartment());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.assetDepartment.getId()));
		FacilioChain updateAssetDepartmentChain = FacilioChainFactory.getUpdateAssetDepartmentChain();
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
		FacilioChain addAssetTypeChain = FacilioChainFactory.getAddAssetTypeChain();
		addAssetTypeChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateAssetType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.getAssetType().getId()));
		FacilioChain updateAssetTypeChain = FacilioChainFactory.getUpdateAssetTypeChain();
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
		FacilioChain addAlarmSeverityChain = FacilioChainFactory.getAddAlarmSeverityChain();
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
		FacilioChain updateTicketPrioritiesChain = FacilioChainFactory.getUpdateTicketPrioritiesChain();
		updateTicketPrioritiesChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
		
	}
	
	public String v2updateAlarmSeverities() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getAlarmSeverities()	);
		FacilioChain updateAlarmSeveritiesChain = FacilioChainFactory.getUpdateAlarmSeveritiesChain();
		updateAlarmSeveritiesChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
		
	}
	public String v2deleteAlarmSeverity () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getAssetSeverity().getId()));
		FacilioChain deleteAlarmSeverityChain = FacilioChainFactory.getDeleteAlarmSeverityChain();
		deleteAlarmSeverityChain.execute(context);
		return SUCCESS;
	}
	public String v2updateAlarmSeverity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getAssetSeverity().getId()));
		FacilioChain updateAlarmSeverityChain = FacilioChainFactory.getUpdateAlarmSeverityChain();
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
	
	VisitorTypeContext  visitorType;

	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}

	public String addInventoryCategory() throws Exception {
		if(inventoryCategory.getDisplayName() != null && !inventoryCategory.getDisplayName().isEmpty()) {
			inventoryCategory.setName(inventoryCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		FacilioChain addInventoryCategoryChain = TransactionChainFactory.getAddInventoryCategoryChain();
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
		FacilioChain updateInventoryCategoryChain = TransactionChainFactory.getUpdateInventoryCategoryChain();
		updateInventoryCategoryChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getInventoryCategory());
		return SUCCESS;
	}
	
	public String updateVisitorType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getVisitorType());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getVisitorType().getId()));
		FacilioChain updateVisitorTypeChain = TransactionChainFactory.getUpdateVisitorTypePicklistOptionChain();
		updateVisitorTypeChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getVisitorType());
		return SUCCESS;
	}
	
	public String deleteInventoryCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.inventoryCategoryId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.inventoryCategoryId));
		FacilioChain deleteInventoryCategoryChain = TransactionChainFactory.getDeleteInventoryCategoryChain();
		deleteInventoryCategoryChain.execute(context);
		setResult("inventoryCategoryId", inventoryCategoryId);
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
		FacilioChain addStockedToolsStatusChain = TransactionChainFactory.getAddToolStatusChain();
		addStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolStatus());
		return SUCCESS;
	}
	
	public String updateToolStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getToolStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getToolStatus().getId()));
		FacilioChain updateStockedToolsStatusChain = TransactionChainFactory.getUpdateToolStatusChain();
		updateStockedToolsStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getToolStatus());
		return SUCCESS;
	}
	
	public String deleteToolStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.toolStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.toolStatusId));
		FacilioChain deleteStockedToolsStatusChain = TransactionChainFactory.getDeleteToolStatusChain();
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
		FacilioChain addInventoryStatusChain = TransactionChainFactory.getAddItemStatusChain();
		addInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String updateItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		itemStatus.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, getItemStatus());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(getItemStatus().getId()));
		FacilioChain updateInventoryStatusChain = TransactionChainFactory.getUpdateItemStatusChain();
		updateInventoryStatusChain.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD, getItemStatus());
		return SUCCESS;
	}
	
	public String deleteItemStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, this.itemStatusId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(this.itemStatusId));
		FacilioChain deleteStockedToolsStatusChain = TransactionChainFactory.getItemStatusChain();
		deleteStockedToolsStatusChain.execute(context);
		setResult("itemStatusId", itemStatusId);
		return SUCCESS;
	}

	public String fetchLabels() throws Exception {
		FacilioChain fetchLabel = ReadOnlyChainFactory.fetchLabels();
		fetchLabel.getContext().put(FacilioConstants.PickList.LOOKUP_LABEL_META, labelMeta);
		fetchLabel.execute();

		Map<String, List<FieldOption<? extends Object>>> labels = (Map<String, List<FieldOption<? extends Object>>>) fetchLabel.getContext().get(FacilioConstants.PickList.LOOKUP_LABELS);
		setResult("label", labels);

		return SUCCESS;
	}

	public Map<String, List<String>> getLabelMeta() {
		return labelMeta;
	}
	public void setLabelMeta(Map<String, List<String>> labelMeta) {
		this.labelMeta = labelMeta;
	}
	private Map<String, List<String>> labelMeta;
}
