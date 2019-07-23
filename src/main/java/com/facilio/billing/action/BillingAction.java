package com.facilio.billing.action;

import java.io.File;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.billing.context.BillContext;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioContext;
import com.opensymphony.xwork2.ActionSupport;

public class BillingAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String GenerateTemplate() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(BillContext.ContextNames.FILE, getExcelFile());
		context.put(BillContext.ContextNames.FILENAME, getExcelFileFileName());
		context.put(BillContext.ContextNames.TEMPLATENAME, getTemplateName());
		context.put(BillContext.ContextNames.CONTENTTYPE, getExcelFileContentType());
		Chain handleExcelFileUploadChain = BillContext.HandleExcelFileUploadChain();
		handleExcelFileUploadChain.execute(context);
		ExcelTemplate excelTemplate = (ExcelTemplate) context.get(BillContext.ContextNames.EXCELOBJECT);
		setExcelTemplate(excelTemplate);
		return SUCCESS;
	}
	
	public String LoadTemplates() throws Exception
	{
		List<ExcelTemplate> excelTemplates = TemplateAPI.getAllExcelTemplates();
		setExcelTemplates(excelTemplates);
		return SUCCESS;
	}
	
	public String GenerateBill() throws Exception
	{
		FacilioContext context = new FacilioContext();	
		context.put(BillContext.ContextNames.STARTTIME,getStartTime());
		context.put(BillContext.ContextNames.ENDTIME,getEndTime());
		context.put(BillContext.ContextNames.TEMPLATEID,getTemplateId());
		Chain handleBillGenerationChain = BillContext.HandleBillGenerationChain();
		handleBillGenerationChain.execute(context);
		setDownloadURL((String)context.get(BillContext.ContextNames.EXCEL_FILE_DOWNLOAD_URL));
		return SUCCESS;
	}
	
	long startTime;
	long endTime;
	long templateId;
	
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	String downloadURL;
	
	public String getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
	
	private String templateName;
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	private File excelFile;
	public File getExcelFile() {
		return excelFile;
	}
	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}
	
	private String excelFileFileName;
	
	private String excelFileContentType;
	
	public String getExcelFileFileName() {
		return excelFileFileName;
	}
	public void setExcelFileFileName(String excelFileFileName) {
		this.excelFileFileName = excelFileFileName;
	}
	public String getExcelFileContentType() {
		return excelFileContentType;
	}
	public void setExcelFileContentType(String excelFileContentType) {
		this.excelFileContentType = excelFileContentType;
	}
	
	ExcelTemplate excelTemplate;
	
	
	public ExcelTemplate getExcelTemplate() {
		return excelTemplate;
	}

	public void setExcelTemplate(ExcelTemplate excelTemplate) {
		this.excelTemplate = excelTemplate;
	}

	List<ExcelTemplate> excelTemplates;

	public List<ExcelTemplate> getExcelTemplates() {
		return excelTemplates;
	}

	public void setExcelTemplates(List<ExcelTemplate> excelTemplates) {
		this.excelTemplates = excelTemplates;
	}
	
}
