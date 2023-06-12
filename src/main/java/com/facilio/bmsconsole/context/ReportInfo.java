package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.services.email.EmailClient;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.JobContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportInfo {
	
	public static final String PARENT_VIEW_NAME = "report";
	
	private long id = -1;	// Scheduled id
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private String name;	// Report name
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private FileFormat fileFormat;
	public int getFileFormat() {
		if (fileFormat != null) {
			return fileFormat.getIntVal();
		}
		return -1;
	}
	public void setFileFormat(int format) {
		this.fileFormat = FileFormat.getFileFormat(format);
	}
	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}
	public FileFormat getFileFormatEnum() {
		return fileFormat;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
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

	private Map<String, Object> viewObj;
	public Map<String, Object> getViewObj() {
		return viewObj;
	}

	public void setViewObj(Map<String, Object> viewObj) {
		this.viewObj = viewObj;
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
		if (emailTemplate != null) {
			emailTemplate.setName("Report");
			emailTemplate.setFrom(EmailClient.getFromEmail("report"));
		}
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
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	private JobContext job;
	public JobContext getJob() {
		return job;
	}
	public void setJob(JobContext job) {
		this.job = job;
	}

	public List<Long> getSelected_reportIds() {
		return selected_reportIds;
	}

	public void setSelected_reportIds(List<Long> selected_reportIds) {
		this.selected_reportIds = selected_reportIds;
	}

	public List<Long> selected_reportIds = new ArrayList<>();

	public String getScheduled_report_name() {
		return scheduled_report_name;
	}

	public void setScheduled_report_name(String scheduled_report_name) {
		this.scheduled_report_name = scheduled_report_name;
	}

	public String scheduled_report_name;
}
