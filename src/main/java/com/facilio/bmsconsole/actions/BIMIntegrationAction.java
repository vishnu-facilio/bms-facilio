package com.facilio.bmsconsole.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
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
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class BIMIntegrationAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(BIMIntegrationAction.class.getName());
	
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
		connection.setReadTimeout(500000);
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
			.addParameter("username", getUserName())
			.addParameter("password", getPassword());

	RequestConfig config = RequestConfig
	      .custom()
	      .setConnectTimeout(50000)
	      .setConnectionRequestTimeout(30000)
	      .setSocketTimeout(300000)
	      .build();
	  HttpPost request = new HttpPost(uri.build());
	  request.setConfig(config);
	  request.addHeader("Content-Type", "application/json");
	  
	  String requestBody = "{\"username\":\""+ getUserName() +"\",\"password\":\""+ getPassword() +"\"}";

	  HttpEntity entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
	  request.setEntity(entity);
	  
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
		return ((JSONObject)parser.parse(((JSONObject)parser.parse(((JSONObject) parser.parse(result.toString())).get("response").toString())).get("data").toString())).get("token").toString();
	}
	
	return "";
}
	public String getYouBimViewDetails() throws Exception {
		List<CustomModuleData> assetDatas = getCustomModuleData("assetData", String.valueOf(bimAssetId));
		List<CustomModuleData> typeDatas = getCustomModuleData("typeData", String.valueOf(bimAssetId));
		List<CustomModuleData> workorderDatas = getCustomModuleData("workorderData", String.valueOf(bimAssetId));
		List<CustomModuleData> documentDatas = getCustomModuleData("documentData", String.valueOf(bimAssetId));
		
		JSONParser parser = new JSONParser();
        
		Map<String,String> assetDataMap = new HashMap<String,String>();
		for(CustomModuleData data:assetDatas){
			JSONObject json = (JSONObject) parser.parse(data.getDatum("value").toString());
			Set<Map.Entry<String,String>> set= json.entrySet();
			for(Map.Entry<String,String>  en: set){
				assetDataMap.put(en.getKey(),en.getValue());
			}
		}
		setResult("assetData", assetDataMap);
		System.out.println("assetDataArr :: "+assetDataMap);
		
		Map<String,String> typeDataMap = new HashMap<String,String>();
		for(CustomModuleData data:typeDatas){
			JSONObject json = (JSONObject) parser.parse(data.getDatum("value").toString());
			for(Map.Entry<String,String>  en: (Set<Map.Entry<String,String>>)json.entrySet()){
				typeDataMap.put(en.getKey(),en.getValue());
			}
		}
		setResult("typeData", typeDataMap);
		
		List<JSONObject> woDataList = new LinkedList<>();
		for(CustomModuleData data:workorderDatas){
			JSONArray json = new JSONArray(data.getDatum("value").toString());
			for(int i=0;i<json.length();i++){
				JSONObject json1 = (JSONObject) parser.parse(json.get(i).toString());
				woDataList.add(json1);
			}
		}
		setResult("workorderData", woDataList);
		
		List<JSONObject> documentDataList = new LinkedList<>();
		for(CustomModuleData data:documentDatas){
			JSONArray json = new JSONArray(data.getDatum("value").toString());
			for(int i=0;i<json.length();i++){
				JSONObject json1 = (JSONObject) parser.parse(json.get(i).toString());
				documentDataList.add(json1);
			}
		}
		setResult("documentData", documentDataList);
		
		return SUCCESS;
	}
	public String bimJsonImport() throws Exception {
		
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		
		BimIntegrationLogsContext bimIntegrationLog=new BimIntegrationLogsContext();
		bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.INPROGRESS);
		bimIntegrationLog.setUploadedBy(AccountUtil.getCurrentUser().getOuid());
		bimIntegrationLog.setThirdParty(thirdParty.getValue());
		HashMap<String,String> thirdPartyDetailsMap = BimAPI.getThirdPartyDetailsMap(thirdParty);
		setAssetURL(assetURL !=null ? assetURL : thirdPartyDetailsMap.get("assetURL"));
		setUserName(userName !=null ? userName : thirdPartyDetailsMap.get("userName"));
		setPassword(password !=null ? password : thirdPartyDetailsMap.get("password"));
		String token = getAccessToken(thirdParty,thirdPartyDetailsMap);
		addThirdPartyIdCustomModuleFieldInAsset(thirdParty);
		
		if(thirdParty.equals(ThirdParty.INVICARA)){
			bimIntegrationLog.setNoOfModules(1);
			long bimid = BimAPI.addBimIntegrationLog(module,fields,bimIntegrationLog);
			bimIntegrationLog.setId(bimid);
			
			importInvicaraAssets(thirdParty,getAssetURL(),token);
			bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.COMPLETED);
			BimAPI.updateBimIntegrationLog(module,fields,bimIntegrationLog);
		}else if(thirdParty.equals(ThirdParty.YOUBIM)){
			bimIntegrationLog.setNoOfModules(3);
			long bimid = BimAPI.addBimIntegrationLog(module,fields,bimIntegrationLog);
			bimIntegrationLog.setId(bimid);

			importYouBimSitesBuildingAssets(thirdParty,thirdPartyDetailsMap,getAssetURL(),token);
			bimIntegrationLog.setStatus(BimIntegrationLogsContext.Status.COMPLETED);
			BimAPI.updateBimIntegrationLog(module,fields,bimIntegrationLog);
		}
		return SUCCESS;
	}
	
	public void addThirdPartyIdCustomModuleFieldInAsset(ThirdParty thirdParty) throws Exception {
		List<FacilioField> assetCustomFields = new ArrayList<FacilioField>();
		List<FacilioField> buildingCustomFields = new ArrayList<FacilioField>();
		List<FacilioField> bimViewCustomFields = new ArrayList<FacilioField>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields("asset"));
		if(!fieldsMap.containsKey("thirdpartyid")){
			assetCustomFields.add(getCustomField("Third Party Id", FieldDisplayType.TEXTBOX, 1, 1, false));
		}
		
		if(thirdParty.equals(ThirdParty.INVICARA)){
			
			if(!fieldsMap.containsKey("runstatus")){
				BooleanField field =  new BooleanField();
				field.setDataType(4);
				field.setDisplayName("Run status");
				field.setDisplayType(FieldDisplayType.DECISION_BOX);
				field.setDisplayTypeInt(4);
				field.setRequired(false);
				field.setTrueVal("On");
				field.setFalseVal("Off");
				assetCustomFields.add(field);
			}
			
			if(!fieldsMap.containsKey("servingspacename")){
				assetCustomFields.add(getCustomField("Serving space Name", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("servingspacenumber")){
				assetCustomFields.add(getCustomField("Serving space Number", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("systemname")){
				assetCustomFields.add(getCustomField("System Name", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("systemclassification")){
				assetCustomFields.add(getCustomField("System Classification", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("uniclass")){
				assetCustomFields.add(getCustomField("Uniclass", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("uniclassname")){
				assetCustomFields.add(getCustomField("Uniclass Name", FieldDisplayType.TEXTBOX, 1, 1, false));
			}	
		}
		if(thirdParty.equals(ThirdParty.YOUBIM)){
			if(!fieldsMap.containsKey("2dviewid")){
				assetCustomFields.add(getCustomField("2d View Id", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("3dviewid")){
				assetCustomFields.add(getCustomField("3d View Id", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			if(!fieldsMap.containsKey("thirdpartytypeid")){
				assetCustomFields.add(getCustomField("Third Party Type Id", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			
			if(!assetCustomFields.isEmpty()){
				FacilioChain addAssetFieldsChain = TransactionChainFactory.getAddFieldsChain();
				FacilioContext context = addAssetFieldsChain.getContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, "asset");
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, assetCustomFields);
				addAssetFieldsChain.execute();
			}
			
			Map<String,FacilioField> buildingFieldsMap = FieldFactory.getAsMap(modBean.getAllFields("building"));
			if(!buildingFieldsMap.containsKey("thirdpartyid")){
				buildingCustomFields.add(getCustomField("Third Party Id", FieldDisplayType.TEXTBOX, 1, 1, false));
			}

			if(!buildingCustomFields.isEmpty()){
				FacilioChain addBuildingFieldsChain = TransactionChainFactory.getAddFieldsChain();
				FacilioContext context1 = addBuildingFieldsChain.getContext();
				context1.put(FacilioConstants.ContextNames.MODULE_NAME, "building");
				context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, buildingCustomFields);
				addBuildingFieldsChain.execute();
			}
			
			FacilioModule customBimViewModule = modBean.getModule("custom_bimview");
			if(customBimViewModule == null){
				FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
				FacilioContext context = addModulesChain.getContext();
				context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, "bimView");
				context.put(FacilioConstants.ContextNames.MODULE_TYPE, 1);
				context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, "bimView");
				addModulesChain.execute();
				customBimViewModule = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
			}
			Map<String,FacilioField> bimViewFieldsMap = FieldFactory.getAsMap(modBean.getAllFields("custom_bimview"));
			
			if(!bimViewFieldsMap.containsKey("thirdpartyid")){
				bimViewCustomFields.add(getCustomField("Third Party Id", FieldDisplayType.TEXTBOX, 1, 2, false));
			}
			
			if(!bimViewFieldsMap.containsKey("valuetype")){
				bimViewCustomFields.add(getCustomField("Value Type", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			
			if(!bimViewFieldsMap.containsKey("value")){
				bimViewCustomFields.add(getCustomField("Value", FieldDisplayType.TEXTBOX, 1, 1, false));
			}
			
			if(!bimViewCustomFields.isEmpty()){
				FacilioChain addBimViewFieldsChain = TransactionChainFactory.getAddFieldsChain();
				FacilioContext context1 = addBimViewFieldsChain.getContext();
				context1.put(FacilioConstants.ContextNames.MODULE_NAME, "custom_bimview");
				context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, bimViewCustomFields);
				addBimViewFieldsChain.execute();
			}
			
		}
	}
	
	public FacilioField getCustomField(String displayName,FieldDisplayType displayType,int displayTypeInt,int dataType,boolean required){
		FacilioField field =  new FacilioField();
		field.setDataType(dataType);
		field.setDisplayName(displayName);
		field.setDisplayType(displayType);
		field.setDisplayTypeInt(displayTypeInt);
		field.setRequired(required);
		
		return field;
	}
	
	public void importYouBimSitesBuildingAssets(ThirdParty thirdParty,HashMap<String,String> thirdPartyDetailsMap,String assetURL,String token) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<Long,Long> oldNewSiteIds = new HashMap<>();
		Map<Long,Long> oldNewBuildingIds = new HashMap<>();
		
		String sitesJsonString= getResponse(thirdParty,thirdPartyDetailsMap.get("siteURL"),token);
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
		
		String buildingsJsonString= getResponse(thirdParty,thirdPartyDetailsMap.get("buildingURL"),token);
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
				building.setDatum("thirdpartyid", oldBuildingId);
				building.setName(buildingName);
				building.setSiteId(oldNewSiteIds.get(siteId));
				updateBuilding(building);
			}else{
				building = new BuildingContext();
				building.setDatum("thirdpartyid", oldBuildingId);
				building.setName(buildingName);
				building.setSiteId(oldNewSiteIds.get(siteId));
				addBuilding(building);
			}
			
			long newBuildingId =  building.getId();
			oldNewBuildingIds.put(oldBuildingId, newBuildingId);
			
			String assetCategorysJsonString= getResponse(thirdParty,thirdPartyDetailsMap.get("assetCategoryURL")+String.valueOf(oldBuildingId),token);
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
//						assetCategory = AssetsAPI.getCategory(assetCategoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
//						if(assetCategory == null){
							if(assetCategoryName != null && !assetCategoryName.isEmpty()) {
								assetCategory = new AssetCategoryContext();
								assetCategory.setName(assetCategoryName);
								assetCategory.setType(AssetCategoryType.MISC);
								FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
								FacilioContext context1 = addAssetCategoryChain.getContext();
								context1.put(FacilioConstants.ContextNames.RECORD, assetCategory);
								addAssetCategoryChain.execute();
							}
//						}
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
						JSONObject viewIdJson = (JSONObject) parser.parse(result2.get("modeling_identifier").toString());
						String serialNumber = "";
						String manufacturer = "";
						String model = "";
						String tag = "";
						
						long assetId = AssetsAPI.getAssetId(assetName, thirdPartyId);
							
						String assetDataJsonString= getResponse(thirdParty,thirdPartyDetailsMap.get("assetdataURL")+thirdPartyId+"&limit=100",token);
						String typeDataJsonString= getResponse(thirdParty,thirdPartyDetailsMap.get("typedataURL")+oldAssetCategoryId+"?ContainTypeattributes=all",token);
						String workordersString= getResponse(thirdParty,thirdPartyDetailsMap.get("workordersURL")+thirdPartyId,token);
						String documentsString= getResponse(thirdParty,thirdPartyDetailsMap.get("documentsURL")+thirdPartyId,token);
						
						resultObj = (JSONObject) parser.parse(assetDataJsonString);
						JSONArray arr3  = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
						Map<String,String> assetDatas = new LinkedHashMap<String,String>();
						int assetDatalength = arr3.length();
						int l=1;
						for(int o=0;o<assetDatalength;o+=25){
							JSONObject arr4 = new JSONObject();
							for(int m=o;m<o+25;m++){
								if(m<assetDatalength){
									JSONObject result3 = (JSONObject) parser.parse(arr3.get(m).toString());
									arr4.put(result3.get("name").toString(), result3.get("value").toString());
									if(result3.get("name").toString().equals("Serial number")){
										serialNumber = result3.get("value").toString();
									}else if(result3.get("name").toString().equals("Tag Number")){
										tag = result3.get("value").toString();
									}
								}
							}
							assetDatas.put("assetData_"+l, arr4.toString());
							l++;
						}

						resultObj = (JSONObject) parser.parse(typeDataJsonString);
						JSONObject result4 = (JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString());
						JSONObject arr5 = new JSONObject();

						arr5.put("Type", result4.get("name").toString());
						arr5.put("Description", result4.get("description").toString());
						arr5.put("AssetType", result4.get("assettype").toString());
						
						if(result4.get("manufacturer_id") != null){
							JSONObject manufacturerJson = (JSONObject)parser.parse(result4.get("manufacturer").toString());
							manufacturer =  manufacturerJson.get("email").toString();
						}
						arr5.put("Manufacturer", manufacturer);
						
						model = result4.get("modelnumber").toString();
						arr5.put("ModelNumber", model);
						
						String warrantyDurationUnit = "";
						if(result4.get("warrantydurationunit_id")!=null){
							warrantyDurationUnit = result4.get("warrantydurationunit_id").toString();
						}
						arr5.put("WarrantyDurationUnit", warrantyDurationUnit);
						
						String warrantyGuarantorParts = "";
						if(result4.get("warrantyguarantorparts_id") != null){
							JSONObject guarantorparts = (JSONObject)parser.parse(result4.get("guarantorparts").toString());
							warrantyGuarantorParts =guarantorparts.get("email").toString();
						}
						arr5.put("WarrantyGuarantorParts", warrantyGuarantorParts);
						
						arr5.put("WarrantyDurationParts", result4.get("warrantydurationparts").toString());
						
						String WarrantyGuarantorLabor = "";
						if(result4.get("warrantyguarantorlabor_id") != null){
							JSONObject guarantorlabor = (JSONObject)parser.parse(result4.get("guarantorlabor").toString());
							WarrantyGuarantorLabor = guarantorlabor.get("email").toString();
						}
						arr5.put("WarrantyGuarantorLabor", WarrantyGuarantorLabor);
						
						arr5.put("WarrantyDurationLabor", result4.get("warrantydurationlabor").toString());
						
						
						String typeData = arr5.toString();
						
						resultObj = (JSONObject) parser.parse(workordersString);
						JSONArray workorderDataJson  = new JSONArray(((JSONObject) parser.parse(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString())).get("records").toString());
						
						Map<String,String> woDatas = new LinkedHashMap<String,String>();
						int woLength = workorderDataJson.length();
						int p = 1;
						for(int o=0;o<woLength;o+=5){
							JSONArray arr6 = new JSONArray();
							for(int m=o;m<o+5;m++){
								if(m<woLength){
									JSONObject json1 = (JSONObject) parser.parse(workorderDataJson.get(m).toString());
									JSONObject json2 = new JSONObject();
									json2.put("status", ((JSONObject)parser.parse(json1.get("status").toString())).get("name").toString());
									json2.put("desc", json1.get("description").toString().replaceAll("\n", ""));
									json2.put("priority", ((JSONObject)parser.parse(json1.get("priority").toString())).get("name").toString());
									json2.put("assignee", ((JSONObject)parser.parse(json1.get("user").toString())).get("first_name").toString() + " " +((JSONObject)parser.parse(json1.get("user").toString())).get("last_name").toString());
									arr6.put(json2);
								}
							}
							woDatas.put("workorderData_"+p, arr6.toString());
							p++;
						}
						
						resultObj = (JSONObject) parser.parse(documentsString);
						JSONArray documentDataJson  = new JSONArray(((JSONObject) parser.parse(resultObj.get("response").toString())).get("data").toString());
						JSONArray arr7 = new JSONArray();
						for(int n=0;n<documentDataJson.length();n++){
							JSONObject json1 = (JSONObject) parser.parse(documentDataJson.get(n).toString());
							JSONObject json2 = new JSONObject();
							json2.put("Type", ((JSONObject)parser.parse(json1.get("documentcategory").toString())).get("name").toString().toUpperCase());
							json2.put("Filename", json1.get("name").toString());
							
							int size = Integer.parseInt(json1.get("file_size").toString());
							DecimalFormat dec = new DecimalFormat("0.0");
							double b = size;
							double kb = size/1024.0;
							double mb = size/1048576.0;
							double gb = size/1073741824.0;
							
							if ( gb>1 ) {
								json2.put("Size", dec.format(gb).concat(" GB"));
						    } else if ( mb>1 ) {
						    	json2.put("Size", dec.format(mb).concat(" MB"));
						    } else if ( kb>1 ) {
						    	json2.put("Size", dec.format(kb).concat(" KB"));
						    } else {
						    	json2.put("Size", dec.format(b).concat(" Bytes"));
						    }
							arr7.put(json2);
						}
						String documentData = arr7.toString();
						
						CustomModuleData bimView = new CustomModuleData();
						if(!assetDatas.isEmpty()){
							for(Entry<String,String> en:assetDatas.entrySet()){
								bimView.setDatum("thirdpartyid",Long.parseLong(thirdPartyId));
								bimView.setDatum("valuetype", en.getKey());
								bimView.setDatum("value", en.getValue());
								addOrupdateModuleData(bimView,thirdPartyId);
							}
						}
						
						bimView.setDatum("thirdpartyid",Long.parseLong(thirdPartyId));
						bimView.setDatum("valuetype", "typeData");
						bimView.setDatum("value", typeData);
						addOrupdateModuleData(bimView,thirdPartyId);
						
						if(!woDatas.isEmpty()){
							for(Entry<String,String> en:woDatas.entrySet()){
								bimView.setDatum("thirdpartyid",Long.parseLong(thirdPartyId));
								bimView.setDatum("valuetype", en.getKey());
								bimView.setDatum("value", en.getValue());
								addOrupdateModuleData(bimView,thirdPartyId);
							}
						}
						
						bimView.setDatum("thirdpartyid",Long.parseLong(thirdPartyId));
						bimView.setDatum("valuetype", "documentData");
						bimView.setDatum("value", documentData);
						addOrupdateModuleData(bimView,thirdPartyId);
						boolean addAsset = false;
						if(assetId > 0){
							AssetContext asset = AssetsAPI.getAssetInfo(assetId);
							
							if(asset.getCategory().getId() != assetCategory.getId()){
								AssetsAPI.deleteAsset(Collections.singletonList(assetId));
								asset.setId(-1);
								addAsset = true;
							}else{
								asset.setDatum("thirdpartyid", thirdPartyId);
								asset.setDatum("thirdpartytypeid", oldAssetCategoryId);
								asset.setDatum("2dviewid", viewIdJson.get("2d").toString());
								asset.setDatum("3dviewid", viewIdJson.get("3d").toString());
								asset.setName(assetName);
								asset.setDescription(description);
								asset.setSiteId(building.getSiteId());
								asset.setSpaceId(building.getId());
								asset.setCurrentSpaceId(building.getId());
								asset.setCategory(asset.getCategory());
								asset.setSerialNumber(serialNumber);
								asset.setManufacturer(manufacturer);
								asset.setModel(model);
								asset.setTagNumber(tag);
								updateAsset(asset,asset.getCategory().getModuleName());
							}
							
						}else{
							addAsset = true;
						}
						if(addAsset){
							AssetContext asset = new AssetContext();
							asset.setDatum("thirdpartyid", thirdPartyId);
							asset.setDatum("thirdpartytypeid", oldAssetCategoryId);
							asset.setDatum("2dviewid", viewIdJson.get("2d").toString());
							asset.setDatum("3dviewid", viewIdJson.get("3d").toString());
							asset.setName(assetName);
							asset.setDescription(description);
							asset.setSiteId(building.getSiteId());
							asset.setSpaceId(building.getId());
							asset.setCurrentSpaceId(building.getId());
							asset.setCategory(assetCategory);
							asset.setSerialNumber(serialNumber);
							asset.setManufacturer(manufacturer);
							asset.setModel(model);
							asset.setTagNumber(tag);
							addAsset(asset,moduleName);
						}
						
					}
				}
			}
		}
	}
	
	public List<CustomModuleData> getCustomModuleData(String bimValueType,String thirdPartyId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule bimViewModule = modBean.getModule("custom_bimview");
		List<FacilioField> bimViewFields = modBean.getAllFields("custom_bimview");
		Map<String,FacilioField> bimViewFieldsMap = FieldFactory.getAsMap(bimViewFields);
		
		SelectRecordsBuilder<CustomModuleData> builder = new SelectRecordsBuilder<CustomModuleData>()
				.select(bimViewFields).module(bimViewModule)
				.beanClass(CustomModuleData.class)
				.andCondition(CriteriaAPI.getCondition(bimViewFieldsMap.get("thirdpartyid"), thirdPartyId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(bimViewFieldsMap.get("valuetype"), bimValueType, StringOperators.CONTAINS))
				.orderBy("ID");
		return builder.get();
	}
	
	public void addOrupdateModuleData(CustomModuleData bimView,String thirdPartyId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule bimViewModule = modBean.getModule("custom_bimview");
		List<FacilioField> bimViewFields = modBean.getAllFields("custom_bimview");
		Map<String,FacilioField> bimViewFieldsMap = FieldFactory.getAsMap(bimViewFields);
		
		SelectRecordsBuilder<CustomModuleData> builder = new SelectRecordsBuilder<CustomModuleData>()
				.select(bimViewFields).module(bimViewModule)
				.beanClass(CustomModuleData.class)
				.andCondition(CriteriaAPI.getCondition(bimViewFieldsMap.get("thirdpartyid"), thirdPartyId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(bimViewFieldsMap.get("valuetype"), bimView.getDatum("valuetype").toString(), StringOperators.IS));
		CustomModuleData bimView1 = builder.fetchFirst();
		
		if(bimView1!=null){
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "custom_bimview");
			context.put(FacilioConstants.ContextNames.RECORD, bimView);
			bimView.parseFormData();
			
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(bimView1.getId()));
			
			FacilioChain updateModuleDataChain = FacilioChainFactory.updateModuleDataChain();
			updateModuleDataChain.execute(context);
		}else{
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "custom_bimview");
			context.put(FacilioConstants.ContextNames.RECORD, bimView);
			bimView.parseFormData();
			
			FacilioChain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
			addModuleDataChain.execute(context);
		}
		
		
	}
	
	
	public void importInvicaraAssets(ThirdParty thirdParty,String assetURL,String token) throws Exception {
		LOGGER.info("Inside importInvicaraAssets");	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
		SiteContext site = SpaceAPI.getSite(siteName);
		if(site != null){
			site.setName(siteName);
			updateSite(site);
		}else{
			site = new SiteContext();
			site.setName(siteName);
			addSite(site);
		}
		Long siteId = site.getId();
		
		BuildingContext building = SpaceAPI.getBuilding(buildingName);
		if(building != null){
			updateBuilding(building);
		}else{
			building = new BuildingContext();
			building.setName(buildingName);
			building.setSiteId(siteId);
			addBuilding(building);
		}
		Long buildingId = building.getId();
		LOGGER.info("Site Building added");	
		String assetsJsonString= getResponse(thirdParty,assetURL,token);

		JSONParser parser = new JSONParser();

		JSONObject resultObj = (JSONObject) parser.parse(assetsJsonString);
		JSONArray arr = new JSONArray(resultObj.get("_list").toString());
		LOGGER.info("Asset arr :: "+resultObj.get("_list").toString());	
		for(int i=0;i<arr.length();i++){

			JSONObject result = (JSONObject) parser.parse(arr.get(i).toString());
			String assetName= result.get("Asset Name").toString();
			
			String thirdPartyId= result.get("_id").toString();
			JSONObject properties=  (JSONObject) parser.parse(result.get("properties").toString());
			
			
			String category = ((JSONObject)parser.parse(properties.get("dtType").toString())).get("val").toString();
			
			String servingSpaceName = "";
			if(properties.containsKey("Containing Space Name")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("Containing Space Name").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						servingSpaceName =  snoObject.get("val").toString().substring(0, 48);
					}else{
						servingSpaceName =  snoObject.get("val").toString();
					}
				}
			}
			
			String servingSpaceNumber = "";
			if(properties.containsKey("Containing Space Number")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("Containing Space Number").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						servingSpaceNumber =  snoObject.get("val").toString().substring(0, 48);
					}else{
						servingSpaceNumber =  snoObject.get("val").toString();
					}
				}
			}
			
			String systemName = "";
			if(properties.containsKey("System Name")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("System Name").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						systemName =  snoObject.get("val").toString().substring(0, 48);
					}else{
						systemName =  snoObject.get("val").toString();
					}
				}
			}
			
			String systemClassification = "";
			if(properties.containsKey("System Classification")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("System Classification").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						systemClassification =  snoObject.get("val").toString().substring(0, 48);
					}else{
						systemClassification =  snoObject.get("val").toString();
					}
				}
			}
			
			String uniclass = "";
			if(properties.containsKey("Uniclass2015")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("Uniclass2015").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						uniclass =  snoObject.get("val").toString().substring(0, 48);
					}else{
						uniclass =  snoObject.get("val").toString();
					}
				}
			}
			
			String uniclassName = "";
			if(properties.containsKey("Uniclass2015 Name")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("Uniclass2015 Name").toString());
				if(snoObject.containsKey("val")){
					if(snoObject.get("val").toString().length()>48){
						uniclassName =  snoObject.get("val").toString().substring(0, 48);
					}else{
						uniclassName =  snoObject.get("val").toString();
					}
				}
			}
			
			String serialNumber = "";
			if(properties.containsKey("Serial Number")){
				JSONObject snoObject = (JSONObject)parser.parse(properties.get("Serial Number").toString());
				if(snoObject.containsKey("val")){
					serialNumber =  snoObject.get("val").toString();
				}
			}
			
			String tag="";
			
			if(properties.containsKey("Asset Tag")){
				tag =  ((JSONObject)parser.parse(properties.get("Asset Tag").toString())).get("val").toString();
			}
			
			String model = "";
			
			if(properties.containsKey("Model")){
				JSONObject modelObject = (JSONObject)parser.parse(properties.get("Model").toString());
				if(modelObject.containsKey("val")){
					model =  modelObject.get("val").toString();
				}
			}
			
			String description="";
			
			if(properties.containsKey("COBie Type Description")){
				description =  ((JSONObject)parser.parse(properties.get("COBie Type Description").toString())).get("val").toString();
			}
			
			String manufacturer="";
			
			if(properties.containsKey("Manufacturer")){
				manufacturer =  ((JSONObject)parser.parse(properties.get("Manufacturer").toString())).get("val").toString();
			}
			
			String moduleName = "";
			String categoryName = "";
			
			HashMap<String,String> assetCategoryMap = BimAPI.getThirdPartyAssetCategoryNames(thirdParty);
			
			if(assetCategoryMap.containsKey(category)) {
				categoryName = assetCategoryMap.get(category).toString();
				AssetCategoryContext assetCategory = AssetsAPI.getCategory(categoryName);

				if(assetCategory == null){
					assetCategory = AssetsAPI.getCategory(categoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
					if(assetCategory == null){
						assetCategory = new AssetCategoryContext();
						assetCategory.setName(categoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						assetCategory.setType(AssetCategoryType.MISC);
						FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
						FacilioContext context = addAssetCategoryChain.getContext();
						context.put(FacilioConstants.ContextNames.RECORD, assetCategory);
						addAssetCategoryChain.execute();	
					}
				}
				
				long assetId = AssetsAPI.getAssetId(assetName, AccountUtil.getCurrentOrg().getId());
				
				LOGGER.info("assetId :: "+assetId);	
				
				if(assetId > 0){
					AssetContext asset = AssetsAPI.getAssetInfo(assetId);
					asset.setDatum("thirdpartyid", thirdPartyId);
					asset.setDatum("servingspacename", servingSpaceName);
					asset.setDatum("servingspacenumber", servingSpaceNumber);
					asset.setDatum("systemname", systemName);
					asset.setDatum("systemclassification", systemClassification);
					asset.setDatum("uniclass", uniclass);
					asset.setDatum("uniclassname", uniclassName);
					asset.setDatum("runstatus",false);
					asset.setName(assetName);
					asset.setDescription(description);
					asset.setSiteId(siteId);
					asset.setSpaceId(buildingId);
					asset.setCurrentSpaceId(buildingId);
					asset.setManufacturer(manufacturer);
					asset.setSerialNumber(serialNumber);
					asset.setTagNumber(tag);
					asset.setModel(model);
					asset.setCategory(asset.getCategory());
					updateAsset(asset, asset.getCategory().getModuleName());
				}else{
					FacilioModule module = modBean.getModule(assetCategory.getAssetModuleID());
					moduleName = module.getName();
					
					AssetContext asset = new AssetContext();
					asset.setDatum("thirdpartyid", thirdPartyId);
					asset.setDatum("servingspacename", servingSpaceName);
					asset.setDatum("servingspacenumber", servingSpaceNumber);
					asset.setDatum("systemname", systemName);
					asset.setDatum("systemclassification", systemClassification);
					asset.setDatum("uniclass", uniclass);
					asset.setDatum("uniclassname", uniclassName);
					asset.setDatum("runstatus",false);
					asset.setName(assetName);
					asset.setDescription(description);
					asset.setSiteId(siteId);
					asset.setSpaceId(buildingId);
					asset.setCurrentSpaceId(buildingId);
					asset.setManufacturer(manufacturer);
					asset.setSerialNumber(serialNumber);
					asset.setTagNumber(tag);
					asset.setModel(model);
					asset.setCategory(assetCategory);
					addAsset(asset,moduleName);
				}
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
	
	public String getStatusAndSeverityList() throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioStatus> assetTicketStatusList = TicketAPI.getStatuses(module,null);
		setResult("assetTicketStatusList",assetTicketStatusList);
		List<FacilioStatus> workorderTicketStatusList = WorkOrderAPI.getWorkorderTicketStatusList();
		setResult("workorderTicketStatusList", workorderTicketStatusList);
		List<AlarmSeverityContext> alarmSeverityList = AlarmAPI.getAlarmSeverityList();
		setResult("alarmSeverityList", alarmSeverityList);
		return SUCCESS;
	}
	
	private long bimAssetId;
	
	public long getBimAssetId() {
		return bimAssetId;
	}

	public void setBimAssetId(long bimAssetId) {
		this.bimAssetId = bimAssetId;
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
	
	private String assetURL;
	private String userName;
	private String password;
	private String siteName;
	private String buildingName;

	public String getAssetURL() {
		return assetURL;
	}

	public void setAssetURL(String assetURL) {
		this.assetURL = assetURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	
}
