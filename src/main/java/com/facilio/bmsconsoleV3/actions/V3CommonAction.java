package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.placeholder.enums.PlaceholderSourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Filters;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3CommonAction extends V3Action {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String moduleName;
    private long formId;
    
    private PlaceholderSourceType placeholderSourceType;
	public void setPlaceholderSourceType(int type) {
		if (type != -1) {
			placeholderSourceType = PlaceholderSourceType.valueOf(type);
		}
	}

	public String fetchPlaceholderFields() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getPlaceholderFields(formId > 0);
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		placeholderSourceType = formId > 0 ? PlaceholderSourceType.FORM_RULE : PlaceholderSourceType.SCRIPT;
		context.put(FacilioConstants.ContextNames.SOURCE_TYPE, placeholderSourceType);

		context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
		chain.execute();

		setData("placeholders", context.get(Filters.PLACEHOLDER_FIELDS));

		return SUCCESS;
	}

}
