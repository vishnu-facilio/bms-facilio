package com.facilio.bmsconsole.templates;

import com.facilio.bmsconsole.modules.FieldUtil;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="workflows")
public class DefaultTemplateWorkflowsConf {
	
	private List<TemplateWorkflowConf> defaultTemplatesWorkflows;
	
	@XmlElement(name="template")
	public List<TemplateWorkflowConf> getDefaultTemplatesWorkflows() {
		return defaultTemplatesWorkflows;
	}
	public void setDefaultTemplatesWorkflows(List<TemplateWorkflowConf> defaultTemplatesWorkflows) {
		this.defaultTemplatesWorkflows = defaultTemplatesWorkflows;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return defaultTemplatesWorkflows.toString();
	}
	
	@XmlRootElement(name="template")
	public static class TemplateWorkflowConf {
		private int id = -1;
		
		@XmlAttribute(name="id")
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
		private Element workflow;
		
		@XmlAnyElement
		public Element getWorkflow() {
			return workflow;
		}
		public void setWorkflow(Element workflow) {
			this.workflow = workflow;
			workflowXml = FieldUtil.getMapper(String.class).convertValue(workflow, String.class);
		}
		
		private String workflowXml;
		public String getWorkflowXml() {
			return workflowXml;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			try {
				return workflowXml;
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			return null;
		}
	}
}
