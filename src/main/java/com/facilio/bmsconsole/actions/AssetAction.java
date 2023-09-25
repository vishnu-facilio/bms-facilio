package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class AssetAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = LogManager.getLogger(WorkOrderAction.class.getName());

	public String newAsset() throws Exception {
		
		FacilioContext context = new FacilioContext();
		FacilioChain newAsset = FacilioChainFactory.getNewAssetChain();
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
	
	private Long approvalTransitionId = null;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}
	
 	
	public String generateQr() throws Exception {
		FacilioContext context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,id);
		FacilioChain updateQrChain = TransactionChainFactory.getUpdateQrChain();
		updateQrChain.execute(context);
		Map<Long,String> mappedqr =(Map<Long,String>) context.get(FacilioConstants.ContextNames.MAP_QR);
		setResult("mappedqr",mappedqr);
		return SUCCESS;
	}

	public String addAsset() throws Exception {
		try {
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
			if (asset.getSpace() == null || asset.getSpace().getId() < 0) {
				BaseSpaceContext assetLocation = new BaseSpaceContext();
				assetLocation.setId(asset.getSiteId());
				asset.setSpace(assetLocation);
			}
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
			FacilioChain addAssetChain = TransactionChainFactory.getAddAssetChain();
			addAssetChain.execute(context);
			setAssetId(asset.getId());
		}
		catch (Exception e) {
			if (FacilioProperties.isOnpremise() || getFetchStackTrace()) {
				String assetString = FieldUtil.getAsJSON(asset).toString();
				CommonCommandUtil.emailAlert("Asset Add Exception",  "Error occurred while adding asset: \n\n\n------" + ExceptionUtils.getStackTrace(e) + "----\n\n\n" + assetString.toString());
				LOGGER.error("Error occurred while adding asset: "+ assetString, e);
			}
			throw e;
		}
		
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
		try {
			
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		if (assetsId != null && assetsId.size() > 0) {
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, assetsId);
		}else {
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(asset.getId()));
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		// cannot update module state directly
		if (asset.getModuleState() != null) {
			asset.setModuleState(null);
		}
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		FacilioChain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();
		updateAssetChain.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		} catch (Exception e) {
			if (FacilioProperties.isOnpremise() || getFetchStackTrace()) {
				String assetString = FieldUtil.getAsJSON(asset).toString();
				CommonCommandUtil.emailAlert("Asset Update Exception",  "Error occurred while updating asset: \n\n\n------" + ExceptionUtils.getStackTrace(e) + "----\n\n\n" + assetString.toString());
				LOGGER.error("Error occurred while updating asset: "+ assetString, e);
			}
			throw e;
		}
		
		return SUCCESS;
	}
	
	
	
	public String deleteAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		AssetContext asset = new AssetContext();
		asset.setDeleted(true);
		
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, assetsId);
		
		FacilioChain deleteAssetChain = FacilioChainFactory.getDeleteAssetChain();
		deleteAssetChain.execute(context);
		setAssetsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	private String clientCriteria;
	
	public String getClientCriteria() {
		return clientCriteria;
	}

	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}

	Criteria clientFilterCriteria;
	
	
	public Criteria getClientFilterCriteria() {
		return clientFilterCriteria;
	}

	public void setClientFilterCriteria(Criteria clientFilterCriteria) {
		this.clientFilterCriteria = clientFilterCriteria;
	}

	public String assetList() throws Exception {
		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
 		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Assets.LOCAL_ID desc");
 		context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, clientFilterCriteria);
 		
 		if (getClientCriteria() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getClientCriteria());
			Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
			context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
		}
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		} else if(getFilterCriteria() != null) {	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilterCriteria());
	 		Criteria criteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
	 		context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
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
 		
 		if(getOverrideViewOrderBy() != null && getOverrideViewOrderBy() && getOrderBy() != null) {
 			JSONObject sorting = new JSONObject();
 			sorting.put("orderBy", getOrderBy());
 			sorting.put("orderType", getOrderType());
 			context.put(FacilioConstants.ContextNames.SORTING, sorting);
 			context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
 		}
 		
 		
 		context.put(FacilioConstants.ContextNames.READING_ID, readingId);
 		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
 		context.put(FacilioConstants.ContextNames.INPUT_TYPE, getInputType());
 		context.put(FacilioConstants.ContextNames.WITH_READINGS, this.getWithReadings());
 		context.put(FacilioConstants.ContextNames.WITH_WRITABLE_READINGS, this.getWithWritableReadings());
 		context.put(FacilioConstants.ContextNames.FETCH_CUSTOM_LOOKUPS, true);
 		context.put(FacilioConstants.ContextNames.FETCH_AS_MAP, getFetchPrimaryDetails());
 		FacilioChain assetList = FacilioChainFactory.getAssetListChain();
 		assetList.execute(context);
 		if (getCount()) {
			setAssetCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", assetCount);
		}
 		else {
 			if (getFetchPrimaryDetails()) {
 				setResult("assets", context.get(FacilioConstants.ContextNames.RECORD_LIST_MAP));
 			}
 			else {
 				assets= (List<AssetContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
 				// Temp...needs to handle in client
 				if (assets == null) {
 					assets = new ArrayList<>();
 				}
 				setResult("assets", assets);
 			}
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
		// added this to handle the fetch deleted records for assets, in WO summary page.
		context.put(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, true);
		AssetCategoryContext category= asset.getCategory();
		
		if (category != null && category.getId() != -1) {
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
		}
		context.put(FacilioConstants.ContextNames.SHOW_RELATIONS_COUNT, showRelationsCount);
		context.put(FacilioConstants.ContextNames.FETCH_HIERARCHY, getFetchHierarchy());
		FacilioChain assetDetailsChain = FacilioChainFactory.getAssetDetailsChain();
		assetDetailsChain.execute(context);
		
		setAsset((AssetContext) context.get(FacilioConstants.ContextNames.ASSET));
		setRelationsCount((Map<String, Long>) context.get(FacilioConstants.ContextNames.RELATIONS_COUNT));
		setResult(FacilioConstants.ContextNames.ASSET, getAsset());
		setResult("relationsCount", relationsCount);
		return SUCCESS;
	}
	public String get3dviewAssetDetails() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioField viewIdField = modBean.getField("3dviewid", module.getName());
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																.moduleName(module.getName())
																.beanClass(AssetContext.class)
																.select(modBean.getAllFields(module.getName()))
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCondition(viewIdField, viewId, StringOperators.IS));
		
		AssetContext asset = selectBuilder.fetchFirst();
		if(asset!=null){
			AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(asset.getCategory().getId());
			asset.getCategory().setDisplayName(assetCategory.getDisplayName());
			setResult("asset",asset);
			BuildingContext building = SpaceAPI.getBuildingSpace(asset.getCurrentSpaceId());
			setResult("currentBuilding",building);
			if(asset.getOperatingHour()>0){
				BusinessHoursContext businessHour = BusinessHoursAPI.getBusinessHours(Collections.singletonList(asset.getOperatingHour())).get(0);
				setResult("businessHour",businessHour);
			}
			List<WorkOrderContext> workorders = WorkOrderAPI.getOpenOverdueWorkOrdersByResourceId(asset.getId());
			setResult("workorders", workorders);
			List<AlarmContext> alarms = AlarmAPI.getAlarms(asset.getId());
			setResult("alarms", alarms);
			List<ReadingDataMeta> readings = ReadingsAPI.getConnectedLoggedReadings(asset.getId());
			setResult("readings", readings);
		}else{
			setResult("asset",null);
			setResult("workorders", new ArrayList<>());
			setResult("alarms", new ArrayList<>());
			setResult("readings", new ArrayList<>());
		}
		return SUCCESS;
	}
	
	private String viewId;
	
	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	private Boolean fetchHierarchy;
	public Boolean getFetchHierarchy() {
		if (fetchHierarchy == null) {
			return false;
		}
		return fetchHierarchy;
	}
	public void setFetchHierarchy(Boolean fetchHierarchy) {
		this.fetchHierarchy = fetchHierarchy;
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
	
	private List<Long> categoryIds;
	public List<Long> getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}

	private JSONObject reports;
	public JSONObject getReports() {
		return reports;
	}
	public void setReports(JSONObject reports) {
		this.reports = reports;
	}
	
	private String orderBy;
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	private String orderType;
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	private Boolean overrideViewOrderBy;

	public Boolean getOverrideViewOrderBy() {
		return overrideViewOrderBy;
	}

	public void setOverrideViewOrderBy(Boolean overrideViewOrderBy) {
		this.overrideViewOrderBy = overrideViewOrderBy;
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
		
		FacilioChain getReportCardsChain = FacilioChainFactory.getAssetReportCardsChain();
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
	
	private Boolean withWritableReadings;
	
	public Boolean getWithWritableReadings() {
		if (withWritableReadings == null) {
			return false;
		}
		return withWritableReadings;
	}

	public void setWithWritableReadings(Boolean withWritableReadings) {
		this.withWritableReadings = withWritableReadings;
	}
	
	private Boolean fetchPrimaryDetails;
	public boolean getFetchPrimaryDetails() {
		if(fetchPrimaryDetails == null) {
			fetchPrimaryDetails = false;
		}
		return fetchPrimaryDetails;
	}
	public void setFetchPrimaryDetails(boolean fetchPrimaryDetails) {
		this.fetchPrimaryDetails = fetchPrimaryDetails;
	}

	private String filterCriteria;
	

	JSONObject assetsReadings;
	public JSONObject getAssetsReadings() {
		return assetsReadings;
	}
	public void setAssetsReadings(JSONObject assetsReadings) {
		this.assetsReadings = assetsReadings;
	}
	
	JSONObject assetCategoriesWithReadings;
	public JSONObject getAssetCategoriesWithReadings() {
		return assetCategoriesWithReadings;
	}
	public void setAssetCategoriesWithReadings(JSONObject assetCategoriesWithReadings) {
		this.assetCategoriesWithReadings = assetCategoriesWithReadings;
	}
	
	public String getReadingsWithAssets() throws Exception {
		if (search == null || search.isEmpty()) {
			assetsReadings = AssetsAPI.getAssetsWithReadings(buildingIds, fieldsNotRequired != null && fieldsNotRequired);
		}
		else {
			JSONObject pagination = new JSONObject();
	 		pagination.put("page", getPage());
	 		pagination.put("perPage", getPerPage());
			assetsReadings = AssetsAPI.getAssetsWithReadings(buildingIds, search, pagination);
		}
		return SUCCESS;
  	}
	
	public String getAssetsWithReadings() throws Exception {
		JSONObject pagination = new JSONObject();
 		pagination.put("page", getPage());
 		pagination.put("perPage", getPerPage());
		assetsReadings = AssetsAPI.getAssetsWithReadings(buildingIds, categoryIds, assetIds, fieldIds, search, pagination, fetchOnlyAssets != null && fetchOnlyAssets, fetchOnlyReadings != null && fetchOnlyReadings, fetchOnlyAssetReadingMap != null && fetchOnlyAssetReadingMap, getCount(),fetchOnlyAlarmPoints != null && fetchOnlyAlarmPoints);
		return SUCCESS;
  	}
	
	public String getReadingsWithAssetsForSpecificCategories() throws Exception {
		assetsReadings = AssetsAPI.getAssetsWithReadingsForSpecificCategory(buildingIds, categoryIds, fetchOnlyAlarmPoints != null && fetchOnlyAlarmPoints, fetchOnlyKpiPoints!=null && fetchOnlyKpiPoints);
		return SUCCESS;
  	}
	
	public String getAssetCategoryWithReadings () throws Exception {
			assetCategoriesWithReadings = AssetsAPI.getAssetCategoryWithReadings(buildingIds, fetchOnlyAlarmPoints != null && fetchOnlyAlarmPoints, fetchOnlyKpiPoints!=null && fetchOnlyKpiPoints);
		return SUCCESS;
	}
	
	private Boolean fieldsNotRequired;
	public Boolean getFieldsNotRequired() {
		return fieldsNotRequired;
	}
	public void setFieldsNotRequired(Boolean fieldsNotRequired) {
		this.fieldsNotRequired = fieldsNotRequired;
	}
	
	private Boolean fetchOnlyAssets;
	public Boolean getFetchOnlyAssets() {
		return fetchOnlyAssets;
	}
	public void setFetchOnlyAssets(Boolean fetchOnlyAssets) {
		this.fetchOnlyAssets = fetchOnlyAssets;
	}
	
	private Boolean fetchOnlyReadings;
	public Boolean getFetchOnlyReadings() {
		return fetchOnlyReadings;
	}
	public void setFetchOnlyReadings(Boolean fetchOnlyReadings) {
		this.fetchOnlyReadings = fetchOnlyReadings;
	}
	
	private Boolean fetchOnlyAssetReadingMap;
	public Boolean getFetchOnlyAssetReadingMap() {
		return fetchOnlyAssetReadingMap;
	}
	public void setFetchOnlyAssetReadingMap(Boolean fetchOnlyAssetReadingMap) {
		this.fetchOnlyAssetReadingMap = fetchOnlyAssetReadingMap;
	}
	
	private Boolean fetchOnlyAlarmPoints;
	public Boolean getFetchOnlyAlarmPoints() {
		return fetchOnlyAlarmPoints;
	}
	public void setFetchOnlyAlarmPoints(Boolean fetchOnlyAlarmPoints) {
		this.fetchOnlyAlarmPoints = fetchOnlyAlarmPoints;
	}

	private Boolean fetchOnlyKpiPoints;
	public Boolean getFetchOnlyKpiPoints() {
		return fetchOnlyKpiPoints;
	}
	public void setFetchOnlyKpiPoints(Boolean fetchOnlyKpiPoints) {
		this.fetchOnlyKpiPoints = fetchOnlyKpiPoints;
	}

	private List<Long> assetIds;
	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}

	private List<Long> fieldIds;
	public List<Long> getFieldIds() {
		return fieldIds;
	}
	public void setFieldIds(List<Long> fieldIds) {
		this.fieldIds = fieldIds;
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
		context.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.ASSET);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSET_LOCATION_CHANGE);
		FacilioChain getAssetChain = TransactionChainFactory.getAssetFromQRChain();
		getAssetChain.execute(context);
		setResult("asset", context.get(FacilioConstants.ContextNames.ASSET));
		
		return SUCCESS;
	}
	
	public String fetchActivity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, assetId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		FacilioChain assetActivity = ReadOnlyChainFactory.getActivitiesChain();
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
//		assetDetails();
		return SUCCESS;
	}
	
	public String v2deleteAsset() throws Exception {
		deleteAsset();
		setResult("assetsId", assetsId);
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
	public String fetchAssetDowntimeHistory() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		ReadOnlyChainFactory.getAssetDowntimeHistoryChain().execute(context);
		setResult("history", context.get(FacilioConstants.ContextNames.RESULT));
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

	public String getFilterCriteria() {
		return filterCriteria;
	}

	public void setFilterCriteria(String filterCriteria) {
		this.filterCriteria = filterCriteria;
	}
	public String getAssetAssociatedActiveContracts() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getAssetId());
		FacilioChain chain =ReadOnlyChainFactory.getAssetAssociatedActiveContractsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.CONTRACTS, context.get(FacilioConstants.ContextNames.CONTRACTS));
		
		return SUCCESS;
	}
	
	public String checkAssetRunStatus() throws Exception {
		List<Map<String, Object>> runStatusFields = AssetsAPI.getRunStatusFields(assetId);
		setResult("runStatusAvailable", CollectionUtils.isNotEmpty(runStatusFields));
		return SUCCESS;
	}
	
	public String v2thirdParty3dViewAssetsList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		FacilioField thirdpartyidField = modBean.getField("thirdpartyid", module.getName());
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																.moduleName(module.getName())
																.beanClass(BuildingContext.class)
																.select(modBean.getAllFields(module.getName()))
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCondition(thirdpartyidField,String.valueOf(buildingThirdPartyId),NumberOperators.EQUALS));
		
		BuildingContext building = selectBuilder.fetchFirst();
		
		if(building != null){
			module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			thirdpartyidField = modBean.getField("thirdpartyid", module.getName());
			FacilioField viewidField = modBean.getField("3dviewid", module.getName());
			FacilioField currentSpaceIdField = modBean.getField("currentSpaceId", module.getName());
			
			SelectRecordsBuilder<AssetContext> selectBuilder1 = new SelectRecordsBuilder<AssetContext>()
																	.moduleName(module.getName())
																	.beanClass(AssetContext.class)
																	.select(modBean.getAllFields(module.getName()))
																	.table(module.getTableName())
																	.andCondition(CriteriaAPI.getCondition(thirdpartyidField,"NULL",CommonOperators.IS_NOT_EMPTY))
																	.andCondition(CriteriaAPI.getCondition(viewidField,"NULL",CommonOperators.IS_NOT_EMPTY))
																	.andCondition(CriteriaAPI.getCondition(currentSpaceIdField,String.valueOf(building.getId()),NumberOperators.EQUALS));
			
			List<AssetContext> assets = selectBuilder1.get();
			List<String> overdueWorkorderAsset3dViewIds = new ArrayList<>();
			List<String> alarmAsset3dViewIds = new ArrayList<>();
			for(AssetContext asset:assets){
				List<WorkOrderContext> workorders = WorkOrderAPI.getOpenOverdueWorkOrdersByResourceId(asset.getId());
				if(!workorders.isEmpty()){
					overdueWorkorderAsset3dViewIds.add(asset.getData().get("3dviewid").toString());
				}
				
				List<AlarmContext> alarms = AlarmAPI.getAlarms(asset.getId());
				if(!alarms.isEmpty()){
					alarmAsset3dViewIds.add(asset.getData().get("3dviewid").toString());
				}
			}
			
			setResult(FacilioConstants.ContextNames.ASSET_LIST, assets);
			setResult("overdueWorkorderAsset3dViewIds", overdueWorkorderAsset3dViewIds);
			setResult("alarmAsset3dViewIds", alarmAsset3dViewIds);
		}else{
			setResult(FacilioConstants.ContextNames.ASSET_LIST, null);
		}
		return SUCCESS;
	}
	
	long buildingThirdPartyId;

	public long getBuildingThirdPartyId() {
		return buildingThirdPartyId;
	}

	public void setBuildingThirdPartyId(long buildingThirdPartyId) {
		this.buildingThirdPartyId = buildingThirdPartyId;
	}
	
}

