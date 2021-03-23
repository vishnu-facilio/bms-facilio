package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.v3.V3Action;

public class V3PageAction extends V3Action {
	
	private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long widgetId = -1;
	public long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(long widgetId) {
		this.widgetId = widgetId;
	}
	
	private long formId = -1;
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	public String fetchSummaryFields() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getSummaryFieldsCommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.ID, id);
		context.put(ContextNames.FORM_ID, formId);
		context.put(ContextNames.WIDGET_ID, widgetId);
		chain.execute();
		
		setData("fields", context.get("fields"));
		
		return SUCCESS;
	}

}
