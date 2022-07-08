package com.facilio.bmsconsoleV3.actions;

import java.util.Map;

import org.json.simple.parser.ParseException;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter @Setter
@Log4j
public class V3PageAction extends V3Action {
	
	private String moduleName;
    private long id = -1;
	private long widgetId = -1;
	private long formId = -1;
	private Map<String, Object> widgetParams;
	private boolean fetchMainFields;
	
	public String fetchSummaryFields() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getSummaryFieldsCommand();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.ID, id);
		context.put(ContextNames.FORM_ID, formId);
		context.put(ContextNames.WIDGET_ID, widgetId);
		context.put(ContextNames.FETCH_LOOKUPS, true);
		context.put(ContextNames.FETCH_MAIN_FIELDS, fetchMainFields);
		context.put(FacilioConstants.ContextNames.WIDGET_PARAMJSON, widgetParams); // Remove once page db support
		chain.execute();
		
		setData("fields", context.get("fields"));
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public void setWidgetParams(String params) {
		if (params != null) {
			try {
				widgetParams = FacilioUtil.parseJson(params);
			} catch (ParseException e) {
				LOGGER.error("Error while parsing widget params", e);
			}
		}
	}

}
