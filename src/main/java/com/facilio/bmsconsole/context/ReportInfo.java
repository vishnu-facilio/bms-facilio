package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.pdf.PdfUtil;
import com.facilio.tasker.executor.ScheduleInfo;

public class ReportInfo {
	
	public static final String PARENT_VIEW_NAME = "report";
	
	private ReportContext reportContext;
	public ReportContext getReportContext() {
		return reportContext;
	}
	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public FacilioModule getModule() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		return modBean.getModule(moduleName);
	}
	
	private FacilioView view;
	public FacilioView getView() {
		return view;
	}
	public void setView(FacilioView facilioView) {
		this.view = facilioView;
	}

	String dateFilter;
	public String getDateFilter() {
		return dateFilter;
	}
	
	public void setDateFilter(String dateFilter) {
		this.dateFilter = dateFilter;
	}
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	private ScheduleInfo scheduleInfo;
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	
	private long startTime;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public String getFileUrl(List<ModuleBaseWithCustomFields> records) throws Exception {
		String fileUrl = null;
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		if(fileFormat == FileFormat.PDF) {
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(), "/app/em/reports/view/"+reportContext.getId());
		}
		else {
			fileUrl = ExportUtil.exportData(fileFormat, getModule(), view.getFields(), records);
		}
		return fileUrl;
	}

}
