package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AssetAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String newAsset() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain newAsset = FacilioChainFactory.getNewAssetChain();
		newAsset.execute(context);
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public List getFormlayout()
	{
		return FormLayout.getNewAssetLayout(fields);
	}
	
	private Boolean withReadings;
	
	private long readingId;
	
	public long getReadingId() {
		return readingId;
	}

	public void setReadingId(long readingId) {
		this.readingId = readingId;
	}
	
	private Map<Long,String> mappedqr = new HashMap<Long, String>();
	
	private ArrayList<Long> assetQr;
	
	public void setAssetQr(ArrayList<Long> assetQr) {
		this.assetQr=assetQr;
	}
	
	public ArrayList<Long> getAssetQr() {
		return this.assetQr;
	}
	
 	
	public String generateQr() throws Exception {
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,id);
		Chain updateQrChain = TransactionChainFactory.getUpdateQrChain();
		updateQrChain.execute(context);
		Map<Long,String> mappedqr =(Map<Long,String>) context.get(FacilioConstants.ContextNames.MAP_QR);
		setResult("mappedqr",mappedqr);
		return SUCCESS;
	}

	public String addAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		AssetCategoryContext assetCategory= asset.getCategory();
		long categoryId=-1;
		if(assetCategory!=null && assetCategory.getId() != 0) {
			categoryId=assetCategory.getId();
		}
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		Chain addAssetChain = TransactionChainFactory.getAddAssetChain();
		addAssetChain.execute(context);
		setAssetId(asset.getId());
		
		return SUCCESS;
	}
	
	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}
	
	public String updateAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(asset.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		// cannot update module state directly
		if (asset.getModuleState() != null) {
			asset.setModuleState(null);
		}
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		
		Chain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();
		updateAssetChain.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String deleteAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		AssetContext asset = new AssetContext();
		asset.setDeleted(true);
		
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, assetsId);
		
		Chain deleteAssetChain = FacilioChainFactory.getDeleteAssetChain();
		deleteAssetChain.execute(context);
		setAssetsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String assetList() throws Exception {
		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
 		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Assets.LOCAL_ID desc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "asset.name,asset.serialNumber");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		if (getCount()) {	// only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		}
 		if (getSelectFields() != null) {
 			context.put(FacilioConstants.ContextNames.FETCH_SELECTED_FIELDS, getSelectFields());			
 		}
 		else {
 			JSONObject pagination = new JSONObject();
 	 		pagination.put("page", getPage());
 	 		pagination.put("perPage", getPerPage());
 	 		if (getPerPage() < 0) {
 	 			pagination.put("perPage", 5000);
 	 		}
 	 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
 		}
 		
 		
 		context.put(FacilioConstants.ContextNames.READING_ID, readingId);
 		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
 		context.put(FacilioConstants.ContextNames.INPUT_TYPE, getInputType());
 		context.put(FacilioConstants.ContextNames.WITH_READINGS, this.getWithReadings());
 		Chain assetList = FacilioChainFactory.getAssetListChain();
 		assetList.execute(context);
 		if (getCount()) {
			setAssetCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", assetCount);
		}
 		else {
 			assets = (List<AssetContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
 			// Temp...needs to handle in client
 			if (assets == null) {
 				assets = new ArrayList<>();
 			}
 			setResult("assets", assets);
 		}
		return SUCCESS;
	}
	
	private Boolean count;
	public Boolean getCount() {
		if (count == null) {
			return false;
		}
		return count;
	}
	public void setCount(Boolean count) {
		this.count = count;
	}
	
	private ReadingInputType inputType;
	public ReadingInputType getInputType() {
		return inputType;
	}
	public void setInputType(int inputType) {
		this.inputType = ReadingInputType.valueOf(inputType);
	}
	
	public String assetDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getAssetId());
		AssetContext asset= AssetsAPI.getAssetInfo(assetId, true);
		AssetCategoryContext category= asset.getCategory();
		
		if (category != null && category.getId() != -1) {
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
		}
		context.put(FacilioConstants.ContextNames.SHOW_RELATIONS_COUNT, showRelationsCount);
		Chain assetDetailsChain = FacilioChainFactory.getAssetDetailsChain();
		assetDetailsChain.execute(context);
		
		setAsset((AssetContext) context.get(FacilioConstants.ContextNames.ASSET));
		setRelationsCount((Map<String, Long>) context.get(FacilioConstants.ContextNames.RELATIONS_COUNT));
		setResult(FacilioConstants.ContextNames.ASSET, getAsset());
		setResult("relationsCount", relationsCount);
		return SUCCESS;
	}
	
	public String assetCount () throws Exception {
		return assetList();
	}
	private Boolean showRelationsCount;
	public Boolean getShowRelationsCount() {
		if (showRelationsCount == null) {
			return false;
		}
		return showRelationsCount;
	}
	public void setShowRelationsCount(Boolean showRelationsCount) {
		this.showRelationsCount = showRelationsCount;
	}
	
	private Map<String, Long> relationsCount;
	public Map<String, Long> getRelationsCount() {
		return relationsCount;
	}
	public void setRelationsCount(Map<String, Long> relationsCount) {
		this.relationsCount = relationsCount;
	}

	private long categoryId;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	private JSONObject reports;
	public JSONObject getReports() {
		return reports;
	}
	public void setReports(JSONObject reports) {
		this.reports = reports;
	}
	
	private JSONArray reportcards;
	public JSONArray getReportcards() {
		return reportcards;
	}
	public void setReportcards(JSONArray reportcards) {
		this.reportcards = reportcards;
	}
	
	public String reportCards() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getAssetId());
		
		Chain getReportCardsChain = FacilioChainFactory.getAssetReportCardsChain();
		getReportCardsChain.execute(context);
		
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.REPORTS));
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		setResult("assetCards", reportcards);
		
		return SUCCESS;
	}
	
	private List<AssetContext> assets;
	public List<AssetContext> getAssets() {
		return assets;
	}
	public void setAssets(List<AssetContext> assets) {
		this.assets = assets;
	}
	private Long assetCount;
	public Long getAssetCount() {
		if (assetCount == null) {
			assetCount = 0L;
		}
		return assetCount;
	}

	public void setAssetCount(Long assetCount) {
		this.assetCount = assetCount;
	}

	private List<Long> assetsId;

	public List<Long> getAssetsId() {
		return assetsId;
	}

	public void setAssetsId(List<Long> assetsId) {
		this.assetsId = assetsId;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private long assetId;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String filters;
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
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
	
	private int perPage = -1;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	
	public int getPerPage() {
		return this.perPage;
	}

	public Boolean getWithReadings() {
		if (withReadings == null) {
			return false;
		}
		return withReadings;
	}

	public void setWithReadings(Boolean withReadings) {
		this.withReadings = withReadings;
	}
	
	JSONObject assetsReadings;
	public JSONObject getAssetsReadings() {
		return assetsReadings;
	}
	public void setAssetsReadings(JSONObject assetsReadings) {
		this.assetsReadings = assetsReadings;
	}
	
	public String getReadingsWithAssets() throws Exception {
		if (search == null || search.isEmpty()) {
			assetsReadings = AssetsAPI.getAssetsWithReadings(buildingIds);
		}
		else {
			JSONObject pagination = new JSONObject();
	 		pagination.put("page", getPage());
	 		pagination.put("perPage", getPerPage());
			assetsReadings = AssetsAPI.getAssetsWithReadings(buildingIds, search, pagination);
		}
		return SUCCESS;
  	}
	
	private List<Long> buildingIds;
	public List<Long> getBuildingIds() {
		return buildingIds;
	}
	public void setBuildingIds(List<Long> buildingIds) {
		this.buildingIds = buildingIds;
	}
	
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private String location;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public String fetchAssetFromQR() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.QR_VALUE, value);
		context.put(FacilioConstants.ContextNames.LOCATION, location);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, ContextNames.ASSET_ACTIVITY);
		Chain getAssetChain = TransactionChainFactory.getAssetFromQRChain();
		getAssetChain.execute(context);
		setResult("asset", context.get(FacilioConstants.ContextNames.ASSET));
		
		return SUCCESS;
	}
	
	public String fetchActivity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, assetId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain assetActivity = ReadOnlyChainFactory.getActivitiesChain();
		assetActivity.execute(context);
		setResult("activity", context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String v2addAsset() throws Exception {
		addAsset();
		assetDetails();
		return SUCCESS;
	}
	
	public String v2updateAsset() throws Exception {
		updateAsset();
		setAssetId(asset.getId());
		assetDetails();
		return SUCCESS;
	}
	
	public String fetchModuleCards() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		ReadOnlyChainFactory.getAssetModuleReportCardChain().execute(context);
		setResult("cards", context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		return SUCCESS;
	}
	
	public String fetchAssetDowntimeMetrics() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		ReadOnlyChainFactory.getAssetDowntimeMetricsChain().execute(context);
		setResult("metrics", context.get(FacilioConstants.ContextNames.RESULT));
		return SUCCESS;
	}
	
	private long itemTypeId;
	private long toolTypeId;
	private long storeRoomId; 
	private List<Long> itemTypeIds;
	private List<Long> toolTypeIds;
	
	
	public List<Long> getItemTypeIds() {
		return itemTypeIds;
	}

	public void setItemTypeIds(List<Long> itemTypeIds) {
		this.itemTypeIds = itemTypeIds;
	}

	public List<Long> getToolTypeIds() {
		return toolTypeIds;
	}

	public void setToolTypeIds(List<Long> toolTypeIds) {
		this.toolTypeIds = toolTypeIds;
	}

	private int inventoryType;
	
	
	
	public int getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(int inventoryType) {
		this.inventoryType = inventoryType;
	}

	public long getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(long itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	public long getToolTypeId() {
		return toolTypeId;
	}

	public void setToolTypeId(long toolTypeId) {
		this.toolTypeId = toolTypeId;
	}

	public long getStoreRoomId() {
		return storeRoomId;
	}

	public void setStoreRoomId(long storeRoomId) {
		this.storeRoomId = storeRoomId;
	}

	public String fetchAssetForTypeStore() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, inventoryType);
		if(inventoryType == InventoryType.ITEM.getValue()) {
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypeId);
		}
		else if(inventoryType == InventoryType.TOOL.getValue()) {
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypeId);
		}
		context.put(FacilioConstants.ContextNames.STORE_ROOM_ID, storeRoomId);
		
		ReadOnlyChainFactory.getAssetForTypeAndStoreChain().execute(context);
		setResult("assets", context.get(FacilioConstants.ContextNames.ASSETS));
		return SUCCESS;
	}
	
	public String fetchAssetForType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, inventoryType);
		if(inventoryType == InventoryType.ITEM.getValue()) {
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypeIds);
		}
		else if(inventoryType == InventoryType.TOOL.getValue()) {
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypeIds);
		}
		
		ReadOnlyChainFactory.getAssetForTypeChain().execute(context);
		setResult("assets", context.get(FacilioConstants.ContextNames.ASSETS));
		return SUCCESS;
	}
}
