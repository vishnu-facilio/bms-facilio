package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetConnectedAppViewURLCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectedAppContext connectedApp = (ConnectedAppContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP);
		ConnectedAppWidgetContext connectedAppWidget = (ConnectedAppWidgetContext) context.get(FacilioConstants.ContextNames.CONNECTED_APP_WIDGET);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Boolean isSandbox = (Boolean) context.get(FacilioConstants.ContextNames.SANDBOX_MODE);
		
		String viewURL = null;
		
		String baseUrl = connectedApp.getProductionBaseUrl();
		if (isSandbox != null && isSandbox == true) {
			baseUrl = connectedApp.getSandBoxBaseUrl();
		}
		
		if (connectedAppWidget != null) {
			viewURL = baseUrl + connectedAppWidget.getResourcePath();
			// if (connectedAppWidget.getEntityTypeEnum() == ConnectedAppWidgetContext.EntityType.SUMMARY_PAGE && recordId != null && recordId > 0) {
			// 	viewURL = replacePlaceholders(viewURL, connectedAppWidget.getEntityId(), recordId, connectedAppWidget.getCriteria());
			// }
		}
		else if (connectedApp != null) {
			viewURL = baseUrl;
			if (connectedApp.getStartUrl() != null && !connectedApp.getStartUrl().trim().isEmpty()) {
				viewURL = viewURL + connectedApp.getStartUrl();
			}
		}
		
		context.put(FacilioConstants.ContextNames.CONNECTED_APP_VIEW_URL, viewURL);
		
		return false;
	}
	
	private String replacePlaceholders(String url, long moduleId, long recordId, Criteria criteria) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.beanClass(ModuleBaseWithCustomFields.class)
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(recordId, module))
				;

		List<ModuleBaseWithCustomFields> records = builder.get();
		if (records.size() > 0) {
		
			ModuleBaseWithCustomFields record = records.get(0);
			
			for (FacilioField field : fields) {
				String placeholder = "${" + field.getName() + "}";
				Object value = record.getDatum(field.getName());
				if (value == null) {
					value = "";
				}
				url = url.replace(placeholder, value.toString());
			}
			url = url.replace("${id}", record.getId() + "");
		}
		return url;
	}
}