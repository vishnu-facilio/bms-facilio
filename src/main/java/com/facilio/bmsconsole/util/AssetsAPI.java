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

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.db.criteria.operators.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.context.AssetDepartmentContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class AssetsAPI {
	
	private static Logger logger = Logger.getLogger(AssetsAPI.class.getName());

	public static boolean isAssetsModule (FacilioModule module) {
		return module.instanceOf(FacilioConstants.ContextNames.ASSET);
	}
	
	public static List<Long> getAssetIdsFromBaseSpaceIds(List<Long> baseSpaceIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		FacilioField spaceField= FieldFactory.getAsMap(fields).get("space");
		
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
	
	public static Map<Long, PhotosContext> getAssetPhotoId(List<Long> assetIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_PHOTOS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_PHOTOS);
			Condition idCondition = new Condition();
			idCondition.setField(modBean.getField("parentId", module.getName()));
			idCondition.setOperator(PickListOperators.IS);
			idCondition.setValue(StringUtils.join(assetIds, ","));
			
			SelectRecordsBuilder<PhotosContext> selectBuilder = new SelectRecordsBuilder<PhotosContext>()
																		.moduleName(module.getName())
																		.beanClass(PhotosContext.class)
																		.select(fields)
																		.table(module.getTableName())
																		.andCondition(idCondition);	
			List<PhotosContext> photos = selectBuilder.get();
			Map<Long, PhotosContext> photoMap = new HashMap<Long, PhotosContext>();
			for (PhotosContext photoContext : photos) {
				photoMap.put(photoContext.getParentId(), photoContext);
			}
		return photoMap;
		
	}

	
	public static List<Long> getAssetIdsFromBaseSpaceIdsWithCategory(List<Long> baseSpaceIds,List<Long> categoryIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioField spaceField= fieldMap.get("space");
		FacilioField categoryField= fieldMap.get("category");
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(BaseSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(spaceField, StringUtils.join(baseSpaceIds, ","), PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(categoryField, StringUtils.join(categoryIds, ","), PickListOperators.IS))
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
		FacilioField orgField = AccountConstants.getOrgIdField(module);
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

	public static V3AssetContext getAssetInfoForAccessibleSite(long assetId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

		List<Long> ids = AccountUtil.getUserBean().getAccessibleSpaceList(AccountUtil.getCurrentUser().getOuid());

		SelectRecordsBuilder<V3AssetContext> selectBuilder = new SelectRecordsBuilder<V3AssetContext>()
				.moduleName(module.getName())
				.beanClass(V3AssetContext.class)
				.select(modBean.getAllFields(module.getName()))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(assetId, module));

		if (CollectionUtils.isNotEmpty(ids)){
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), StringUtils.join(ids, ","), BuildingOperator.BUILDING_IS));
		}


		List<V3AssetContext> assets = selectBuilder.get();
		if(assets != null && !assets.isEmpty()) {
			return assets.get(0);
		}
		return null;
	}
	
	public static List<AssetContext> getAssetInfo(List<Long> assetIds) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																.moduleName(module.getName())
																.beanClass(AssetContext.class)
																.select(modBean.getAllFields(module.getName()))
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getIdCondition(assetIds, module));
		
		List<AssetContext> assets = selectBuilder.get();
		return assets;
	}
	
	public static List<Long> getAssetCategoryIds(long baseSpaceID, Long buildingId, boolean fetchChildSpaces) throws Exception {
		
		return getAssetCategoryIds(Collections.singletonList(baseSpaceID), buildingId, fetchChildSpaces);
	}
	
	public static List<Long> getAssetCategoryIds(List<Long> baseSpaceIDs, Long buildingId, boolean fetchChildSpaces) throws Exception {
		if (fetchChildSpaces) {
			List<BaseSpaceContext> spaces = SpaceAPI.getBaseSpaceWithChildren(baseSpaceIDs);
			List<BaseSpaceContext> allSpaces = new ArrayList<>();
			if (buildingId == null || buildingId < 0) {
				allSpaces = spaces;
			} else if (spaces != null) {
				for (BaseSpaceContext space: spaces) {
					if (space.getBuildingId() == buildingId) {
						allSpaces.add(space);
					}
				}
			}
			
			if (allSpaces != null && !allSpaces.isEmpty()) {
				return getAssetCategoryIds(allSpaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList()));
			}
			return Collections.emptyList();
		}
		else {
			return getAssetCategoryIds(baseSpaceIDs);
		}
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
		selectField.setColumnName("DISTINCT(" + categoryField.getColumnName() + ")");
		selectField.setDataType(categoryField.getDataTypeEnum());

		selectFields.add(selectField);

		GenericSelectRecordBuilder newSelectBuilder = new GenericSelectRecordBuilder()
				.table(assetModule.getTableName())
				.innerJoin(resourceModule.getTableName())
				.on(assetModule.getTableName() + ".ID = " + resourceModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getCondition(spaceIdField, baseSpaceIds, NumberOperators.EQUALS))    // change to buildingIs if nedded
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "isDeleted", Boolean.FALSE.toString(), BooleanOperators.IS))
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
	
	public static List<AssetCategoryContext> getSubCategory(Long assetCategoryId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
		
		FacilioField parentCategoryField = modBean.getField("parentCategoryId", assetModule.getName());
		
		SelectRecordsBuilder<AssetCategoryContext> newSelectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.module(assetModule)
				.beanClass(AssetCategoryContext.class)
				.andCondition(CriteriaAPI.getCondition(parentCategoryField, Collections.singletonList(assetCategoryId), NumberOperators.EQUALS))	// change to buildingIs if nedded
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
		
		 List<AssetCategoryContext> props = newSelectBuilder.get();
		
		return props;
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
	
	public static long getAssetId(String name,String thirdpartyid) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		FacilioField thirdpartyidField= FieldFactory.getAsMap(fields).get("thirdpartyid");
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.moduleName(module.getName())
				.beanClass(AssetContext.class)
				.select(fields)
				.table(module.getTableName())
				.andCustomWhere("NAME = ?", name)
				.andCondition(CriteriaAPI.getCondition(thirdpartyidField, thirdpartyid, StringOperators.IS));
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

	public static AssetCategoryContext getCategoryByDisplayName(String name) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
				.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
				.beanClass(AssetCategoryContext.class)
				.skipModuleCriteria()
				.andCustomWhere("DISPLAY_NAME = ?", name);

		List<AssetCategoryContext> categories = selectBuilder.get();

		if(categories != null && !categories.isEmpty()) {
			return categories.get(0);
		}
		return null;
	}
	
	public static AssetCategoryContext getCategory(String name) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
																		.beanClass(AssetCategoryContext.class)
																		.skipModuleCriteria()
																		.andCustomWhere("NAME = ?", name);
		
		List<AssetCategoryContext> categories = selectBuilder.get();
		
		if(categories != null && !categories.isEmpty()) {
			return categories.get(0);
		}
		return null;
	}

	public static AssetCategoryContext getCategoryByAssetModule(long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
																		.beanClass(AssetCategoryContext.class)
																		.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetModuleID"), String.valueOf(moduleId) ,NumberOperators.EQUALS));

		List<AssetCategoryContext> categories = selectBuilder.get();

		if(categories != null && !categories.isEmpty()) {
			return categories.get(0);
		}
		return null;
	}

	public static V3AssetCategoryContext getCategoryByAssetModuleV3(long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
		SelectRecordsBuilder<V3AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
				.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
				.beanClass(V3AssetCategoryContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetModuleID"), String.valueOf(moduleId) ,NumberOperators.EQUALS));

		List<V3AssetCategoryContext> categories = selectBuilder.get();

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
	
	public static List<AssetContext> getAssetListOfCategory(long category,long buildingId) throws Exception {
		List<Long> buildingIds = null;
		if(buildingId > 0) {
			buildingIds = Collections.singletonList(buildingId);
		}
		return getAssetListOfCategory(category, buildingIds);
	}
	
	public static List<AssetContext> getAssetListOfCategory(long category,List<Long> buildingIds) throws Exception {
		return getAssetListOfCategory(category, buildingIds, -1);
	}
	
	public static List<AssetContext> getAssetListOfCategory(long category,List<Long> buildingIds, long siteId) throws Exception
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
		
		if(buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), PickListOperators.IS));
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
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
																		.module(module)
																		.beanClass(AssetCategoryContext.class)
																		.skipModuleCriteria()
																		.andCondition(CriteriaAPI.getIdCondition(categoryId, module));
		List<AssetCategoryContext> assetList = selectBuilder.get();
		if (assetList != null && !assetList.isEmpty()) {
			return assetList.get(0);
		}
		return null;
	}
	
	public static JSONObject getAssetsWithReadings (List<Long> buildingIds, boolean fieldsNotRequired) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(assetModule));
		fields.add(assetFieldMap.get("name"));
		fields.add(assetFieldMap.get("category"));
		
		FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);
		fields.add(readingFieldsMap.get("fieldId"));
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.table(assetModule.getTableName())
				.moduleName(assetModule.getName())
				.beanClass(AssetContext.class)
				.innerJoin(readingDataMetaModule.getTableName())
				.on(readingDataMetaModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+assetModule.getTableName()+".ID")
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
				.limit(30000);	// Temp

		if (buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		
		List<AssetContext> assets = selectBuilder.get();
		
		JSONObject data = new JSONObject();

		JSONObject categoryVsFields = new JSONObject();
		data.put("categoryWithFields", categoryVsFields);

		JSONObject categoryVsAssets = new JSONObject();
		data.put("categoryWithAssets", categoryVsAssets);

		Map<Long, Object> assetMap = new JSONObject();
		data.put("assets", assetMap);

		Map<Long, Map<String,Object>> fieldMap = null;
		data.put("fields", fieldMap);

		if (assets == null || assets.isEmpty()) {
			return data;
		}
		Set<Long> fieldIds = assets.stream().map(asset -> (Long) asset.getData().get("fieldId")).collect(Collectors.toSet());
		if (!fieldsNotRequired) {
			List<FacilioField> fieldDetailList = modBean.getFields(fieldIds);
			fieldMap =new HashMap<>();
			for(FacilioField field: fieldDetailList) {
				Map<String, Object> details = getFieldDefaultProp(field);
				fieldMap.put(field.getFieldId(), details);
			}
		}
		
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

		data.put("categoryWithFields", categoryVsFields);
		data.put("categoryWithAssets", categoryVsAssets);
		data.put("assets", assetMap);
		data.put("fields", fieldMap);
		
		
		return data;
	}
	
	private static Map<String, Object> getFieldDefaultProp(FacilioField field) {
		Map<String, Object> details =new HashMap<>();
		details.put("name", field.getName());
		details.put("displayName", field.getDisplayName());
		details.put("fieldId", field.getFieldId());
		details.put("id", field.getFieldId());
		details.put("dataType", field.getDataType());
		details.put("default", field.getDefault());
		details.put("module", Collections.singletonMap("type", field.getModule().getType()));
		if (field instanceof NumberField) {
			NumberField numberField = (NumberField)field;
			details.put("unit", numberField.getUnit());
		}
		return details;
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
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(readingMetaFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY));
				

		if (buildingIds != null && !buildingIds.isEmpty()) {
			builder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
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
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (categoryId == null || categoryId <= 0)
		{	
			FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, assetModule.getName());
			moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, assetModule.getTableName());
		}
		else 
		{
			AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(categoryId);
			long assetModuleID = assetCategory.getAssetModuleID();
			FacilioModule module = modBean.getModule(assetModuleID);

			if (module != null) {
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, module.getTableName());
			} else {
				FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_NAME, assetModule.getName());
				moduleInfo.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, assetModule.getTableName());
			}
		}
		return moduleInfo;
	}
	
	public static List<AssetContext> getAssetForItemTypeAndStore(long itemTypeId, long storeroomId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ItemContext rotatingItem = ItemsApi.getItemsForTypeAndStore(storeroomId, itemTypeId);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition("ROTATING_ITEM", "rotatingItem", String.valueOf(rotatingItem.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_USED", "isUsed", String.valueOf("false"), BooleanOperators.IS))
				;
		List<AssetContext> assets = selectBuilder.get();
		if(!CollectionUtils.isEmpty(assets)) {
			return assets;
		}
	   throw new IllegalArgumentException("No appropriate asset found");


	}

	public static List<AssetContext> getAssetForToolTypeAndStore(long toolTypeId, long storeroomId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ToolContext rotatingTool = ToolsApi.getToolsForTypeAndStore(storeroomId, toolTypeId);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition("ROTATING_TOOL", "rotatingTool", String.valueOf(rotatingTool.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_USED", "isUsed", String.valueOf("false"), BooleanOperators.IS))

				;
		List<AssetContext> assets = selectBuilder.get();
		if(!CollectionUtils.isEmpty(assets)) {
			return assets;
		}
	   throw new IllegalArgumentException("No appropriate asset found");


	}

	public static void loadAssetsLookups(List<AssetContext> assets) throws Exception {
		Set<Long> spaceIds = new HashSet<Long>();
		Set<Long> typeIds = new HashSet<Long>();
		List<Long> assetIds = new ArrayList<Long>();
		for(AssetContext asset: assets) {
			assetIds.add(asset.getId());
			if (asset.getSpaceId() > 0) {
				spaceIds.add(asset.getSpaceId());
			}
			if (assets.size() == 1 && asset instanceof EnergyMeterContext) {
				BaseSpaceContext purposeSpace = ((EnergyMeterContext)asset).getPurposeSpace();
				if (purposeSpace != null && purposeSpace.getId() > 0) {
					spaceIds.add(purposeSpace.getId());
				}
			}
			if (asset.getType() != null && asset.getType().getId() > 0) {
				typeIds.add(asset.getType().getId());
			}
		}
		Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(spaceIds);
		Map<Long, AssetTypeContext> assetTypeMap = AssetsAPI.getAssetType(typeIds);
		Map<Long, PhotosContext> assetPhotoMap = AssetsAPI.getAssetPhotoId(assetIds);
		for(AssetContext asset: assets) {
			PhotosContext phot = assetPhotoMap.get(asset.getId());
			if (phot != null) {
				asset.setPhotoId(phot.getPhotoId());
			}
			if(asset.getSpaceId() != -1) {
				asset.setSpace(spaceMap.get(asset.getSpaceId()));
			}
			if (assets.size() == 1 && asset instanceof EnergyMeterContext) {
				BaseSpaceContext purposeSpace = ((EnergyMeterContext)asset).getPurposeSpace();
				if (purposeSpace != null && purposeSpace.getId() > 0) {
					((EnergyMeterContext)asset).setPurposeSpace(spaceMap.get(purposeSpace.getId()));
				}
			}
			if (asset.getType() != null && asset.getType().getId() > 0) {
				asset.setType(assetTypeMap.get(asset.getType().getId()));
			}
			if(asset.getRotatingItem()!=null && asset.getRotatingItem().getId()>0) {
				asset.setRotatingItem(ItemsApi.getItems(asset.getRotatingItem().getId()));
			}
			if(asset.getRotatingTool()!=null && asset.getRotatingTool().getId() > 0) {
				asset.setRotatingTool(ToolsApi.getTool(asset.getRotatingTool().getId()));
			}
		
		}
	}

	public static void changeAssetSpace(long id, Map<String, Object> prop) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		FacilioField isDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
		FacilioField siteField = FieldFactory.getSiteIdField(module.getParentModule());
		List<FacilioField> modFields = new ArrayList<FacilioField>();
		modFields.addAll(fields);
		modFields.add(isDeletedField);
		modFields.add(siteField);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
			.table(module.getTableName())
			.fields(modFields)
			.innerJoin(resourceModule.getTableName())
			.on(module.getTableName()+".ID = "+resourceModule.getTableName()+".ID")
//			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
			.andCondition(CriteriaAPI.getIdCondition(id, module))
		;
		builder.update(prop);

	}

	public static int updateAssetConnectionStatus(Collection<Long> assetIds, boolean isConnected) throws Exception {

		AssetContext asset = new AssetContext();
		asset.setConnected(isConnected);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(assetIds, module));

		return updateBuilder.update(asset);

	}


   public static List<AssetContext> getAssetForItemType(List<Long> itemTypeIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ItemContext> rotatingItems = ItemsApi.getItemsForType(itemTypeIds);
		Map<Long,ItemContext> items = FieldUtil.getAsMap(rotatingItems);
		String id = StringUtils.join(items.keySet(), ",");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition("ROTATING_ITEM", "rotatingItem", id, NumberOperators.EQUALS))
				;
		List<AssetContext> assets = selectBuilder.get();
		if(!CollectionUtils.isEmpty(assets)) {
			return assets;
		}
	   throw new IllegalArgumentException("No appropriate asset found");


	}

   public static List<AssetContext> getAssetForToolType(List<Long> toolTypeIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ToolContext> rotatingTools = ToolsApi.getToolsForType(toolTypeIds);
		Map<Long,ToolContext> tools = FieldUtil.getAsMap(rotatingTools);
		String id = StringUtils.join(tools.keySet(), ",");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition("ROTATING_TOOL", "rotatingTool", id, NumberOperators.EQUALS))
				;
		List<AssetContext> assets = selectBuilder.get();
		if(!CollectionUtils.isEmpty(assets)) {
			return assets;
		}
	   throw new IllegalArgumentException("No appropriate asset found");


	}

   public static void updateAssetMovement(long movementId) throws Exception {
	   
	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		SelectRecordsBuilder<AssetMovementContext> selectBuilder = new SelectRecordsBuilder<AssetMovementContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetMovementContext.class)
				.andCondition(CriteriaAPI.getIdCondition(movementId, module));
				;
		AssetMovementContext assetMovementRecord = selectBuilder.fetchFirst();
	
		
		if(assetMovementRecord != null) {
			AssetContext asset = new AssetContext();
			if(assetMovementRecord.getToSite() > 0 && assetMovementRecord.getToSpace() > 0) {
				SiteContext newsite = SpaceAPI.getSiteSpace(assetMovementRecord.getToSite());
				asset.setIdentifiedLocation(newsite);
				asset.setCurrentSpaceId(assetMovementRecord.getToSpace());
				updateAsset(asset, assetMovementRecord.getAssetId());
			}
			else if(StringUtils.isNotEmpty(assetMovementRecord.getToGeoLocation())) {
				AssetContext assetContext = AssetsAPI.getAssetInfo(assetMovementRecord.getAssetId());
				String[] latLng = assetMovementRecord.getToGeoLocation().trim().split("\\s*,\\s*");
				double newLat = Double.parseDouble(latLng[0]);
				double newLng = Double.parseDouble(latLng[1]);
		    	SiteContext assetSite = SpaceAPI.getSiteSpace(assetContext.getIdentifiedLocation().getSiteId());
		      	if(isWithinSiteLocation(assetSite, newLat, newLng)) {
			  		asset.setCurrentLocation(assetMovementRecord.getToGeoLocation());
			  		asset.setCurrentSpaceId(-99);
		    		updateAsset(asset, assetMovementRecord.getAssetId());
			  		return;
			  	}
				List<SiteContext> sites = SpaceAPI.getAllSites(Collections.singletonList((LookupField) modBean.getField("location", FacilioConstants.ContextNames.SITE)));
				
				boolean isWithinAnySite = false;
			    for(SiteContext site : sites) {
			    	if(asset.getIdentifiedLocation().getSiteId() == site.getSiteId()) {
			    		continue;
			    	}
			    	if(isWithinSiteLocation(site, newLat, newLng)) {
			    		isWithinAnySite = true;
			    		asset.setCurrentLocation(assetMovementRecord.getToGeoLocation());
			    		asset.setIdentifiedLocation(site);
			    		asset.setCurrentSpaceId(-99);
			    		updateAsset(asset, assetMovementRecord.getAssetId());
			    		break;
		    		}
			    }
			    
				if(!isWithinAnySite) {
					throw new IllegalArgumentException("Invalid geolocation.Not present in any of the sites");
				}
			}
			
		}
   }
   
   public static void updateAsset(AssetContext asset, long assetId) throws Exception {
	   	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

	   UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
				.module(assetModule)
				.fields(assetFields)
				.andCondition(CriteriaAPI.getIdCondition(assetId, assetModule));

		updateBuilder.update(asset);

   }
	public static void updateAssetV3(V3AssetContext asset, long assetId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

		UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>()
				.module(assetModule)
				.fields(assetFields)
				.andCondition(CriteriaAPI.getIdCondition(assetId, assetModule));

		updateBuilder.update(asset);

	}
   public static boolean isWithInLocation (String location, double lat, double lng, int boundaryRadius) {
		if (StringUtils.isEmpty(location)) {
			return false;
		}
		return getDistance(location, lat, lng) <= boundaryRadius;
	}
	
   public static boolean isWithinSiteLocation (SiteContext site, double lat, double lng) {
		if(site.getBoundaryRadius() <= 0) {
    		site.setBoundaryRadius(FacilioConstants.ContextNames.SITE_BOUNDARY_RADIUS);//meters
    	}
	
		if (site == null || site.getLocation() == null || site.getLocation().getLat() == -1 || site.getLocation().getLng() == -1) {
			return false;
		}
		return FacilioUtil.calculateHaversineDistance(site.getLocation().getLat(), site.getLocation().getLng(), lat, lng) <= site.getBoundaryRadius();
	}
	
	public static double getDistance (String location, double lat, double lng) {
		if (StringUtils.isEmpty(location)) {
			return 0;
		}
		String[] latLng = location.trim().split("\\s*,\\s*");
		double prevLat = Double.parseDouble(latLng[0]);
		double prevLng = Double.parseDouble(latLng[1]);
		
		double distance = FacilioUtil.calculateHaversineDistance(prevLat, prevLng, lat, lng);
		return distance;
	}
   public static AssetMovementContext getAssetMovementContext(long id) throws Exception {
	   
	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		SelectRecordsBuilder<AssetMovementContext> selectBuilder = new SelectRecordsBuilder<AssetMovementContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetMovementContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
				;
		AssetMovementContext assetMovementRecord = selectBuilder.fetchFirst();
        return assetMovementRecord;
   }
   
   public static boolean checkIfAssetMovementNecessary(String toLocation, String currentLocation, long assetId) throws Exception {
	   if(StringUtils.isNotEmpty(toLocation) && StringUtils.isNotEmpty(currentLocation)) {
			String[] latLng = toLocation.trim().split("\\s*,\\s*");
			double newLat = Double.parseDouble(latLng[0]);
			double newLng = Double.parseDouble(latLng[1]);
			AssetContext asset = getAssetInfo(assetId);
			if(asset.getBoundaryRadius() <= 0) {
				asset.setBoundaryRadius(FacilioConstants.ContextNames.ASSET_BOUNDARY_RADIUS);
			}
			if(AssetsAPI.isWithInLocation(currentLocation, newLat, newLng, asset.getBoundaryRadius())) {
				return false;
			}
			return true;
		}
	   return false;
		
   }
   

   public static boolean getPendingAssetMovementRecordForAsset(long assetId) throws Exception {
	   
	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		String statusTableName = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS).getTableName();
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET_MOVEMENT);
		SelectRecordsBuilder<AssetMovementContext> selectBuilder = new SelectRecordsBuilder<AssetMovementContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetMovementContext.class)
				.innerJoin(statusTableName)
				.on("MODULE_STATE = "+ statusTableName+".ID")
				.andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(ViewFactory.getPendingAssetMovementStateTypeCriteria());
		;
		
		List<AssetMovementContext> assetMovementRecord = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(assetMovementRecord)) {
			return false;
		}
        return true;
   }
   
	public static JSONObject getAssetCategoryWithReadings (List<Long> buildingIds, boolean fetchOnlyAlarmPoints) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		
		FacilioModule assetCategoryModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
		List<FacilioField> assetCategoryFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY));
		Map<String, FacilioField> assetCategoryFieldMap = FieldFactory.getAsMap(assetCategoryFields);
		
		FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);
		
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(assetCategoryModule));
		fields.add(assetCategoryFieldMap.get("name"));
		fields.add(assetCategoryFieldMap.get("displayName"));
		
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.select(fields)
				.table(assetCategoryModule.getTableName())
				.moduleName(assetCategoryModule.getName())
				.beanClass(AssetCategoryContext.class)
				.innerJoin(assetModule.getTableName())
				.on(assetCategoryModule.getTableName()+"."+FieldFactory.getIdField(assetCategoryModule).getColumnName()+"="+assetModule.getTableName()+"."+assetFieldMap.get("category").getColumnName())
				.innerJoin(resourceModule.getTableName())
				.on(resourceModule.getTableName()+'.'+FieldFactory.getIdField(resourceModule).getColumnName()+'='+assetModule.getTableName()+".ID")
				.innerJoin(readingDataMetaModule.getTableName())
				.on(readingDataMetaModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+assetModule.getTableName()+".ID")
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),String.valueOf(ReadingInputType.HIDDEN_FORMULA_FIELD.getValue()), PickListOperators.ISN_T))
				.groupBy(FieldFactory.getIdField(assetCategoryModule).getCompleteColumnName());
		
		if(fetchOnlyAlarmPoints) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.IS));
		} else{
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.ISN_T));
		}

		if (buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		
		List<AssetCategoryContext> assetcategories = selectBuilder.get();
		JSONObject data = new JSONObject();
		
		for(AssetCategoryContext assetcategory : assetcategories) {
			data.put(assetcategory.getId(),assetcategory.getDisplayName());
		}
		return data;
	}
	public static JSONObject getAssetsWithReadingsForSpecificCategory(List<Long> buildingIds, List<Long> categoryIds, boolean fetchOnlyAlarmPoints) throws Exception{
		return getAssetsWithReadingsForSpecificCategory(buildingIds, categoryIds, fetchOnlyAlarmPoints, null);
	}
	public static JSONObject getAssetsWithReadingsForSpecificCategory(List<Long> buildingIds, List<Long> categoryIds, boolean fetchOnlyAlarmPoints, Criteria criteria) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(assetModule));
		fields.add(assetFieldMap.get("name"));
		fields.add(assetFieldMap.get("category"));
		
		FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);
		fields.add(readingFieldsMap.get("fieldId"));
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.table(assetModule.getTableName())
				.moduleName(assetModule.getName())
				.beanClass(AssetContext.class)
				.innerJoin(readingDataMetaModule.getTableName())
				.on(readingDataMetaModule.getTableName()+"."+readingFieldsMap.get("resourceId").getColumnName()+"="+assetModule.getTableName()+".ID")
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
				String.valueOf(ReadingInputType.HIDDEN_FORMULA_FIELD.getValue()), PickListOperators.ISN_T))
				.limit(30000); //Temp
		
		if(fetchOnlyAlarmPoints) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.IS));
		} else{
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.ISN_T));
		}
		
		if (categoryIds != null && !categoryIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(assetFieldMap.get("category"), StringUtils.join(categoryIds, ","), NumberOperators.EQUALS));
		}
		if (buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		if(criteria != null){
			selectBuilder.andCriteria(criteria);
		}
		
		List<AssetContext> assets = selectBuilder.get();
		
		JSONObject data = new JSONObject();

		JSONObject categoryVsFields = new JSONObject();
		data.put("categoryWithFields", categoryVsFields);

		JSONObject categoryVsAssets = new JSONObject();
		data.put("categoryWithAssets", categoryVsAssets);

		Map<Long, Object> assetMap = new JSONObject();
		data.put("assets", assetMap);

		Map<Long, Map<String,Object>> fieldMap = null;
		data.put("fields", fieldMap);

		if (assets == null || assets.isEmpty()) {
			return data;
		}
		Set<Long> fieldIds = assets.stream().map(asset -> (Long) asset.getData().get("fieldId")).collect(Collectors.toSet());
		List<FacilioField> fieldDetailList = modBean.getFields(fieldIds);
		fieldMap =new HashMap<>();
		for(FacilioField field: fieldDetailList) {
			if(canSkipField(field)) {
				continue;
			}
			Map<String, Object> details = getFieldDefaultProp(field);
			fieldMap.put(field.getFieldId(), details);
		}
		
		for(AssetContext asset: assets) {
			if (asset.getCategory() == null) {
				continue;
			}
			
			Long fieldId = (Long) asset.getData().get("fieldId");

			FacilioField field = modBean.getField(fieldId);
			if(field.getName().equals("info") && field.getColumnName().equals("SYS_INFO")) {
				continue;
			}

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

		data.put("categoryWithFields", categoryVsFields);
		data.put("categoryWithAssets", categoryVsAssets);
		data.put("assets", assetMap);
		data.put("fields", fieldMap);
		return data;
	}

	private static boolean canSkipField(FacilioField field) {
		return field.getName().equals("info") && field.getColumnName().equals("SYS_INFO");
	}

	public static JSONObject getAssetsWithReadings(List<Long> buildingIds, List<Long> categoryIds, List<Long> assetIds,
			List<Long> fieldIds, String searchText, JSONObject pagination, boolean fetchOnlyAssets,
			boolean fetchOnlyReadings, boolean fetchOnlyAssetReadingMap, boolean count,boolean fetchOnlyAlarmPoints) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		Map<String, FacilioField> assetFieldMap = FieldFactory.getAsMap(assetFields);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(assetModule));
		fields.add(assetFieldMap.get("name"));
		fields.add(assetFieldMap.get("category"));

		FacilioModule fieldFieldsModule = ModuleFactory.getFieldsModule();
		List<FacilioField> fieldFields = FieldFactory.getSelectFieldFields();
		Map<String, FacilioField> fieldFieldsMap = FieldFactory.getAsMap(fieldFields);

		FacilioModule readingDataMetaModule = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> readingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(readingFields);
		fields.add(readingFieldsMap.get("fieldId"));

		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(assetModule.getTableName()).moduleName(assetModule.getName()).beanClass(AssetContext.class)
				.innerJoin(readingDataMetaModule.getTableName())
				.on(readingDataMetaModule.getTableName() + "." + readingFieldsMap.get("resourceId").getColumnName()
						+ "=" + assetModule.getTableName() + ".ID")
				.innerJoin(fieldFieldsModule.getTableName())
				.on(fieldFieldsModule.getTableName() + "." + fieldFieldsMap.get("fieldId").getColumnName() + "="
						+ readingDataMetaModule.getTableName() + "." + readingFieldsMap.get("fieldId").getColumnName())
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
						String.valueOf(ReadingInputType.HIDDEN_FORMULA_FIELD.getValue()), PickListOperators.ISN_T));
		
		if(fetchOnlyAlarmPoints) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.IS));
		} else{
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),
					String.valueOf(ReadingInputType.ALARM_POINT_FIELD.getValue()), PickListOperators.ISN_T));
		}
		if (categoryIds != null && !categoryIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(assetFieldMap.get("category"),
					StringUtils.join(categoryIds, ","), NumberOperators.EQUALS));
		}

		if (buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","),
					BuildingOperator.BUILDING_IS));
		}

		if (assetIds != null && !assetIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(assetModule),
					StringUtils.join(assetIds, ","), NumberOperators.EQUALS));
		}

		if (fieldIds != null && !fieldIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("fieldId"),
					StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));
		}

		if (fetchOnlyAssets) {
			selectBuilder.groupBy(FieldFactory.getIdField(assetModule).getCompleteColumnName());
		} else if (fetchOnlyReadings) {
			selectBuilder.groupBy(readingFieldsMap.get("fieldId").getCompleteColumnName());
		}

		if (searchText != null && !searchText.isEmpty()) {
			Criteria criteria = new Criteria();
			if (fetchOnlyAssets) {
				criteria.addOrCondition(
						CriteriaAPI.getCondition(assetFieldMap.get("name"), searchText, StringOperators.CONTAINS));
			} else if (fetchOnlyReadings) {
				criteria.addOrCondition(CriteriaAPI.getCondition(fieldFieldsMap.get("displayName"), searchText,
						StringOperators.CONTAINS));
			} else {
				criteria.addOrCondition(
						CriteriaAPI.getCondition(assetFieldMap.get("name"), searchText, StringOperators.CONTAINS));
				criteria.addOrCondition(CriteriaAPI.getCondition(fieldFieldsMap.get("displayName"), searchText,
						StringOperators.CONTAINS));
			}
			selectBuilder.andCriteria(criteria);
		}

		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			if (perPage != -1) {
				int offset = ((page - 1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				selectBuilder.offset(offset);
				selectBuilder.limit(perPage);
			}
		}

		List<AssetContext> list = selectBuilder.get();

		JSONObject data = new JSONObject();

		JSONObject AssetsWithReadings = new JSONObject();
		JSONObject ReadingsWithAssets = new JSONObject();
		Map<Long, Object> assetMap = new JSONObject();
		Map<Long, Map<String, Object>> fieldMap = null;

		if (!fetchOnlyAssets && !fetchOnlyReadings && !fetchOnlyAssetReadingMap) {
			data.put("AssetsWithReadings", AssetsWithReadings);
			data.put("ReadingsWithAssets", ReadingsWithAssets);
			data.put("assets", assetMap);
			data.put("fields", fieldMap);
		}

		if (list == null || list.isEmpty()) {
			return data;
		}

		if (fetchOnlyAssets) {
			for (AssetContext asset : list) {
				if (asset.getCategory() == null) {
					continue;
				}
				assetMap.put(asset.getId(), asset.getName());
			}
			data.put("assets", assetMap);
			return data;
		}

		Set<Long> fieldIdsSet = list.stream().map(asset -> (Long) asset.getData().get("fieldId"))
				.collect(Collectors.toSet());
		List<FacilioField> fieldDetailList = modBean.getFields(fieldIdsSet);
		fieldMap = new HashMap<>();
		for (FacilioField field : fieldDetailList) {
			Map<String, Object> details = getFieldDefaultProp(field);
			fieldMap.put(field.getFieldId(), details);
		}

		if (fetchOnlyReadings) {
			data.put("fields", fieldMap);
			return data;
		}

		for (AssetContext asset : list) {
			if (asset.getCategory() == null) {
				continue;
			}

			Long fieldId = (Long) asset.getData().get("fieldId");

			List<Long> assetFieldIds;
			Map<Long, List<Long>> categoryAssets;
			if (!AssetsWithReadings.containsKey(asset.getId())) {
				assetFieldIds = new ArrayList<>();
				AssetsWithReadings.put(asset.getId(), assetFieldIds);
				assetMap.put(asset.getId(), asset.getName());
			} else {
				assetFieldIds = (List<Long>) AssetsWithReadings.get(asset.getId());
			}
			assetFieldIds.add(fieldId);

			Set<Long> assetIdsSet;
			Map<Long, Set<Long>> readings;
			if (!ReadingsWithAssets.containsKey(fieldId)) {
				assetIdsSet = new HashSet<Long>();
				ReadingsWithAssets.put(fieldId, assetIdsSet);
			} else {
				assetIdsSet = (Set<Long>) ReadingsWithAssets.get(fieldId);
			}
			assetIdsSet.add(asset.getId());

		}

		if (fetchOnlyAssetReadingMap) {
			data.put("AssetsWithReadings", AssetsWithReadings);
			data.put("ReadingsWithAssets", ReadingsWithAssets);
			return data;
		}

		data.put("AssetsWithReadings", AssetsWithReadings);
		data.put("ReadingsWithAssets", ReadingsWithAssets);
		data.put("assets", assetMap);
		data.put("fields", fieldMap);
		return data;
	}
	
	public static  List<Map<String, Object>> getRunStatusFields(long assetId) throws Exception {
		
		AssetContext asset = getAssetInfo(assetId);
		if (asset == null){
			return null;
		}
		AssetCategoryContext category = getCategoryForAsset(asset.getCategory().getId());
		
		String statusField = categoryVsRunStatus.get(category.getName());
		if (statusField == null) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();
        List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
        fields.addAll(FieldFactory.getSelectFieldFields());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        
        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(fieldMap.get("fieldId"));
        
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(readingDataMeta.getTableName())
                .select(selectFields)
                .innerJoin(fieldsModule.getTableName())
                .on(fieldsModule.getTableName() +".FIELDID = "+ readingDataMeta.getTableName()+".FIELD_ID")
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), assetId +"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("default"), String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), statusField, StringOperators.IS));
                ;
                
         List<Map<String, Object>> list = selectBuilder.get();
         return list;
	}
	
	private static Map<String, String> categoryVsRunStatus = Collections.unmodifiableMap(initializeRunStatusFields());
	private static Map<String, String> initializeRunStatusFields() {
		Map<String, String> statusMap = new HashMap<String, String>();
		// add more runstatus fields based on category
		statusMap.put("Chiller", "runStatus");
		statusMap.put("AHU", "runStatus");
		statusMap.put("FAHU", "runStatus");
		statusMap.put("Cooling Tower", "runStatus");
		statusMap.put("Primary Pump", "pumpRunStatus");
		statusMap.put("Secondary Pump", "pumpRunStatus");
		statusMap.put("Condenser Pump", "pumpRunStatus");
		
		return statusMap;
	}
	
	public static void deleteAsset(List<Long> assetsId) throws Exception {
		FacilioContext context = new FacilioContext();
		AssetContext asset = new AssetContext();
		asset.setDeleted(true);
		
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, assetsId);
		
		FacilioChain deleteAssetChain = FacilioChainFactory.getDeleteAssetChain();
		deleteAssetChain.execute(context);
	}
	
	public static long getAssetExpiryRuleId() throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(assetModule.getModuleId()),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleType"), String.valueOf(RuleType.BUSSINESS_LOGIC_ASSET_RULE.getIntVal()),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), "Warranty Expiry", StringOperators.IS))
				;

		ruleBuilder.select(Collections.singletonList(FieldFactory.getIdField(module)));
		return (long) ruleBuilder.fetchFirst().get("id");
	}
	
}

