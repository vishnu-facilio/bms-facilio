package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepartmentContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AssetsAPI {
	
	private static Logger logger = Logger.getLogger(AssetsAPI.class.getName());
	
	public static List<Long> getAssetIdsFromBaseSpaceIds(List<Long> baseSpaceIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		FacilioField spaceField= FieldFactory.getAsMap(fields).get("spaceId");
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(BaseSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(spaceField, StringUtils.join(baseSpaceIds, ","), PickListOperators.IS))
				.andCustomWhere("Resources.RESOURCE_TYPE = ?", ResourceContext.ResourceType.ASSET.getValue());
		
		List<BaseSpaceContext> assets = selectBuilder.get();
		List<Long> assetIds = null; 
		for(BaseSpaceContext asset :assets) {
			if(assetIds == null) {
				assetIds = new ArrayList<>();
			}
			assetIds.add(asset.getId());
		}
		return assetIds;
	}
	
	public static long getOrgId(Long assetId) throws Exception
	{
		List<FacilioField> fields = new ArrayList<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioField orgField = FieldFactory.getOrgIdField(module);
		fields.add(orgField);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCustomWhere("ID = ?", assetId);
		
		List<Map<String, Object>> assets = selectBuilder.get();
		
		if(assets != null && !assets.isEmpty()) {
			return (long) assets.get(0).get(orgField.getName());
		}
		return -1;
	}
	
	public static AssetContext getAssetInfo(long assetId) throws Exception
	{
		return getAssetInfo(assetId, false);
	}
	
	public static AssetContext getAssetInfo(long assetId, boolean fetchDeleted) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																.moduleName(module.getName())
																.beanClass(AssetContext.class)
																.select(modBean.getAllFields(module.getName()))
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getIdCondition(assetId, module));
		
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		
		List<AssetContext> assets = selectBuilder.get();
		if(assets != null && !assets.isEmpty()) {
			return assets.get(0);
		}
		return null;
	}
	
	public static List<Long> getAssetCategoryIds(Long baseSpaceID) throws Exception {
		
		return getAssetCategoryIds(Collections.singletonList(baseSpaceID));
	}
	
	public static List<Long> getAssetCategoryIds(List<Long> baseSpaceIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		
		List<FacilioField> selectFields = new ArrayList<>();
		
		
		FacilioField spaceIdField = modBean.getField("space", assetModule.getName());
		
		FacilioField categoryField = modBean.getField("category", assetModule.getName());
		FacilioField selectField = new FacilioField();
		selectField.setName(categoryField.getName());
		selectField.setDisplayName(categoryField.getDisplayName());
		selectField.setColumnName("DISTINCT("+categoryField.getColumnName()+")");
		
		selectFields.add(selectField);
		
		GenericSelectRecordBuilder newSelectBuilder = new GenericSelectRecordBuilder()
				.table(assetModule.getTableName())
				.innerJoin(resourceModule.getTableName())
				.on(assetModule.getTableName()+".ID = "+resourceModule.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(spaceIdField, baseSpaceIds, NumberOperators.EQUALS))	// change to buildingIs if nedded
				.select(selectFields);
		
		 List<Map<String, Object>> props = newSelectBuilder.get();
		
		List<Long> categoryIds = new ArrayList<>();
		if(props != null) {
			for(Map<String, Object> prop :props) {
				categoryIds.add((Long)prop.get(selectField.getName()));
			}
		}
		
		return categoryIds; 
	}
	
	public static List<Long> getSubCategoryIds(Long assetCategoryId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
		
		FacilioField parentCategoryField = modBean.getField("parentCategoryId", assetModule.getName());
		
		SelectRecordsBuilder<AssetCategoryContext> newSelectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.module(assetModule)
				.andCondition(CriteriaAPI.getCondition(parentCategoryField, Collections.singletonList(assetCategoryId), NumberOperators.EQUALS))	// change to buildingIs if nedded
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
		
		 Map<Long, AssetCategoryContext> props = newSelectBuilder.getAsMap();
		
		if(props != null) {
			return new ArrayList<>(props.keySet());
		}
		return null;
	}
	
	public static long getAssetId(String name, Long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.moduleName(module.getName())
				.beanClass(AssetContext.class)
				.select(modBean.getAllFields(module.getName()))
				.table(module.getTableName())
				.andCustomWhere("NAME = ?", name);
		List<AssetContext> assets = selectBuilder.get();
		if(assets != null && !assets.isEmpty()) {
			return assets.get(0).getId();
		}
		return -1;
	}
	
	public static Long addAsset(String name, Long orgId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long assetId = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Assets (NAME, ORGID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setLong(2, orgId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				assetId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding asset" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return assetId;
	}
	
	public static Long addAsset(String name, Long orgId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long assetId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO Assets (NAME, ORGID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setLong(2, orgId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				assetId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding asset" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return assetId;
	}
	
	public static AssetCategoryContext getCategory(String name) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
																		.beanClass(AssetCategoryContext.class)
																		.andCustomWhere("NAME = ?", name);
		
		List<AssetCategoryContext> categories = selectBuilder.get();
		
		if(categories != null && !categories.isEmpty()) {
			return categories.get(0);
		}
		return null;
	}
	
	public static List<AssetCategoryContext> getCategoryList() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
																		.beanClass(AssetCategoryContext.class);
		return selectBuilder.get();
	}
	
	public static List<AssetTypeContext> getTypeList() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetTypeContext> selectBuilder = new SelectRecordsBuilder<AssetTypeContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_TYPE))
																		.moduleName(FacilioConstants.ContextNames.ASSET_TYPE)
																		.beanClass(AssetTypeContext.class);
		return selectBuilder.get();
	}
	
	public static Map<Long, AssetTypeContext> getAssetType(Collection<Long> typeIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetTypeContext> selectBuilder = new SelectRecordsBuilder<AssetTypeContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_TYPE))
																		.moduleName(FacilioConstants.ContextNames.ASSET_TYPE)
																		.beanClass(AssetTypeContext.class);
		return selectBuilder.getAsMap();
	}
	
	public static List<AssetDepartmentContext> getDepartmentList() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetDepartmentContext> selectBuilder = new SelectRecordsBuilder<AssetDepartmentContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPARTMENT))
																		.moduleName(FacilioConstants.ContextNames.ASSET_DEPARTMENT)
																		.beanClass(AssetDepartmentContext.class);
		return selectBuilder.get();
	}
	
	public static List<AssetContext> getAssetListOfCategory(long category) throws Exception
	{
		return getAssetListOfCategory(category,-1);
	}
	
	public static List<AssetContext> getAssetListOfCategory(long category,long buildingId) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		FacilioField categoryField= FieldFactory.getAsMap(fields).get("category");
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition(categoryField, String.valueOf(category), PickListOperators.IS));
		
		if(buildingId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", ""+buildingId, BuildingOperator.BUILDING_IS));
		}
		List<AssetContext> assets = selectBuilder.get();
		return assets;
		
	}
	public static List<AssetContext> getAssetListOfCategoryInParticularSpace(long category,long baseSpaceID) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		FacilioField categoryField= FieldFactory.getAsMap(fields).get("category");
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition(categoryField, String.valueOf(category), PickListOperators.IS));
		
		if(baseSpaceID > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", ""+baseSpaceID, PickListOperators.IS));
		}
		List<AssetContext> assets = selectBuilder.get();
		return assets;
		
	}
	
	public static AssetCategoryContext getCategoryForAsset(long categoryId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
																		.beanClass(AssetCategoryContext.class)
																		.andCondition(CriteriaAPI.getIdCondition(categoryId, modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY)));
		List<AssetCategoryContext> assetList = selectBuilder.get();
		if (assetList != null && !assetList.isEmpty()) {
			return assetList.get(0);
		}
		return null;
	}
	
	public static JSONObject getAssetsWithReadings (List<Long> buildingIds) throws Exception {
		Map<Long, FacilioField> fieldMap = null;
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		FacilioModule readingsModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> redingFields = FieldFactory.getReadingDataMetaFields();
		fields.addAll(redingFields);
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(redingFields);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(AssetContext.class)
				.innerJoin(readingsModule.getTableName())
				.on(readingsModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY));

		if (buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "spaceId", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		
		List<AssetContext> assets = selectBuilder.get();
		
		if (assets == null || assets.isEmpty()) {
			return null;
		}
		Set<Long> fieldIds = assets.stream().map(asset -> (Long) asset.getData().get("fieldId")).collect(Collectors.toSet());
		fieldMap = modBean.getFields(fieldIds);
		
		Map<Long, Object> assetMap = new JSONObject();
		
		JSONObject categoryVsFields = new JSONObject();
		JSONObject categoryVsAssets = new JSONObject();
		for(AssetContext asset: assets) {
			if (asset.getCategory() == null) {
				continue;
			}
			
			Long fieldId = (Long) asset.getData().get("fieldId");
			
			List<Long> assetFieldIds;
			Map<Long, List<Long>> categoryAssets;
			if (!categoryVsAssets.containsKey(asset.getCategory().getId())) {
				categoryAssets = new HashMap<>();
				categoryVsAssets.put(asset.getCategory().getId(), categoryAssets);
			}
			else {
				categoryAssets = (Map<Long, List<Long>>) categoryVsAssets.get(asset.getCategory().getId());
			}
			if (!categoryAssets.containsKey(asset.getId())) {
				assetFieldIds = new ArrayList<>();
				categoryAssets.put(asset.getId(), assetFieldIds);
				assetMap.put(asset.getId(), asset.getName());
			}
			else {
				assetFieldIds = categoryAssets.get(asset.getId());
			}
			assetFieldIds.add(fieldId);
			categoryVsAssets.put(asset.getCategory().getId(), categoryAssets);
			
			
			Set<Long> assetIds;
			Map<Long, Set<Long>> readings;
			if (!categoryVsFields.containsKey(asset.getCategory().getId())) {
				readings = new HashMap<>();
				categoryVsFields.put(asset.getCategory().getId(), readings);
			}
			else {
				readings = (Map<Long, Set<Long>>) categoryVsFields.get(asset.getCategory().getId());
			}
			if (!readings.containsKey(fieldId)) {
				assetIds = new HashSet<Long>();
				readings.put(fieldId, assetIds);
			}
			else {
				assetIds = readings.get(fieldId);
			}
			assetIds.add(asset.getId());
			categoryVsFields.put(asset.getCategory().getId(), readings);
			
		}
		
		JSONObject data = new JSONObject();
		data.put("categoryWithFields", categoryVsFields);
		data.put("categoryWithAssets", categoryVsAssets);
		data.put("assets", assetMap);
		data.put("fields", fieldMap);
		
		
		return data;
	}
	
	public static JSONObject getAssetsWithReadings(List<Long> buildingIds, String searchText, JSONObject pagination) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioModule resourcesModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		fields.add(FieldFactory.getIdField(module));
		FacilioField assetNameField = assetFieldMap.get("name");
		fields.add(FieldFactory.getField("assetName", assetNameField.getColumnName(), resourcesModule, assetNameField.getDataTypeEnum()));
		fields.add(assetFieldMap.get("category"));
		
		FacilioModule readingFieldsModule = ModuleFactory.getFieldsModule();
		List<FacilioField> readingFields = FieldFactory.getSelectFieldFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);
		fields.addAll(readingFields);
		
		FacilioModule readingsMetaModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> redingMetaFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingMetaFieldsMap = FieldFactory.getAsMap(redingMetaFields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.innerJoin(resourcesModule.getTableName())
				.on(module.getTableName()+".ID"+"="+resourcesModule.getTableName()+".ID")
				.innerJoin(readingsMetaModule.getTableName())
				.on(readingsMetaModule.getTableName()+"."+readingMetaFieldsMap.get("resourceId").getColumnName()+"="+module.getTableName()+".ID")
				.innerJoin(readingFieldsModule.getTableName())
				.on(readingFieldsModule.getTableName()+"." + readingFieldsMap.get("fieldId").getColumnName()+"="+ readingsMetaModule.getTableName()+"."+readingMetaFieldsMap.get("fieldId").getColumnName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(readingMetaFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY));
				

		if (buildingIds != null && !buildingIds.isEmpty()) {
			builder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "spaceId", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		
		if (searchText != null && !searchText.isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(assetFieldMap.get("name"), searchText, StringOperators.CONTAINS));
			criteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("displayName"), searchText, StringOperators.CONTAINS));
			builder.andCriteria(criteria);
		}
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			if (perPage != -1) {
				int offset = ((page-1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				builder.offset(offset);
				builder.limit(perPage);
			}
		}
		
		List<Map<String, Object>> list = builder.get();
		if (list == null || list.isEmpty()) {
			return null;
		}
		
		Map<Long, Object> assetMap = new HashMap<Long, Object>();
		Map<Long, FacilioField> fieldMap = new HashMap<Long, FacilioField>();
		JSONObject assetVsReading = new JSONObject();
		Map<Long, List<Long>> assetsAndReadings = new HashMap<>();
		
		for(Map<String, Object> item: list) {
			long assetId = (long) item.get("id");
			if (!assetMap.containsKey(assetId)) {
				AssetContext asset = new AssetContext();
				asset.setId((long) item.get("id"));
				asset.setName((String) item.get("assetName"));
				AssetCategoryContext category = new AssetCategoryContext();
				category.setId(((long) item.get("category")));
				asset.setCategory(category);
				assetMap.put(assetId, asset);
			}
			
			long fieldId = (long) item.get("fieldId");
			if (!fieldMap.containsKey(fieldId)) {
				FacilioField field = FieldUtil.getAsBeanFromMap(item, FacilioField.class);
				fieldMap.put(fieldId, field);
			}
			
			if (!assetVsReading.containsKey(assetId) && assetVsReading.containsValue(fieldId)) {
				assetVsReading.put(assetId, fieldId);
			}
			
			List<Long> readings;
			if (!assetsAndReadings.containsKey(assetId)) {
				readings = new ArrayList<Long>();
				assetsAndReadings.put(assetId, readings);
			}
			else {
				readings = assetsAndReadings.get(assetId);
			}
			if (!readings.contains(fieldId)) {
				readings.add(fieldId);
			}
		}
		
		
		JSONObject data = new JSONObject();
		data.put("assetVsReadings", assetsAndReadings);
		data.put("readings", fieldMap);
		data.put("assets", assetMap);
		return data;
	}
	
	public static Map<String,String> getAssetModuleName(Long categoryId) throws Exception{
		Map<String,String> moduleInfo = new HashMap<String,String>();
		if (categoryId == null || categoryId <= 0)
		{	
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
		}
		else 
		{
			AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
			String assetCategoryName = assetCategory.getName();
			if(assetCategoryName.trim().equalsIgnoreCase("Energy Meter"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ENERGY_METER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Energy_Meter");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Water Meter"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WATER_METER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Water_Meter");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Chiller"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Primary Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_PRIMARY_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Primary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Secondary Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_SECONDARY_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Secondary_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Condenser Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CHILLER_CONDENSER_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Chiller_Condenser_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("AHU"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.AHU);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "AHU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Cooling Tower"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.COOLING_TOWER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Cooling_Tower");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("FCU"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.FCU);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "FCU");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Heat Pump"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.HEAT_PUMP);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Heat_Pump");
			}
			else if(assetCategoryName.trim().equalsIgnoreCase("Utility Meter"))
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.UTILITY_METER);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Utility_Meters");
			}
			else
			{
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Assets");
			}
		}
		return moduleInfo;
	}
	
}
