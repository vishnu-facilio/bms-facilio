package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.BeanFactory;

public class ReadingRuleMetricContext {
	
	long id = -1l;
	long orgId = -1l;
	long readingRuleId = -1l;
	long fieldId = -1l;
	long resourceId = -1l;

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	FacilioField field;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getReadingRuleId() {
		return readingRuleId;
	}

	public void setReadingRuleId(long readingRuleId) {
		this.readingRuleId = readingRuleId;
	}

	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public FacilioField getField() throws Exception {
		
		if(field == null && fieldId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			setField(modBean.getField(fieldId));
		}
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
	}
}
