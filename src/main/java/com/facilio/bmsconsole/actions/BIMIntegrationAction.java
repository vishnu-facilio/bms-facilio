package com.facilio.bmsconsole.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetCategoryContext.AssetCategoryType;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext.ThirdParty;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class BIMIntegrationAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	public String uploadBim() throws Exception {
				
		FacilioChain uploadFileChain = TransactionChainFactory.uploadBimFileChain();
		FacilioContext context = uploadFileChain.getContext();
		
		context.put(FacilioConstants.ContextNames.FILE_NAME, fileUploadFileName);
		context.put(FacilioConstants.ContextNames.FILE, fileUpload);
		context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileUploadContentType);
		
		uploadFileChain.execute();
		setResult(FacilioConstants.ContextNames.BIM_IMPORT_ID ,(Long)context.get(FacilioConstants.ContextNames.BIM_IMPORT_ID));
		setResult(FacilioConstants.ContextNames.VALID_SHEETS ,(org.json.simple.JSONArray)context.get(FacilioConstants.ContextNames.VALID_SHEETS));
		return SUCCESS;
	}
	
	public String importBim() throws Exception {
		
		FacilioChain importSheetsChain = TransactionChainFactory.importBimFileSheetsChain();
		
		FacilioContext context = importSheetsChain.getContext();
		context.put(FacilioConstants.ContextNames.SITE, site);
		context.put(FacilioConstants.ContextNames.BIM_IMPORT_ID, bimImportId);
		context.put(FacilioConstants.ContextNames.SELECTED_SHEET_NAMES, selectedSheetNames);
		
		importSheetsChain.execute();
		
		return SUCCESS;
	}
	
	public String checkStatus() throws Exception {
		FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> fields =  FieldFactory.getBimImportProcessMappingFields();
		List<BimImportProcessMappingContext> bimImportProcessList = BimAPI.getBimImportProcessMapping(module, fields, bimImportId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(BimImportProcessMappingContext bim : bimImportProcessList){
			FacilioModule facilioModule = modBean.getModule(bim.getModuleName());
			if(facilioModule != null){
				bim.setModuleName(facilioModule.getDisplayName());
			}
		}
		setResult("bimImportProcessList" , bimImportProcessList);
		
		return SUCCESS;
	}
	
	public String getBimIntegrationLogs() throws Exception {
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		List<BimIntegrationLogsContext> bimIntegrationList  = new ArrayList<>();
		bimIntegrationList = BimAPI.getBimIntegrationLog(module, fields);
		for(BimIntegrationLogsContext bim:bimIntegrationList){
			HashMap<String, Object> defaultValues = BimAPI.getBimDefaultValues(bim.getId(), "asset");
			if(defaultValues != null && defaultValues.get("site") != null){
				Long siteId = Long.parseLong(defaultValues.get("site").toString());
				SiteContext site = SpaceAPI.getSiteSpace(siteId);
				if(site != null){
					bim.setSiteName(site.getName());
				}
			}
		}
		setResult("bimIntegrationList" , bimIntegrationList);
		
		return SUCCESS;
	}

private static InputStream getInputStream(HttpURLConnection connection) throws Exception{
	
	String encoding = connection.getContentEncoding();
	InputStream iStream=connection.getInputStream();
	
	if("gzip".equalsIgnoreCase(encoding)) {
		 return new GZIPInputStream( iStream );
	}
	else if("deflate".equalsIgnoreCase(encoding)){
		return new InflaterInputStream(iStream, new Inflater(true) );
	}
	return iStream;
}

public static String getResponse (ThirdParty thirdParty, String requestURL,String accessToken) throws Exception {
	if(requestURL.contains(" ")){
		requestURL = requestURL.replace(" ", "%20");
	}
	String response = "";
	URL request = new URL(requestURL.trim());
	HttpURLConnection connection = (HttpURLConnection) request.openConnection();
	try {
		
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.setConnectTimeout(300000);
		connection.setReadTimeout(300000);
		if(thirdParty.equals(ThirdParty.INVICARA)){
			connection.setRequestProperty ("Authorization", "Bearer "+accessToken);
		}else if(thirdParty.equals(ThirdParty.YOUBIM)){
			connection.setRequestProperty ("x-auth-token", accessToken);
		}
		
		connection.connect();
		
		int responseCode=connection.getResponseCode();

		InputStream iStream=null;
		if(responseCode == HttpURLConnection.HTTP_OK){
			iStream=getInputStream(connection);
		}
		else {
			iStream=connection.getErrorStream();
		}

		try (BufferedReader reader=new BufferedReader(new InputStreamReader(iStream,"UTF-8"))) {
			String rawResponse="";
			while( (rawResponse = reader.readLine()) != null ) {
				response = rawResponse;
			}
		}
		
	} catch (IOException e) {
		throw e;	
	} finally {		
		connection.disconnect();
	}
	return response;
}

public String getAccessToken(ThirdParty thirdParty,HashMap<String,String> thirdPartyDetailsMap) throws Exception{
	CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build() ;

	URIBuilder uri = new URIBuilder(thirdPartyDetailsMap.get("tokenURL"))
			.addParameter("grant_type", thirdPartyDetailsMap.get("grant_type"))
			.addParameter("username", thirdPartyDetailsMap.get("username"))
			.addParameter("password", thirdPartyDetailsMap.get("password"));

	RequestConfig config = RequestConfig
	      .custom()
	      .setConnectTimeout(30000)
	      .setConnectionRequestTimeout(30000)
	      .setSocketTimeout(300000)
	      .build();
	  HttpPost request = new HttpPost(uri.build());
	  request.setConfig(config);
	  request.addHeader("Content-Type", "application/json");
	  CloseableHttpResponse response = client.execute(request);

	  StringBuilder result = new StringBuilder();

        try(BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
        	
        	String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }
        JSONParser parser = new JSONParser();
        
	if(thirdParty.equals(ThirdParty.INVICARA)){
		return ((JSONObject) parser.parse(result.toString())).get("access_token").toString();
	}else if(thirdParty.equals(ThirdParty.YOUBIM)){
//		return ((JSONObject)parser.parse(((JSONObject)parser.parse(((JSONObject) parser.parse(result.toString())).get("response").toString())).get("data").toString())).get("token").toString();
		return "4b41407126fa3d8a9f1e709d4d813ccf63685af1";
	}
	
	return "";
}

	public String bimJsonImport() throws Exception {
		
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		
		BimIntegrationLogsContext bimIntegrationLog=new BimIntegrationLogsContext();
		bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.INPROGRESS);
		bimIntegrationLog.setUploadedBy(AccountUtil.getCurrentUser().getOuid());
		bimIntegrationLog.setThirdParty(thirdParty.getValue());
		HashMap<String,String> thirdPartyDetailsMap = BimAPI.getThirdPartyDetailsMap(thirdParty);
		
		String token = getAccessToken(thirdParty,thirdPartyDetailsMap);
		addThirdPartyIdCustomFieldInAsset();
		
		if(thirdParty.equals(ThirdParty.INVICARA)){
			bimIntegrationLog.setNoOfModules(1);
			long bimid = BimAPI.addBimIntegrationLog(module,fields,bimIntegrationLog);
			bimIntegrationLog.setId(bimid);
			
			importInvicaraAssets(thirdParty,thirdPartyDetailsMap.get("assetURL"),token);
			bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.COMPLETED);
			BimAPI.updateBimIntegrationLog(module,fields,bimIntegrationLog);
		}else if(thirdParty.equals(ThirdParty.YOUBIM)){
			bimIntegrationLog.setNoOfModules(3);
			long bimid = BimAPI.addBimIntegrationLog(module,fields,bimIntegrationLog);
			bimIntegrationLog.setId(bimid);

			importYouBimSitesBuildingAssets(thirdParty,thirdPartyDetailsMap.get("siteURL"),thirdPartyDetailsMap.get("buildingURL"),thirdPartyDetailsMap.get("assetCategoryURL"),thirdPartyDetailsMap.get("assetURL"),token);
			bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.COMPLETED);
			BimAPI.updateBimIntegrationLog(module,fields,bimIntegrationLog);
		}
		return SUCCESS;
	}
	
	public void addThirdPartyIdCustomFieldInAsset() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields("asset"));
		if(!fieldsMap.containsKey("thirdpartyid")){
			FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
			FacilioContext context = addFieldsChain.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
			FacilioField field =  new FacilioField();
			field.setDataType(1);
			field.setDisplayName("Third Party Id");
			field.setDisplayType(FieldDisplayType.TEXTBOX);
			field.setDisplayTypeInt(1);
			field.setRequired(false);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(field));
			
			addFieldsChain.execute();
		}
	}
	
	public void importYouBimSitesBuildingAssets(ThirdParty thirdParty,String siteURL,String buildingURL,String assetCategoryURL,String assetURL,String token) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<Long,Long> oldNewSiteIds = new HashMap<>();
		Map<Long,Long> oldNewBuildingIds = new HashMap<>();
		
		String sitesJsonString= getResponse(thirdParty,siteURL,token);
		JSONParser parser = new JSONParser();
		JSONObject resultObj = (JSONObject) parser.parse(sitesJsonString);
		JSONArray arr = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
		for(int i=0;i<arr.length();i++){

			JSONObject result = (JSONObject) parser.parse(arr.get(i).toString());
			long oldSiteId = Long.parseLong(result.get("id").toString());
			String siteName= result.get("name").toString();
			
			SiteContext site = SpaceAPI.getSite(siteName);
			if(site != null){
				site.setName(siteName);
				updateSite(site);
			}else{
				site = new SiteContext();
				site.setName(siteName);
				addSite(site);
			}
			long newSiteId =  site.getId();
			oldNewSiteIds.put(oldSiteId, newSiteId);
		}
		
		String buildingsJsonString= getResponse(thirdParty,buildingURL,token);
		resultObj = (JSONObject) parser.parse(buildingsJsonString);
		arr = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
		HashMap<String,String> assetCategoryMap = BimAPI.getThirdPartyAssetCategoryNames(thirdParty);
		
		for(int i=0;i<arr.length();i++){

			JSONObject result = (JSONObject) parser.parse(arr.get(i).toString());
			long oldBuildingId = Long.parseLong(result.get("id").toString());
			String buildingName= result.get("name").toString();
			long siteId = Long.parseLong(result.get("site_id").toString());
			
			
			BuildingContext building = SpaceAPI.getBuilding(buildingName);
			if(building != null){
				updateBuilding(building);
			}else{
				building = new BuildingContext();
				building.setName(buildingName);
				building.setSiteId(oldNewSiteIds.get(siteId));
				addBuilding(building);
			}
			
			long newBuildingId =  building.getId();
			oldNewBuildingIds.put(oldBuildingId, newBuildingId);
			
			String assetCategorysJsonString= getResponse(thirdParty,assetCategoryURL+String.valueOf(oldBuildingId),token);
			resultObj = (JSONObject) parser.parse(assetCategorysJsonString);
			JSONArray arr1 = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
			
			for(int j=0;j<arr1.length();j++){
				JSONObject result1 = (JSONObject) parser.parse(arr1.get(j).toString());
				String moduleName = "";
				String assetCategoryName= result1.get("name").toString();
				long oldAssetCategoryId= Long.parseLong(result1.get("id").toString());
				if(assetCategoryMap.containsKey(assetCategoryName)){
					assetCategoryName = assetCategoryMap.get(assetCategoryName).toString();
					AssetCategoryContext assetCategory = AssetsAPI.getCategory(assetCategoryName);
					
					if(assetCategory == null){
						assetCategory = AssetsAPI.getCategory(assetCategoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						if(assetCategory == null){
							if(assetCategoryName != null && !assetCategoryName.isEmpty()) {
								assetCategory = new AssetCategoryContext();
								assetCategory.setName(assetCategoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
								assetCategory.setType(AssetCategoryType.MISC);
								FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
								FacilioContext context1 = addAssetCategoryChain.getContext();
								context1.put(FacilioConstants.ContextNames.RECORD, assetCategory);
								addAssetCategoryChain.execute();
							}
						}
					}
					
					FacilioModule module = modBean.getModule(assetCategory.getAssetModuleID());
					moduleName = module.getName();
					
					String assetsJsonString= getResponse(thirdParty,assetURL+String.valueOf(oldAssetCategoryId),token);
					resultObj = (JSONObject) parser.parse(assetsJsonString);
					JSONArray arr2 = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
					
					for(int k=0;k<arr2.length();k++){
						JSONObject result2 = (JSONObject) parser.parse(arr2.get(k).toString());
						String assetName= result2.get("name").toString();
						String thirdPartyId= result2.get("id").toString();
						String description= result2.get("description").toString();
						
						long assetId = AssetsAPI.getAssetId(assetName, AccountUtil.getCurrentOrg().getId());
						if(assetId > 0){
							AssetContext asset = AssetsAPI.getAssetInfo(assetId);
							asset.setDatum("thirdpartyid", thirdPartyId);
							asset.setName(assetName);
							asset.setDescription(description);
							asset.setSiteId(building.getSiteId());
							asset.setCategory(assetCategory);
							updateAsset(asset,moduleName);
						}else{
							AssetContext asset = new AssetContext();
							asset.setDatum("thirdpartyid", thirdPartyId);
							asset.setName(assetName);
							asset.setDescription(description);
							asset.setSiteId(building.getSiteId());
							asset.setCategory(assetCategory);
							addAsset(asset,moduleName);
						}
						
					}
				}
			}
		}
	}
	
	public void importInvicaraAssets(ThirdParty thirdParty,String assetURL,String token) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String assetsJsonString= getResponse(thirdParty,assetURL,token);

		JSONParser parser = new JSONParser();

		JSONObject resultObj = (JSONObject) parser.parse(assetsJsonString);
		JSONArray arr = new JSONArray(resultObj.get("_list").toString());
		
		for(int i=0;i<arr.length();i++){

			JSONObject result = (JSONObject) parser.parse(arr.get(i).toString());
			String assetName= result.get("Asset Name").toString();
			
			String thirdPartyId= result.get("_id").toString();
			JSONObject properties=  (JSONObject) parser.parse(result.get("properties").toString());
			List<SiteContext> sites = SpaceAPI.getAllSites();
			Long siteId = sites.get(0).getId();
			
			String category = ((JSONObject)parser.parse(properties.get("dtCategory").toString())).get("val").toString();
			JSONObject snoObject = (JSONObject)parser.parse(properties.get("Serial Number").toString());
			
			String serialNumber = "";
			if(snoObject.containsKey("val")){
				serialNumber =  snoObject.get("val").toString();
			}
			
			String tag=  ((JSONObject)parser.parse(properties.get("Asset Tag").toString())).get("val").toString();
			JSONObject modelObject = (JSONObject)parser.parse(properties.get("Model").toString());
			String model = "";
			if(modelObject.containsKey("val")){
				model =  modelObject.get("val").toString();
			}
			String description=  ((JSONObject)parser.parse(properties.get("COBie Type Description").toString())).get("val").toString();
			String manufacturer=  ((JSONObject)parser.parse(properties.get("Manufacturer").toString())).get("val").toString();
			
			
			long assetId = AssetsAPI.getAssetId(assetName, AccountUtil.getCurrentOrg().getId());
			if(assetId > 0){
				AssetContext asset = AssetsAPI.getAssetInfo(assetId);
				asset.setDatum("thirdpartyid", thirdPartyId);
				asset.setName(assetName);
				asset.setDescription(description);
				asset.setSiteId(siteId);
				asset.setManufacturer(manufacturer);
				asset.setSerialNumber(serialNumber);
				asset.setTagNumber(tag);
				asset.setModel(model);
				asset.setCategory(asset.getCategory());
				updateAsset(asset, asset.getCategory().getModuleName());
			}else{
				String moduleName = "";
				String categoryName = "";
				
				HashMap<String,String> assetCategoryMap = BimAPI.getThirdPartyAssetCategoryNames(thirdParty);
				categoryName = assetCategoryMap.get(category).toString();
				
				AssetCategoryContext assetCategory = AssetsAPI.getCategory(categoryName);
				
				if(assetCategory == null){
					assetCategory = AssetsAPI.getCategory(categoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						if(assetCategory == null){
							assetCategory = new AssetCategoryContext();
							if(categoryName != null && !categoryName.isEmpty()) {
								assetCategory.setName(categoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
								assetCategory.setType(AssetCategoryType.MISC);
								FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
								FacilioContext context = addAssetCategoryChain.getContext();
								context.put(FacilioConstants.ContextNames.RECORD, assetCategory);
								addAssetCategoryChain.execute();	
							}
						}	
				}

				FacilioModule module = modBean.getModule(assetCategory.getAssetModuleID());
				moduleName = module.getName();
				
				AssetContext asset = new AssetContext();
				asset.setDatum("thirdpartyid", thirdPartyId);
				asset.setName(assetName);
				asset.setDescription(description);
				asset.setSiteId(siteId);
				asset.setManufacturer(manufacturer);
				asset.setSerialNumber(serialNumber);
				asset.setTagNumber(tag);
				asset.setModel(model);
				asset.setCategory(assetCategory);
				addAsset(asset,moduleName);
			}
		}
	}
	
	public void addSite(SiteContext site) throws Exception {
		FacilioChain addCampus = FacilioChainFactory.getAddCampusChain();
		FacilioContext context = addCampus.getContext();
		context.put(FacilioConstants.ContextNames.SITE, site);
		addCampus.execute();
	}
	
	public void updateSite(SiteContext site) throws Exception {
		FacilioContext context = new FacilioContext();
		LocationContext location = site.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(site.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
				site.setLocation(null);
			}
			else {
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			site.setLocation(null);
		}
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE, site);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "site");
		FacilioChain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
	}
	
	public void addBuilding(BuildingContext building) throws Exception {
		FacilioChain addBuilding = FacilioChainFactory.getAddBuildingChain();
		FacilioContext context = addBuilding.getContext();
		context.put(FacilioConstants.ContextNames.BUILDING, building);
		addBuilding.execute();
	}
	
	public void updateBuilding(BuildingContext building) throws Exception {
		FacilioChain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		FacilioContext context = updateCampus.getContext();
		LocationContext location = building.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(building.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
				building.setLocation(null);
			}
			else {
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			building.setLocation(null);
		}
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE, building);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "building");
		
		updateCampus.execute();
	}
	
	public void addAsset(AssetContext asset,String moduleName) throws Exception {
		FacilioChain addAssetChain = TransactionChainFactory.getAddAssetChain();
		FacilioContext context = addAssetChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		AssetCategoryContext assetCategory= asset.getCategory();
		long categoryId=-1;
		if(assetCategory!=null && assetCategory.getId() != 0) {
			categoryId=assetCategory.getId();
		}
		if (asset.getSpace() == null) {
			BaseSpaceContext assetLocation = new BaseSpaceContext();
			assetLocation.setId(asset.getSiteId());
			asset.setSpace(assetLocation);
		}
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		addAssetChain.execute();
		assetDetails(asset.getId());
	}
	
	
	public void updateAsset(AssetContext asset,String moduleName) throws Exception {
		FacilioChain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();
		FacilioContext context = updateAssetChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, asset);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(asset.getId()));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		// cannot update module state directly
		if (asset.getModuleState() != null) {
			asset.setModuleState(null);
		}
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		
		updateAssetChain.execute();
	}
	
	
	public void assetDetails(long assetId) throws Exception {
		
		FacilioChain assetDetailsChain = FacilioChainFactory.getAssetDetailsChain();
		FacilioContext context = assetDetailsChain.getContext();
		context.put(FacilioConstants.ContextNames.ID, assetId);
		AssetContext asset= AssetsAPI.getAssetInfo(assetId, true);
		AssetCategoryContext category= asset.getCategory();
		
		if (category != null && category.getId() != -1) {
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
		}
		context.put(FacilioConstants.ContextNames.SHOW_RELATIONS_COUNT, null);
		context.put(FacilioConstants.ContextNames.FETCH_HIERARCHY, false);
		
		assetDetailsChain.execute();
	}
	
	private ThirdParty thirdParty;
	
	public ThirdParty getThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(ThirdParty thirdParty) {
		this.thirdParty = thirdParty;
	}

	private SiteContext site;

	public SiteContext getSite() {
		return site;
	}

	public void setSite(SiteContext site) {
		this.site = site;
	}

	private Long bimImportId;
	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
	private Set<String> selectedSheetNames;

	public Long getBimImportId() {
		return bimImportId;
	}

	public void setBimImportId(Long bimImportId) {
		this.bimImportId = bimImportId;
	}

	public Set<String> getSelectedSheetNames() {
		return selectedSheetNames;
	}

	public void setSelectedSheetNames(Set<String> selectedSheetNames) {
		this.selectedSheetNames = selectedSheetNames;
	}

	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getFileUploadContentType() {
		return fileUploadContentType;
	}

	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

}
