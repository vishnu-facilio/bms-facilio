package com.facilio.bmsconsole.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
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
		setResult(FacilioConstants.ContextNames.VALID_SHEETS ,(JSONArray)context.get(FacilioConstants.ContextNames.VALID_SHEETS));
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
public static synchronized HttpURLConnection getHttpURLConnection (String requestURL) throws Exception{
		

		URL request = new URL(requestURL.trim());
		HttpURLConnection connection = (HttpURLConnection) request.openConnection();

		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
//		connection.setRequestProperty("Authorization:", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1ODU2NzcwNzYsInVzZXJfbmFtZSI6IjkzYmY4NmUzLWM0N2ItNDk5NC1iN2E4LTg3ZDUwMWIxZjNkMiIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJiNzZhOWE4Zi03OGM4LTQwMzAtYTM2ZC1mM2RhMzA0OGNhZTciLCJjbGllbnRfaWQiOiJmYWNpbGlvLWFwaSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.AGvyrHevpWz5J543-eNeYGMrqFCVY8BY01C6DNGupMM");
		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);
		return connection;
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

public static String getResponse (HttpURLConnection connection) throws Exception {

	String response = "";

	try {
		
		connection.connect();
		String responseTime = connection.getHeaderField("X-Response-Time");
		System.err.println("response Time: "+responseTime);
		
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

	public String addAssetsJSONImport() throws Exception {
		
//		HttpURLConnection connection= getHttpURLConnection("https://facilio-int.invicara.com/platformapisvc/api/v0.9/assets?nsfilter=5psdproj_O4Z2hIL9&page={\"_pageSize\":800,\"_offset\":0 }&dtCategory=[\"HVAC Air Handling Unit\", \"HVAC Chiller Unit\",\"HVAC Cooling Tower\",\"HVAC Damper\",\"HVAC Valve\",\"HVAC Pump\"]");
//		String hvacStr= getResponse(connection);
//		System.out.println("res ::::::: "+hvacStr);
//		String home = System.getProperty("user.home");
//		
        String hvacStr= FileUtils.readFileToString(fileUpload, "UTF-8");
		

		JSONObject resultObj = new JSONObject(hvacStr);
		JSONArray arr = new JSONArray(resultObj.get("_list").toString());
		
		for(int i=0;i<arr.length();i++){

			JSONObject result = (JSONObject) arr.get(i);
			String assetName= result.get("Asset Name").toString();
			JSONObject properties=  new JSONObject(result.get("properties").toString());
			List<SiteContext> sites = SpaceAPI.getAllSites();
			Long siteId = sites.get(0).getId();
			
			String category = new JSONObject(properties.get("dtCategory").toString()).get("val").toString();
			JSONObject snoObject = new JSONObject(properties.get("Serial Number").toString());
			
			String serialNumber = "";
			if(snoObject.has("val")){
				serialNumber =  snoObject.get("val").toString();
			}
			
			String tag=  new JSONObject(properties.get("Asset Tag").toString()).get("val").toString();
			JSONObject modelObject = new JSONObject(properties.get("Model").toString());
			String model = "";
			if(modelObject.has("val")){
				model =  modelObject.get("val").toString();
			}
			String description=  new JSONObject(properties.get("COBie Type Description").toString()).get("val").toString();
			String manufacturer=  new JSONObject(properties.get("Manufacturer").toString()).get("val").toString();
			String moduleName = "";
			String categoryName = "";
			AssetCategoryContext assetCategory = new AssetCategoryContext();
			
			if(category.equals("HVAC Air Handling Unit")){
				categoryName = "AHU";
			}else if(category.equals("HVAC Chiller Unit")){
				categoryName = "Chiller";
			}else if(category.equals("HVAC Cooling Tower")){
				categoryName = "Cooling Tower";
			}else if(category.equals("HVAC Damper")){
				categoryName = "damper";
			}else if(category.equals("HVAC Valve")){
				categoryName = "valve";
			}else if(category.equals("HVAC Pump")){
				categoryName = "pump";
			}
			
			assetCategory = AssetsAPI.getCategory(categoryName);
			moduleName = assetCategory.getModuleName();
			
			AssetContext asset = new AssetContext();
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
		
		return SUCCESS;
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
