package com.facilio.billing.action;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.*;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.billing.context.BillContext;

public class BillingAction extends ActionSupport {
	
	public String GenerateTemplate() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(BillContext.ContextNames.FILE, getExcelFile());
		context.put(BillContext.ContextNames.FILENAME, getExcelFileFileName());
		context.put(BillContext.ContextNames.TEMPLATENAME, getTemplateName());
		context.put(BillContext.ContextNames.CONTENTTYPE, getExcelFileContentType());
		Chain handleExcelFileUploadChain = BillContext.HandleExcelFileUploadChain();
		handleExcelFileUploadChain.execute(context);
		System.out.println("##### Generate Template End Request");
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
	
	List<ExcelTemplate> excelTemplates;

	public List<ExcelTemplate> getExcelTemplates() {
		return excelTemplates;
	}

	public void setExcelTemplates(List<ExcelTemplate> excelTemplates) {
		this.excelTemplates = excelTemplates;
	}
	
}
