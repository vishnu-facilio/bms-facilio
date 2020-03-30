package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
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
