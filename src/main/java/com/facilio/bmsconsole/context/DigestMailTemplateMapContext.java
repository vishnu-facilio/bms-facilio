package com.facilio.bmsconsole.context;

public class DigestMailTemplateMapContext {

	private int defaultWorkFlowId;
	private int frequencyType;
	private int templateType;
	private String templateName;
	private int defaultTemplateId;
	public int getDefaultWorkFlowId() {
		return defaultWorkFlowId;
	}
	public void setDefaultWorkFlowId(int defaultWorkFlowId) {
		this.defaultWorkFlowId = defaultWorkFlowId;
	}
	public int getFrequencyType() {
		return frequencyType;
	}
	public void setFrequencyType(int frequencyType) {
		this.frequencyType = frequencyType;
	}
	public int getTemplateType() {
		return templateType;
	}
	public void setTemplateType(int templateType) {
		this.templateType = templateType;
	}
	
	public int getDefaultTemplateId() {
		return defaultTemplateId;
	}
	public void setDefaultTemplateId(int defaultTemplateId) {
		this.defaultTemplateId = defaultTemplateId;
	}
	public DigestMailTemplateMapContext() {
		
	}
	
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public DigestMailTemplateMapContext(int defaultWorkFlowId, int frequencyType, int templateType, String templateName,
			int defaultTemplateId) {
		super();
		this.defaultWorkFlowId = defaultWorkFlowId;
		this.frequencyType = frequencyType;
		this.templateType = templateType;
		this.templateName = templateName;
		this.defaultTemplateId = defaultTemplateId;
	}
	
	
}
