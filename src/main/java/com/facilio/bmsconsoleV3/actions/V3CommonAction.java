package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Filters;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Action;

public class V3CommonAction extends V3Action {
	
	private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

	public String fetchPlaceholderFields() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getPlaceholderFields();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
		chain.execute();

		setData("placeholders", context.get(Filters.PLACEHOLDER_FIELDS));

		return SUCCESS;
	}

}
